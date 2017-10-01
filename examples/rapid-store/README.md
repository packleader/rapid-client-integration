# RAPID Store Example
This example demonstrates how to integrate the RAPID plugin with a RESTful Java service.  The project consists of a parent with two modules:
- **service** - This is the actual RESTful service
- **client-java** - This is the module that executes the RAPID plugin and generates the client API

Note that you are not limited to generating a single client API.  If you wish to generate client API's in multiple languages, simply add a new module for each one.  The recommended naming convention is `client-{language}` (eg. `client-c#`, `client-haskell`, etc.)

## The Parent Project
The parent is pretty straightforward and consists only of a single pom file that references the two modules.  Placing the service and client modules in the same parent project is recommended (although not required) because it simplifies the build process.  Whenever the parent project is built, the service will be built first, followed by all of the clients.  This helps to ensure that the service and client API's stay in-sync.

## The Service Module
The example service is not of particular interest; it's just here because you can't generate a client API if you don't have a service to begin with.  The service leverages Spring Boot and can be launched by running `com.packleader.rapid.example.store.RapidStore`.  Note that the service uses in-memory storage, so it is not persistent.  You'll need to create data in the service each time you launch it or else your queries will always return zero results.

## The Client Module
**This is where the magic happens!**  The client module is small contains only the pom file which invokes the RAPID plugin.  The pom consists of three parts:
- **RAPID plugin** - The plugin and its configuration
- **Service dependency** - The pom must include a dependency on the service artifact.  Moreover, that dependency must be a jar.  If your service is packaged as a war, you will need to [generate an attached artifact](https://maven.apache.org/plugins/maven-war-plugin/faq.html#attached).
- **Build dependencies (optional)** - If you have configured the plugin to generate Java sources, Maven will attempt to compile those sources and package them as a jar.  In this case, you will need to include any third-party dependencies required by the generated source.  These dependencies will vary based on your specific configuration, but the sample pom provides a good starting point.

For more information on how the client module works, please inspect the [pom](client-java/pom.xml) directly and review the in-line comments.
