package com.packleader.rapid.config;

import io.swagger.models.Info;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Getter
@Setter
public class DocGeneratorConfig extends RapidConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocGeneratorConfig.class);

    private List<String> locations;
    private Info info;
    private boolean springmvc;
    private List<SecurityDefinitionConfig> securityDefinitions;
    private String swaggerApiReader;
    private String modelSubstitute;

    public Info getInfo() {
        if (info == null && defaultValueProvider != null) {
            LOGGER.info("Info property was not specified. Using default value.");
            info = defaultValueProvider.getInfo();
        }

        return info;
    }

    public String getSwaggerDirectory() {
        return getSwaggerPath();
    }

}
