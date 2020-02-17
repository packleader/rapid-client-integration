package com.packleader.rapid.generator;

import com.packleader.rapid.BaseTest;
import com.packleader.rapid.config.CodeGeneratorConfig;
import io.swagger.codegen.ClientOptInput;
import io.swagger.codegen.CodegenConfig;
import io.swagger.codegen.DefaultGenerator;
import io.swagger.codegen.config.CodegenConfigurator;
import io.swagger.codegen.config.CodegenConfiguratorUtils;
import org.apache.maven.project.MavenProject;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.testng.Assert.assertSame;

@PrepareForTest({CodeGenerator.class, CodegenConfiguratorUtils.class})
public class CodeGeneratorTest extends BaseTest {

    @Mock
    private ModelMapper modelMapperMock;

    @Mock
    private DefaultGenerator defaultGeneratorMock;

    @Mock
    private CodeGeneratorConfig codeGeneratorConfigMock;

    @Mock
    private MavenProject mavenProjectMock;

    @Mock
    private ClientOptInput clientOptInputMock;

    @Mock
    private Map<String, Object> configOptionsMock;

    @Mock
    private Map<String, Object> additionalPropertiesMock;

    @Mock
    private CodegenConfigurator configuratorMock;

    @InjectMocks
    private CodeGenerator codeGenerator;

    @Test
    public void testConstructor() throws Exception {
        Configuration mapperConfigurationMock = mock(Configuration.class);

        whenNew(ModelMapper.class).withNoArguments().thenReturn(modelMapperMock);
        when(modelMapperMock.getConfiguration()).thenReturn(mapperConfigurationMock);
        whenNew(DefaultGenerator.class).withNoArguments().thenReturn(defaultGeneratorMock);

        CodeGenerator codeGenerator = new CodeGenerator();

        ModelMapper modelMapper = Whitebox.getInternalState(codeGenerator, "modelMapper");
        DefaultGenerator defaultGenerator = Whitebox.getInternalState(codeGenerator, "defaultGenerator");

        assertSame(modelMapper, modelMapperMock);
        assertSame(defaultGenerator, defaultGeneratorMock);
        Mockito.verify(mapperConfigurationMock).setPropertyCondition(Conditions.isNotNull());
        verifyZeroInteractions(defaultGeneratorMock);
    }

    @Test
    public void testGenerate() throws Exception {
        final String outputDir = "output_directory";

        when(codeGeneratorConfigMock.getConfigOptions()).thenReturn(null);

        testGenerateRequiredParams(outputDir, true, true);
    }

    @Test(dataProvider = "testGenerateWithGenericConfigOptionsData")
    public void testGenerateWithGenericConfigOptions(boolean generateApis, boolean generateModels) throws Exception {
        final String outputDir = "output_dir";

        testGenerateWithConfigOptions(outputDir, generateApis, generateModels);

        Mockito.verify(additionalPropertiesMock).putAll(configOptionsMock);
    }

    @DataProvider(name = "testGenerateWithGenericConfigOptionsData")
    private Object[][] testGenerateWithGenericConfigOptionsData() {
        return new Object[][]{
                {true, true},
                {true, false},
                {false, true},
                {false, false}
        };
    }

    @Test(dataProvider = "testGenerateWithSpecificConfigOptionsData")
    public void testGenerateWithSpecificConfigOptions(String instantiationTypes, String importMappings,
                                                      String typeMappings, String languageSpecificPrimitives,
                                                      String additionalProperties, String reservedWordsMappings) throws Exception {
        mockStatic(CodegenConfiguratorUtils.class);
        final String outputDir = "outputDir";

        mockMapEntry(configOptionsMock, "instantiation-types", instantiationTypes);
        mockMapEntry(configOptionsMock, "import-mappings", importMappings);
        mockMapEntry(configOptionsMock, "type-mappings", typeMappings);
        mockMapEntry(configOptionsMock, "language-specific-primitives", languageSpecificPrimitives);
        mockMapEntry(configOptionsMock, "additional-properties", additionalProperties);
        mockMapEntry(configOptionsMock, "reserved-words-mappings", reservedWordsMappings);

        testGenerateWithConfigOptions(outputDir, true, true);

        verifyCodegenConfiguratorUtilsMethod(configuratorMock, instantiationTypes, CodegenConfiguratorUtils::applyInstantiationTypesKvp);
        verifyCodegenConfiguratorUtilsMethod(configuratorMock, importMappings, CodegenConfiguratorUtils::applyImportMappingsKvp);
        verifyCodegenConfiguratorUtilsMethod(configuratorMock, typeMappings, CodegenConfiguratorUtils::applyTypeMappingsKvp);
        verifyCodegenConfiguratorUtilsMethod(configuratorMock, languageSpecificPrimitives, CodegenConfiguratorUtils::applyLanguageSpecificPrimitivesCsv);
        verifyCodegenConfiguratorUtilsMethod(configuratorMock, additionalProperties, CodegenConfiguratorUtils::applyAdditionalPropertiesKvp);
        verifyCodegenConfiguratorUtilsMethod(configuratorMock, reservedWordsMappings, CodegenConfiguratorUtils::applyReservedWordsMappingsKvp);
    }

