# Flux Capacitor

This repository contains a ReST service that converts timestamps from
one time zone to another and calculates the offset between time zones.

This service is useful for environments that don't have access to the
zone info database, like older versions of Smalltalk, etc.

This project is a Spring Boot application using the Maven build environment.

## Features
### Web MVC Starter
The Spring Boot web MVC starter dependency is used to jump-start the ReST
service. It includes an embedded Tomcat container, so when the main application
is started, the endpoints can be accessed at **localhost:8080/app/***
### Developer Tools
The Spring Boot DevTools dependency provides quick application restart and
live reloading during development.
### Web MVC Test Starter and JUnit 5
The Spring Boot web MVC test starter dependency allows unit tests to get
access to the Spring context, handy for testing Spring services. This project's
tests use JUnit 5.
### SpringDoc Starter
The SpringDoc OpenAPI / Swagger documentation dependency scans the project when the
application starts up and adds the URLs below documenting the endpoints of the ReST service.
[This link](http://localhost:8080/swagger-ui/index.html) shows the Swagger UI page
and can also be used to experiment with the endpoints. Postman can also be used to
exercise the service. The OpenAPI v3 JSON docs are also available
at [this link](http://localhost:8080/v3/api-docs).
If you like YAML, use [this link](http://localhost:8080/v3/api-docs.yaml).
## TODO
- Add Swagger @Schema annotations for more descriptive documentation.
- Better general exception handling at the ReST controller layer.
