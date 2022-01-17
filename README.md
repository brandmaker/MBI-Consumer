<img align="right" src="https://github.com/brandmaker/MBI-Consumer/blob/f7a4fc69e2bea28d909215df626b55e020fbaffa/BrandMaker_Logo_on_light_bg.png" alt="BrandMaker" width="30%" height="30%">


# BrandMaker MBI Consumer Example

Example implementation of a consumer for the BrandMaker Message Based Integration service (MBI).
For details on MBI, please refer to [https://developers.brandmaker.com/](https://developers.brandmaker.com/)


## Usage

* Clone this project
* Build with maven (`mvn -B package`)
* Start the application (`java -jar target/mbiconsumer-example-...jar`)
* Register the application within your BrandMaker instance (Administration / Fusion / Integration)
* Alternatively, you may check the swagger documentation on `https://<your server>:8080/api-docs.html` and issue requests against the demo example from over there

## Short Description

This "consumer" is based on spring-boot. It establishes a REST service on the URL `https://<your server>:8080/hook`, listening for incoming MBI POST requests. If the request is 
an MBI event message, the message is pushed into an internal JMS queue for asynchroneous processing.

From that queue, the message is picked up by a queue listener and then processed. "Processing" means: the request data is stored in the local working directory 
under `requests/<customer id>/<system id>/event_<timestamp>.json`

This is just an example to show how events from MBI can be received and safely processed. It actually lacks of any "intelligent" business logic other than just 
dumping the request to the filesystem.


## API documentation

Under the link `https://<your server>:8080/api-docs.html` there is an API documentaion of this WebHook. Be aware that this is currently the API of *this consumer* and not any 
API of BrandMaker MBI. Once you try this via the swagger UI, you may therefor simulate the submission of an MBI event. This is just for testing this example consumer.
Furthermore, the API of this consumer is based on the requirements to a BrandMaker MBI consumer as described on [https://developers.brandmaker.com/](https://developers.brandmaker.com/).


## Project state

[![Java CI with Maven](https://github.com/brandmaker/MBI-Consumer/actions/workflows/maven.yml/badge.svg)](https://github.com/brandmaker/MBI-Consumer/actions/workflows/maven.yml)

# Further Information

## Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.3.0.M3/maven-plugin/html/)
