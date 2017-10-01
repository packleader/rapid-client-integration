package com.packleader.rapid.config;

import io.swagger.models.Info;

public interface DefaultValueProvider {

    String getBuildDirectory();
    String getOutputDirectory();
    Info getInfo();
    String getGeneratedSourcesDirectory();

}