    @DataProvider(name = "testGenerateWithSpecificConfigOptionsData")
    private Object[][] testGenerateWithSpecificConfigOptions() {
        return new Object[][]{
                {"a", "b", "c", "d", "e", "f"},
                {"1", null, "2", null, "3", null},
                {null, "Larry", null, "Moe", null, "Curly"},
                {null, null, null, null, null, null}
        };
    }

    private <T> void mockMapEntry(Map<String, T> mapMock, String key, T value) {
        when(configOptionsMock.containsKey(key)).thenReturn((value != null));
        when(mapMock.get(key)).thenReturn(value);
    }

    private void verifyCodegenConfiguratorUtilsMethod(CodegenConfigurator configurator, String value,
                                                      BiConsumer<String, CodegenConfigurator> consumer) {
        if (value != null) {
            verifyStatic(CodegenConfiguratorUtils.class);
            consumer.accept(value, configurator);
        } else {
            verifyStatic(CodegenConfiguratorUtils.class, Mockito.never());
            consumer.accept(Mockito.anyString(), Mockito.any());
        }
    }

    private void testGenerateWithConfigOptions(String outputDir, boolean generateApis, boolean generateModels) throws Exception {
        CodegenConfig codegenConfigMock = mock(CodegenConfig.class);

        when(codeGeneratorConfigMock.getConfigOptions()).thenReturn(configOptionsMock);
        when(clientOptInputMock.getConfig()).thenReturn(codegenConfigMock);
        when(codegenConfigMock.additionalProperties()).thenReturn(additionalPropertiesMock);

        testGenerateRequiredParams(outputDir, generateApis, generateModels);
    }

    private void testGenerateRequiredParams(String outputDir, boolean generateApis, boolean generateModels) throws Exception {
        File outputDirFileMock = mock(File.class);

        when(codeGeneratorConfigMock.isGenerateApis()).thenReturn(generateApis);
        when(codeGeneratorConfigMock.isGenerateModels()).thenReturn(generateModels);
        when(codeGeneratorConfigMock.getOutputDir()).thenReturn(outputDir);
        whenNew(File.class).withArguments(outputDir).thenReturn(outputDirFileMock);
        when(modelMapperMock.map(codeGeneratorConfigMock, CodegenConfigurator.class)).thenReturn(configuratorMock);
        when(configuratorMock.toClientOptInput()).thenReturn(clientOptInputMock);

        codeGenerator.generate(codeGeneratorConfigMock);

        verifyGenerateApis(generateApis);
        verifyGenerateModels(generateModels);
        Mockito.verify(outputDirFileMock).mkdirs();
        Mockito.verify(configuratorMock).addSystemProperty("modelTests", "false");
        Mockito.verify(configuratorMock).addSystemProperty("modelDocs", "false");
        Mockito.verify(configuratorMock).addSystemProperty("apiTests", "false");
        Mockito.verify(configuratorMock).addSystemProperty("apiDocs", "false");
        Mockito.verify(defaultGeneratorMock).opts(clientOptInputMock);
        Mockito.verify(defaultGeneratorMock).generate();
    }

    private void verifyGenerateApis(boolean generateApis) {
        String expected = getFilePattern(generateApis);
        Mockito.verify(configuratorMock).addSystemProperty("apis", expected);
        Mockito.verify(configuratorMock).addSystemProperty("supportingFiles", expected);
    }

    private void verifyGenerateModels(boolean generateModels) {
        String expected = getFilePattern(generateModels);
        Mockito.verify(configuratorMock).addSystemProperty("models", expected);
    }

    private String getFilePattern(boolean generate) {
        return generate ? "" : Whitebox.getInternalState(CodeGenerator.class, "MATCH_NO_FILES");
    }
}
