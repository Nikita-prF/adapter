//package adapter.controllers;
//
//import org.apache.camel.ProducerTemplate;
//import org.apache.camel.builder.AdviceWithRouteBuilder;
//import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.component.mock.MockEndpoint;
//import org.apache.camel.model.ModelCamelContext;
//import org.apache.camel.model.RouteDefinition;
//import org.apache.camel.test.spring.CamelSpringBootRunner;
//import org.apache.camel.test.spring.DisableJmx;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.junit4.SpringRunner;
//
//
//@DirtiesContext
//@RunWith(SpringRunner.class)
//@EnableAutoConfiguration
//@DisableJmx
//@SpringBootTest(
//        classes = {
//                CamelTest.class,
//                CamelTest.TestConfiguration.class
//        },
//        properties = {
//                "debug=true",
//                "camel.rest.enabled=true",
//                "camel.rest.component=jetty",
//                "camel.rest.host=0.0.0.0",
//                "camel.rest.data-format-property.prettyPrint=true",
//                "camel.rest.bindingMode=auto",
//                "typeConverterStatisticsEnabled=true"
//        }
//)
//public class CamelTest {
//
//    @Autowired
//    private ModelCamelContext context;
//
//    @Test
//    public void test() throws Exception {
//        ProducerTemplate template = context.createProducerTemplate();
//        RouteDefinition definition = context.getRouteDefinition("api-route");
//        context.adviceWith(definition, new AdviceWithRouteBuilder() {
//            @Override
//            public void configure() throws Exception {
//                replaceFromWith("direct:start");
//            }
//        });
//        MockEndpoint endpoint = (MockEndpoint) context.getEndpoint("mock:result");
//
//        endpoint.expectedMessageCount(0);
//
//        template.sendBody("direct:start", "test");
//
//        endpoint.assertIsSatisfied();
//
//
//    }
//
//    @Configuration
//    public static class TestConfiguration {
//
//        @Bean
//        public RouteBuilder routeBuilder() {
//            return new AdapterController();
//        }
//    }
//}