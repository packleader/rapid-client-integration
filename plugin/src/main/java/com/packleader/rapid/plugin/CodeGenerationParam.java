package com.packleader.rapid.plugin;

import java.io.File;
import java.util.Map;

public class CodeGenerationParam {

    public String language;
    public File output;
    public File templateDirectory;
    public String modelPackage;
    public String apiPackage;
    public String invokerPackage;
    public String modelNamePrefix;
    public String modelNameSuffix;
    public Map<?, ?> configOptions;
    public String ignoreFileOverride;
    public String library;
    public boolean generateApis = true;
    public boolean generateModels = true;

}
