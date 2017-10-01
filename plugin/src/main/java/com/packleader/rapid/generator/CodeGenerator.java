package com.packleader.rapid.generator;

import com.packleader.rapid.config.CodeGeneratorConfig;
import io.swagger.codegen.ClientOptInput;
import io.swagger.codegen.DefaultGenerator;
import io.swagger.codegen.config.CodegenConfigurator;
import io.swagger.codegen.config.CodegenConfiguratorUtils;
import lombok.NonNull;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.annotations.Component;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

@Component(role = CodeGenerator.class)
public class CodeGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeGenerator.class);
    private static final String MATCH_ALL_FILES = "";
    private static final String MATCH_NO_FILES = UUID.randomUUID().toString();

    private ModelMapper modelMapper;
    private DefaultGenerator defaultGenerator;

    public CodeGenerator() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        defaultGenerator = new DefaultGenerator();
    }

    public void generate(@NonNull CodeGeneratorConfig config) throws MojoExecutionException {
        createOutputDirectory(config);

        ClientOptInput input = buildClientOptInput(config);
        defaultGenerator.opts(input);

        LOGGER.info("Generating client sources");
        defaultGenerator.generate();
    }

    private void createOutputDirectory(CodeGeneratorConfig config) {
        String outputDir = config.getOutputDir();

        LOGGER.info("Creating directory for generated sources at " + outputDir);
        File outputDirFile = new File(outputDir);
        outputDirFile.mkdirs();
    }

    private ClientOptInput buildClientOptInput(CodeGeneratorConfig config) {
        CodegenConfigurator configurator = buildCodegenConfigurator(config);

        ClientOptInput input = configurator.toClientOptInput();

        Map<String, Object> configOptions = config.getConfigOptions();
        if (configOptions != null) {
            input.getConfig().additionalProperties().putAll(configOptions);
        }

        return input;
    }

    private CodegenConfigurator buildCodegenConfigurator(CodeGeneratorConfig config) {
        CodegenConfigurator configurator = modelMapper.map(config, CodegenConfigurator.class);

        Map<String, Object> configOptions = config.getConfigOptions();
        if (configOptions != null) {
            applyConfigOption(configurator, configOptions, "instantiation-types", CodegenConfiguratorUtils::applyInstantiationTypesKvp);
            applyConfigOption(configurator, configOptions, "import-mappings", CodegenConfiguratorUtils::applyImportMappingsKvp);
            applyConfigOption(configurator, configOptions, "type-mappings", CodegenConfiguratorUtils::applyTypeMappingsKvp);
            applyConfigOption(configurator, configOptions, "language-specific-primitives", CodegenConfiguratorUtils::applyLanguageSpecificPrimitivesCsv);
            applyConfigOption(configurator, configOptions, "additional-properties", CodegenConfiguratorUtils::applyAdditionalPropertiesKvp);
            applyConfigOption(configurator, configOptions, "reserved-words-mappings", CodegenConfiguratorUtils::applyReservedWordsMappingsKvp);
        }

        suppressTestsAndDocs(configurator);
        setModelGeneration(config, configurator);
        setApiGeneration(config, configurator);

        return configurator;
    }

    private void applyConfigOption(CodegenConfigurator configurator, Map<String, Object> configOptions, String key,
                                   BiConsumer<String, CodegenConfigurator> consumer) {
        if (configOptions.containsKey(key)) {
            consumer.accept(configOptions.get(key).toString(), configurator);
        }
    }

    private void suppressTestsAndDocs(CodegenConfigurator configurator) {
        configurator.addSystemProperty("modelTests", "false");
        configurator.addSystemProperty("modelDocs", "false");
        configurator.addSystemProperty("apiTests", "false");
        configurator.addSystemProperty("apiDocs", "false");
    }

    private void setModelGeneration(CodeGeneratorConfig config, CodegenConfigurator configurator) {
        String modelFilesParam = getFilePattern(config.isGenerateModels());
        configurator.addSystemProperty("models", modelFilesParam);
    }

    private void setApiGeneration(CodeGeneratorConfig config, CodegenConfigurator configurator) {
        String apiFilesParam = getFilePattern(config.isGenerateApis());
        configurator.addSystemProperty("apis", apiFilesParam);
        configurator.addSystemProperty("supportingFiles", apiFilesParam);
    }

    private String getFilePattern(boolean generateFiles) {
        return generateFiles ? MATCH_ALL_FILES : MATCH_NO_FILES;
    }

}
