package com.packleader.rapid.config;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Setter
public class RapidConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RapidConfig.class);

    protected DefaultValueProvider defaultValueProvider;
    private String buildDirectory;

    public String getBuildDirectory() {
        if (buildDirectory == null && defaultValueProvider != null) {
            buildDirectory = defaultValueProvider.getBuildDirectory();
            LOGGER.info("Build directory was not specified. Using default value {}", buildDirectory);
        }

        return buildDirectory;
    }

    public final String getSwaggerPath() {
        return getBuildDirectory() + "/generated-swagger-ui";
    }

    public final String getSwaggerFile() {
        return getSwaggerPath() + "/swagger.json";
    }
}
