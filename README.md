# RAPID Client Integration
[![Build Status](https://travis-ci.org/packleader/rapid-client-integration.svg?branch=master)](https://travis-ci.org/packleader/rapid-client-integration)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.packleader/rapid-client-integration/badge.svg)](https://mvnrepository.com/artifact/com.github.packleader/rapid-client-integration)

**Simplify the process of adoption so your clients can focus on delivering awesome!**

## Table Of Contents
- [Overview](#overview)
- [Features](#features)
- [How It Works](#how-it-works)
- [Usage](#usage)
- [Multi-Language Support](#multi-language-support)
- [Tips & Troubleshooting](#tips--troubleshooting)

## Overview
The RESTful API Distribution maven plugin enables clients to integrate with your service quickly and seamlessly by providing them with a client API.  The client API contains DTO's for all of your service's request and response objects and a simplified interface that allows the client to interact with your service using simple method calls.  No longer do your clients need to generate their own DTO's from XSD's or sample JSON; gone are the days of building query strings and checking HTTP response codes.  The RAPID client API handles all of these boilerplate tasks for your client and frees them up to focus on leveraging your service to deliver awesome products.

### Before RAPID
Here's what it might look like to call the [RAPID Store](examples/rapid-store) example service using a Jersey client.  Note that the client has to build the URL string with path and query parameters, set the media type, and call the correct HTTP method.  Although not shown here, the developer would also have to create the Customer and Order objects.
```java
    public List<Order> getOrdersForCustomer(Customer customer, Order.StatusEnum status)
            throws WebApplicationException {
        final String path = "http://localhost:8080/" +
                "v1/customers/" +
                customer.getId() +
                "/orders?status=" +
                ((status == null) ? null : status.toString());

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(path);
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE);

        Order[] orders;
        orders = invocationBuilder.get(Order[].class);

        return Arrays.asList(orders);
    }
```

### Using RAPID
Here's how one might call the RAPID Store using a RAPID-generated API.  Note that the developer does not have to concern himself with String building, media types, etc.  He only needs to set the host name and call the API.
```java
    public List<Order> getOrdersForCustomer(Customer customer, Order.StatusEnum status)
            throws ApiException {
        Configuration.getDefaultApiClient().setBasePath("http://localhost:8080/");
        CustomersResourceApi customerApi = new CustomersResourceApi();
        String statusString = (status == null) ? null : status.toString();

        List<Order> orders;
        orders = customerApi.findOrdersForCustomer(customer.getId(), statusString);

        return orders;
    }
```

## Features
- Supports services written in Java and annotated with Swagger 2.0 annotations
- Works with both [JAX-RS](https://github.com/jax-rs/) and [SpringMvc](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) services
- Capable of generating clients in a wide variety of languages by leveraging [swagger-codegen](https://github.com/swagger-api/swagger-codegen/tree/v2.3.1)
- Creates DTO's for all of the service's request and response objects, including enums and complex objects
- Generates a simplified API that replaces traditional HTTP clients and URL building with simple method calls
- Integrates seamlessly with existing services and build jobs
- Supports custom templates to allow generating code in any language and for any use case

## How It Works
The RAPID plugin runs during the `generate-sources` phase of the Maven lifecycle.  Using the [swagger-maven-plugin](https://github.com/kongchen/swagger-maven-plugin/tree/swagger-maven-plugin-3.1.7), it searches your service's jar for Swagger annotations and generates the appropriate Swagger document.  The plugin then uses that document as input to [swagger-codegen](https://github.com/swagger-api/swagger-codegen/tree/v2.3.1) to generate the source code for the client API.  Finally, the generated source is built and packaged by Maven during the `compile` and `package` phases.

It is important to note that the RAPID plugin can only search for Swagger annotations in compiled class files; it cannot parse raw source files.  For this reason, it can only be used to generate a client API from code that has already been compiled (perhaps in another project or in a separate module within the same project).

## Usage
Although the RAPID plugin can be used in a stand-alone project, it is highly recommend to use the plugin in a new module within your existing project.  This way, when you build your service, the RAPID plugin will run automatically.  Once you've created and configured the new module, **you'll get all the benefits of the RAPID client API for free.**

### Using A Template
The easiest way to get started is to copy the [example pom](examples/rapid-store/client-java/pom.xml) and modify it to suit your needs.  The plugin configuration shown below defines all of the required parameters.
```xml
<plugin>
    <!-- Here's the RAPID plugin -->
    <groupId>com.github.packleader.rapid</groupId>
    <artifactId>rapid-client-integration-maven-plugin</artifactId>
    <version>${com.github.packleader.rapid.version}</version>
    <configuration>
        <apiSource>
            <locations>
                <!-- The package that contains our Swagger-annotated resource classes -->
                <!-- Change this to reference the package containing your resource classes -->
                <location>com.packleader.rapid.example.store.resources.v1</location>
            </locations>
            <!-- The RAPID Store uses SpringMvc instead of JAX-RS -->
            <!-- Change this indicate whether your service uses SpringMvc (true) or JAX-RS (false) -->
            <springmvc>true</springmvc>
        </apiSource>
        <codeGeneration>
            <!-- Generate the client code in Java with the Jersey2 library -->
            <language>java</language>
            <library>jersey2</library>
            <!-- The packages where the generates sources will reside -->
            <!-- Update these values so that they are unique for your service -->
            <apiPackage>com.packleader.rapid.example.store.client.api</apiPackage>
            <invokerPackage>com.packleader.rapid.example.store.client</invokerPackage>
            <modelPackage>com.packleader.rapid.example.store.client.model</modelPackage>
        </codeGeneration>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

To adapt this template for your service, you will need to update the following parameters:
- `locations` - This option should contain a list of all the packages that contain your REST endpoints.  Each package will be recursively scanned for Swagger annotations.
- `springmvc` - This should be set to `true` if your service uses SpringMvc or `false` if using JAX-RS.
- `language` - The language for the generated client sources.  Must be one of the [languages supported by swagger-codegen](https://github.com/swagger-api/swagger-codegen/tree/v2.3.1/README.md#overview).
- `library` - Optional configuration parameter.  See [`code-generation`](README.md#codegeneration) below for more details.
- `apiPackage`, `invokerPackage`, and `modelPackage` - These values define the package names for the generated sources.  Each client jar must have a unique set of packages.

You will also need to update the `dependencies` section of the template pom file.  You should remove the dependency on the RAPID Store service artifact and replace it with a dependency on your service artifact.  **This dependency must be a jar**; if your service is packaged as a war, you will need to [generate an attached artifact](https://maven.apache.org/plugins/maven-war-plugin/faq.html#attached)

### Advanced Options
Several advanced options are available to further customize the plugin's behavior.  They are organized in two parts:
- The `apiSource` element defines how the plugin will inspect your service's source code
- The `codeGeneration` element configures how the client code will be generated

#### `apiSource`
The RAPID plugin uses the [swagger-maven-plugin](https://github.com/kongchen/swagger-maven-plugin/tree/swagger-maven-plugin-3.1.7) project to inspect your source code.  The `apiSource` configuration element is borrowed from that project with the following differences:
- Only one `apiSource` element can be specified.  You can still specify multiple `locations` if you like.
- The RAPID plugin only supports the following options at this time: `locations`, `info`, `springmvc`, `securityDefinitions`, `swaggerApiReader`, `modelSubstitute`
- The `info` parameter is not required.  If it is omitted, the plugin will create a default `info` with `${project.name}` as the title and `${project.name}` as the version.
Please see [this documentation](https://github.com/kongchen/swagger-maven-plugin/tree/swagger-maven-plugin-3.1.7#configuration-for-apisource) for more information.

#### `codeGeneration`
The RAPID plugin generates the client API source using [swagger-codegen](https://github.com/swagger-api/swagger-codegen/tree/v2.3.1).  The configuration options are modeled after those in the [swagger-codegen-maven-plugin](https://github.com/swagger-api/swagger-codegen/tree/v2.3.1/modules/swagger-codegen-maven-plugin) except for the following:
- The following options are not supported at this time: `inputSpec`, `addCompileSourceRoot`, `useJaxbAnnotations`, `configHelp`
- The `library` parameter may be specified in `configOptions` or directly under `codeGeneration`
- When generating a Java client, the `withXml` parameter can be specified under `configOptions` to control whether the plugin generates XML annotations on the models.  The default is `false`.
- The optional `generateApis` parameter controls whether the plugin will generate the API classes.  If it is set to `false`, only the models will be generated.  This is useful if you only want to distribute DTO's and not a full-fledged client API.  The default value is `true`.
- The optional `generateModels` parameter controls whether the plugin will generate the model objects.  If it is set to `false`, the plugin will generate the API classes, but not the models.  This option requires that you have already published your DTO's via some other method.  In this case, you will need to set the `modelPackage` parameter to reflect the package where your DTO's reside.  Note that this will only work if all the DTO's are in a single package.  The default value is `true`.
- Please see [this documentation](https://github.com/swagger-api/swagger-codegen/tree/v2.3.1/modules/swagger-codegen-maven-plugin#general-configuration-parameters) for more information.

## Multi-Language Support
The RAPID plugin allows for generating client APIs in a wide variety of languages.  Because it is built on top of [swagger-codegen](https://github.com/swagger-api/swagger-codegen/tree/v2.3.1), the plugin can generate any language supported by that project.  Please see [this list](https://github.com/swagger-api/swagger-codegen/tree/v2.3.1#overview) of currently supported languages.  If none of the current languages suit your needs, the RAPID plugin allows you to provide your own generator and/or templates.

Your custom generator must extend `io.swagger.codegen.DefaultCodegen` and provide customized logic for how the Swagger spec is applied to the template files.  To use your custom generator, pass the fully-qualified class name as the `language` config parameter.  See the [built-in generators](https://github.com/swagger-api/swagger-codegen/tree/v2.3.1/modules/swagger-codegen/src/main/java/io/swagger/codegen/languages) for more information.

If you want to add support for another language, you can provide a set of mustache templates for that language.  These templates will be used as the basis for the generated code.  To use your custom templates, provide the path for the templates in the `templateDirectory` parameter.  See the [built-in templates](https://github.com/swagger-api/swagger-codegen/tree/v2.3.1/modules/swagger-codegen/src/main/resources) for more information.

## Tips & Troubleshooting

### Documenting Your Service
If your service is documented with [Swagger annotations](https://github.com/swagger-api/swagger-core/wiki/Annotations-1.5.X), the plugin can use this information to generate a cleaner, more user-friendly client API.  For this reason, it is recommended that you ensure your service's annotations are thorough and up-to-date.  The following annotations are the minimum required documentation in order to use the RAPID plugin.  If your generated client jar does not look like you expected, please verify that they are correct.
- [@Api](https://github.com/swagger-api/swagger-core/wiki/Annotations-1.5.X#api) - The optional `value` and `tags` properties are used to determine the name of the generated API class.  If the `tags` property is present, its value will be used to name the API class.  If `tags` is not specified, the `value` property will be used.  If neither `tags` nor `value` is given, the generated class will be named `DefaultApi`.
- [@ApiOperation](https://github.com/swagger-api/swagger-core/wiki/Annotations-1.5.X#apioperation) - The required `value` property should be used to provide a descirption of the operation.
- [@ApiParam](https://github.com/swagger-api/swagger-core/wiki/Annotations-1.5.X#apiparam) - When using a JAX-RS service, this annotation must be placed on the entity parameter.  In other words, any parameter which does not have a JAX-RS annotation must have the `@ApiParam` annotation.  All of the annotation's properties are optional.  (The requirement for this annotation will be removed in a future release.)

### Using Pure DTO's
The RAPID plugin is designed to work best when your service's endpoints use pure DTO's - classes that have only public member variables and/or getters and setters with no business logic.  If your endpoints use domain objects that contain business logic, the plugin may still work, but the generated DTO's will likely contain extranneous or non-sensical properties.

### Common Errors
- **The plugin falis with an error message `No Swagger annotations were detected.`** - This indicates that the the plugin could not find any classes with Swagger annotations.  Ensure that the following pom elements are correct:
  - The `<locations>` configuration parameter refers to the package containing your endpoint classes
  - The `<springmvc>` parameter is set correctly
  - The compiled service has been added as a dependency.  This dependency must be a jar.
