package adapter.controllers;

import adapter.messages.MsgA;
import adapter.messages.MsgB;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

/**
 * The core Class implementing the work of the router adapter
 *
 *      The concept consists in handling and enrichment of messages received from
 *      a external service "A" to the input point of our adapter "http:0.0.0.0:8989/adapter" and sending them
 *      to a external service "B".
 *
 *      Also all received messages are filtered and validated with sending the relevant
 *      response message and status code back to the "A" service.
 *
 *      Messages are enriched by receiving weather data from a external weather API service "api.openweathermap.org".
 *      Such choice is based on the existing special integrated Camel Core component
 *      using directly this API service and simplifying data receiving.
 *
 *      To receive weather data from the API we use latitude and longitude values
 *      from message received from service "A".
 *
 *      Next, the received weather data, timestamp and some text message, received also from the service A,
 *      adapter transmits to a external service B.
 *      Any errors received during data transmission are routed and displayed to service A.
 *
 *
 * @author Nikita Filimonov
 */
@Component
public class AdapterController extends RouteBuilder{


    @Autowired
    CamelContext camelContext = new DefaultCamelContext();


    /**
     * "configure()" method of the "RouterBuilder" class contains the primary route definition
     * and its configuration settings.
     */
    @Override
    public void configure() {

        /* Define the data format used for JSON and set the class for unmarshalling. */
        JsonDataFormat json = new JsonDataFormat(JsonLibrary.Jackson);
        json.setUnmarshalType(MsgA.class);


        /* Now install the necessary component for the REST DSL component and configure the settings. */
        restConfiguration().component("jetty").port(8989).clientRequestValidation(true);

        /* Set the entry point */
        rest("/adapter")
                .post()                                                                 // Set the request method
                .route()                                                                // Signify a route
                .unmarshal(json)                                                        // Convert the body data into our POJO model
                .doTry()
                    .to("bean-validator:validator")                                     // Send data to validation
                .doCatch(BeanValidationException.class)                                 // Set up the handling of possible resulting errors
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                    .transform(exceptionMessage())
                    .stop()
                    .end()
                .filter(simple("${body.lng} == 'ru'"))                             // Filtering data
                .setProperty("lon", simple("${body.longitude}"))                   // Save latitude and longitude values
                .setProperty("lat", simple("${body.latitude}"))
                .marshal().json()                                                       // Convert body to JSON
                .to("direct:inbox")                                                     // Sending request data to a next routing section.
                .setBody(simple("${null}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))             // Set the response code and message to service A.
                .end();

        from("direct:inbox")
                .doTry()
                    .enrich("direct:resource", (original, resource) -> {       // Enrich the data via the Weather API in route direct:resource

                        MsgB output = new MsgB();                                       // POJO model of our further result message

                        String jsonOrig = original.getIn().getBody(String.class);
                        String jsonRes = resource.getIn().getBody(String.class);

                        ReadContext contextOrig = JsonPath.parse(jsonOrig);             // Get data from the JSON body of response
                        ReadContext contextRes = JsonPath.parse(jsonRes);               // from the weather API via JSONPath
                                                                                        //  and put it into POJO output model
                        String msg = contextOrig.read("$.msg");
                        Double tempD = contextRes.read("$.main.temp");
                        Integer temp = Math.toIntExact(Math.round(tempD));

                        output.setTxt(msg);
                        output.setCurrentTemp(temp);
                        output.setCreatedDt(new Date());

                        if (original.getPattern().isOutCapable()) {
                            original.getOut().setBody(output);
                        } else {
                            original.getIn().setBody(output);
                        }

                        return original;
                    })
                .doCatch(Exception.class)                                               // Set up the handling of possible resulting errors
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                    .transform(simple("Weather API connection error"))
                    .stop()
                    .end()
                .marshal().json()                                                       // Again convert body in POJO to JSON
                .doTry()
                    .to("mock:result").log("${body}")                                   // Send a message to an external service
                .doCatch(ResponseStatusException.class, Exception.class)                // Set up the handling of possible resulting errors
                    .process(exchange -> {

                        /* Receive an exception model and set a response message based on the error received. */
                        Exception exc = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                        if(exc instanceof HttpOperationFailedException) {
                            HttpOperationFailedException httpOperationFailedException = (HttpOperationFailedException) exc;
                            int responseCode = httpOperationFailedException.getStatusCode();
                            exchange.getIn().setBody(exc.getMessage());
                            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, responseCode);
                        } else {
                            exchange.getIn().setBody(exc.getMessage());
                            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 520);
                        }
                    })
                    .stop()
                    .end()
                .end();

        /* The route of sending latitude and longitude values to the weather API */
        from("direct:resource")
                .log("${body}")
                .toD("weather:?lat=${exchangeProperty[lat]}" +
                        "&lon=${exchangeProperty[lon]}" +
                        "&appid={{weather.token}}" +
                        "&units=metric" +
                        "&bridgeErrorHandler=true")
                .end();
    }
}