package com.packleader.rapid.generator;

import com.packleader.rapid.config.CodeGeneratorConfig;
import com.packleader.rapid.config.DocGeneratorConfig;
import lombok.NonNull;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(role = RapidGenerator.class)
public class RapidGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RapidGenerator.class);

    @Requirement
    private DocGenerator docGenerator;

    @Requirement
    private CodeGenerator codeGenerator;

    public void generate(@NonNull DocGeneratorConfig docConfig, @NonNull CodeGeneratorConfig codeConfig)
            throws MojoFailureException, MojoExecutionException {
        LOGGER.info("Generating client API");
        docGenerator.generate(docConfig);
        codeGenerator.generate(codeConfig);
    }
}
