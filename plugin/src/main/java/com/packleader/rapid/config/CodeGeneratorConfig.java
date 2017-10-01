package com.packleader.rapid.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

@Getter
@Setter
public class CodeGeneratorConfig extends RapidConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeGeneratorConfig.class);

    @Getter(AccessLevel.NONE)
    private String language;
    @Getter(AccessLevel.NONE)
    private File output;
    @Getter(AccessLevel.NONE)
    private File templateDirectory;
    private String modelPackage;
    private String apiPackage;
    private String invokerPackage;
    private String modelNamePrefix;
    private String modelNameSuffix;
    private Map<?, ?> configOptions;
    private String ignoreFileOverride;
    private String library;
    private boolean generateApis;
    private boolean generateModels;

    public String getLang() {
        return language;
    }

    public String getOutputDir() {
        String outputDir = null;

        if (output != null) {
            outputDir = output.getAbsolutePath();
        } else {
            if (defaultValueProvider != null) {
                outputDir = defaultValueProvider.getGeneratedSourcesDirectory();
                LOGGER.info("Output property was not specified. Using default value {}", outputDir);
            }
        }

        return outputDir;
    }

    public String getTemplateDir() {
        return (templateDirectory == null) ? null : templateDirectory.getAbsolutePath();
    }

    public Map<String, Object> getConfigOptions() {
        return (Map<String, Object>) configOptions;
    }

    public String getInputSpec() {
        return getSwaggerFile();
    }
}
