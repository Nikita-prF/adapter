# Web-route Adapter

Web adapter, provided by the [Apache Camel](https://camel.apache.org/) and [Spring Boot](https://spring.io/) frameworks,
which are used as a router between two external REST services.

## Description

The concept consists in handling and enrichment of messages received from an external service "A"
to the input point of our adapter `http:0.0.0.0:8989/adapter` and sending them to an external service "B".

Also, all received messages are filtered and validated with sending the relevant
response message and status code back to the "A" service.

Messages are enriched by receiving weather data from an external weather API service [api.openweathermap.org](https://openweathermap.org/).
Such choice is based on the existing special integrated [Camel Core component](https://camel.apache.org/components/latest/weather-component.html)
using directly this API service and simplifying data receiving.

To receive weather data from the API we use latitude and longitude values
from a message received from service "A".

Next, the received weather data, timestamp and some text message, received also from the service A,
adapter transmits to an external service B.
Any errors received during data transmission are routed and displayed to service A.

## Launch Manual

*To launch the program you should have installed JRE on your computer. Please install it if there are no exists.*

* Clone repository to your machine
* Open the root folder of the project
* Run the following command

`$ java -jar build/libs/*.jar` or `$ gradle bootRun`

* Wait until finished loading

>Do not close the terminal - it will cause the shutdown of the service.

To check the adapter, use the [Postman](https://www.postman.com/) service as a simulation of the service requests to router.

Send a POST request with a body to `http:0.0.0.0:8989/adapter` and follow the router log.

Json body Example:
```json
{
  "msg": "Привет",
  "lng": "ru",
  "coordinates":
  {
    "latitude": "54.35",
    "longitude": "52.52"
  }
}
```

Example of router log info:
```
INFO 56808 --- [tp2136347897-37] route2       : {"msg":"Привет","lng":"ru","coordinates":{"latitude":"54.35","longitude":"52.52"},"latitude":"54.35","longitude":"52.52"}
INFO 56808 --- [tp2136347897-37] route1       : {"txt":"Привет","createdDt":"2020-09-26 02:58 AM UTC","currentTemp":8}
```

Also, receiving the 200 OK response code indicates that the message was successfully transmitted to the endpoint.
In case of any error, you will receive the relevant error code.

To replace the current dummy endpoint with the real service address, replace:
```java
//some code                                                      
.doTry()
    .to("mock:result")                                  
//some code
```
with

```java
 //some code                                                      
 .doTry()
     .to("http:someservice.org")                                  
 //some code
```

## Built with

* [Apache Camel](https://camel.apache.org/)
* [Spring Boot](https://spring.io/)

## Authors

[Nikita-prF](https://github.com/Nikita-prF)

