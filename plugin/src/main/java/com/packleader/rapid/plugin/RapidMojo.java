package com.packleader.rapid.plugin;

import com.packleader.rapid.config.CodeGeneratorConfig;
import com.packleader.rapid.config.DefaultValueProvider;
import com.packleader.rapid.config.DocGeneratorConfig;
import com.packleader.rapid.config.RapidConfig;
import com.packleader.rapid.generator.RapidGenerator;
import io.swagger.codegen.CodegenConstants;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.modelmapper.ModelMapper;

import java.io.File;
import java.util.Map;
import java.util.Optional;

/**
 * Goal which generates client APIs from a RESTful service's source code.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, configurator = "include-project-dependencies-rapid",
      requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class RapidMojo extends AbstractMojo {

    @Parameter(readonly = true, required = true, defaultValue = "${project}")
    private MavenProject mavenProject;

    /**
     * Defines how the service source code will be inspected.
     * See https://github.com/kongchen/swagger-maven-plugin, although not all parameters are supported here.
     */
    @Parameter(required = true)
    private ApiSourceParam apiSource;

    /**
     * Defines how the API client code will be generated.
     * See https://github.com/swagger-api/swagger-codegen/tree/master/modules/swagger-codegen-maven-plugin,
     * although not all parameters are supported here.
     */
    @Parameter(required = true)
    private CodeGenerationParam codeGeneration;

    /**
     * Indicates that the plugin should be skipped.  Default is false.
     */
    @Parameter(required = false)
    private boolean skip = false;

    @Component
    private RapidGenerator rapidGenerator;

    @Component
    private DefaultValueProvider defaultValueProvider;

    private ModelMapper modelMapper;

    public RapidMojo() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true);
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if(skip) {
            getLog().info("<skip> parameter was set to true.  Skipping plugin.");
            return;
        }

        createOutputDirectory();

        DocGeneratorConfig docGeneratorConfig = buildConfig(apiSource, DocGeneratorConfig.class);
        CodeGeneratorConfig codeGeneratorConfig = buildConfig(codeGeneration, CodeGeneratorConfig.class);

        rapidGenerator.generate(docGeneratorConfig, codeGeneratorConfig);

        addCompileSourceRoot(codeGeneratorConfig);
    }

    private void createOutputDirectory() {
        String outputDirectory = defaultValueProvider.getOutputDirectory();
        if(outputDirectory != null) {
            File classesDir = new File(outputDirectory);
            classesDir.mkdirs();
        }
    }

    private <T extends RapidConfig> T buildConfig(Object source, Class<T> destClass) {
        getLog().info("Building configuration " + destClass + " from " + source);
        T config = modelMapper.map(source, destClass);
        config.setDefaultValueProvider(defaultValueProvider);
        return config;
    }

    private void addCompileSourceRoot(CodeGeneratorConfig config) {
        Map<String, Object> configOptions = config.getConfigOptions();

        final String sourceFolder = Optional.ofNullable(configOptions)
                .map(opts -> opts.get(CodegenConstants.SOURCE_FOLDER))
                .map(Object::toString)
                .orElse("src/main/java");

        String sourceJavaFolder = config.getOutputDir() + "/" + sourceFolder;
        mavenProject.addCompileSourceRoot(sourceJavaFolder);

        getLog().info("Added generated code at " + sourceJavaFolder + " to maven project");
    }

}
