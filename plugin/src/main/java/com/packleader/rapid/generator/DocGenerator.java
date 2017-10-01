package com.packleader.rapid.generator;

import com.github.kongchen.swagger.docgen.mavenplugin.ApiDocumentMojo;
import com.github.kongchen.swagger.docgen.mavenplugin.ApiSource;
import com.packleader.rapid.config.DocGeneratorConfig;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import lombok.NonNull;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Component(role = DocGenerator.class)
public class DocGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RapidGenerator.class);
    private static final String API_ERRORS_CLASS = "com.wordnik.swagger.annotations.ApiErrors";
    private static final String API_LISTING_CLASS = "com.wordnik.swagger.model.ApiListing";

    @Requirement
    private org.codehaus.plexus.logging.Logger plexusLogger;

    private ModelMapper modelMapper;
    private ApiDocumentMojo docMojo;

    public DocGenerator() {
        modelMapper = new ModelMapper();
        docMojo = new ApiDocumentMojo();
    }

    public void generate(@NonNull DocGeneratorConfig config) throws MojoFailureException, MojoExecutionException {
        verifyPreconditions();

        List<ApiSource> apiSources = buildApiSources(config);
        final DefaultLog log = new DefaultLog(plexusLogger);
        docMojo.setLog(log);
        docMojo.setApiSources(apiSources);

        LOGGER.info("Generating Swagger document file");
        docMojo.execute();

        verifyOutput(config);
    }

    private void verifyPreconditions() throws MojoExecutionException {
        LOGGER.info("Checking Swagger version in source project");

        if (isOnClassPath(API_ERRORS_CLASS) || isOnClassPath(API_LISTING_CLASS)) {
            throw new MojoExecutionException("The source project appears to be using Swagger spec 1.x (com.wordnik).  Only Swagger spec 2.0 or higher (io.swagger) is supported.");
        }
    }

    private boolean isOnClassPath(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private List<ApiSource> buildApiSources(DocGeneratorConfig config) {
        ApiSource apiSource = modelMapper.map(config, ApiSource.class);
        return Arrays.asList(apiSource);
    }

    private void verifyOutput(DocGeneratorConfig config) throws MojoExecutionException {
        String swaggerDocFile = config.getSwaggerFile();
        LOGGER.info("Checking for {}", swaggerDocFile);
        SwaggerParser swaggerParser = new SwaggerParser();
        Swagger swagger = swaggerParser.read(swaggerDocFile);

        if (swagger.getPaths() == null) {
            throw new MojoExecutionException("No Swagger annotations were detected.  Ensure that <locations> and <springmvc> are configured correctly");
        }
    }
}
