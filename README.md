[![Gitpod Ready-to-Code](https://img.shields.io/badge/Gitpod-Ready--to--Code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/llyorshch/money-loan-demo) 

# Money loan demo

Spring Boot Flowable application with Elasticsearch integration and HTTP tasks.

The application needs two things:

* Elasticsearch running in port 9200
* The included json-server

To run the application:

```bash
./mvnw clean spring-boot:run
```

To test the application, you can use the included Postman collection.  There you will see how to use the REST endpoints.

In the `flowable-app` folder you can find a Flowable Application that you can import in the Modeler. You can import the form and the process independently if you prefer.

