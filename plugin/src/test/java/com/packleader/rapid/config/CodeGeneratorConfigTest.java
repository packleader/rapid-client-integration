package com.packleader.rapid.config;

import com.packleader.rapid.BaseTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class CodeGeneratorConfigTest extends BaseTest {

    @Mock(name = "output")
    private File outputMock;

    @Mock(name = "templateDirectory")
    private File templateDirectoryMock;

    @Mock
    private Map<?, ?> configOptionsMock;

    @Mock
    private DefaultValueProvider defaultValueProviderMock;

    @InjectMocks
    private CodeGeneratorConfig codeGeneratorConfig;

    @Test(dataProvider = "stringProvider")
    public void testGetLang(String language) throws Exception {
        testGetter(codeGeneratorConfig, "language", language, codeGeneratorConfig::getLang);
    }

    @Test
    public void testGetOutputDir() throws Exception {
        final String outputDir = "outputDirectoryForTest";

        when(outputMock.getAbsolutePath()).thenReturn(outputDir);

        testGetter(outputDir, codeGeneratorConfig::getOutputDir);
    }

    @Test
    public void testGetOutputDirForDefault() throws Exception {
        final String outputDirectory = "mojoOutputDir";
        Whitebox.setInternalState(codeGeneratorConfig, "output", (Object) null);
        when(defaultValueProviderMock.getGeneratedSourcesDirectory()).thenReturn(outputDirectory);
        String result = codeGeneratorConfig.getOutputDir();

        assertEquals(result, outputDirectory);
    }

    @Test
    public void testGetOutputDirForNull() throws Exception {
        Whitebox.setInternalState(codeGeneratorConfig, "output", (Object) null);
        Whitebox.setInternalState(codeGeneratorConfig, "defaultValueProvider", (Object) null);

        String result = codeGeneratorConfig.getOutputDir();

        assertNull(result);
    }

    @Test
    public void testGetTemplateDir() throws Exception {
        final String templateDir = "templateDirectoryForTest";

        when(templateDirectoryMock.getAbsolutePath()).thenReturn(templateDir);

        testGetter(templateDir, codeGeneratorConfig::getTemplateDir);
    }

    @Test
    public void testGetTemplateDirForNull() throws Exception {
        testGetter(codeGeneratorConfig, "templateDirectory", null, codeGeneratorConfig::getTemplateDir);
    }

    @Test
    public void testGetConfigOptions() throws Exception {
        testGetter(configOptionsMock, codeGeneratorConfig::getConfigOptions);
    }

    @Test
    public void testGetInputSpec() throws Exception {
        final String buildDirectory = "mojoBuildDir";

        Whitebox.setInternalState(codeGeneratorConfig, "buildDirectory", buildDirectory);

        String result = codeGeneratorConfig.getInputSpec();

        assertEquals(result, buildDirectory + "/generated-swagger-ui/swagger.json");
    }
}
