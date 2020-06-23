package com.packleader.rapid.plugin;

import com.packleader.rapid.BaseTest;
import com.packleader.rapid.config.CodeGeneratorConfig;
import com.packleader.rapid.config.DefaultValueProvider;
import com.packleader.rapid.config.DocGeneratorConfig;
import com.packleader.rapid.generator.RapidGenerator;
import io.swagger.codegen.CodegenConstants;
import org.apache.maven.project.MavenProject;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

import static org.mockito.Mockito.inOrder;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.testng.Assert.assertSame;

@PrepareForTest(RapidMojo.class)
public class RapidMojoTest extends BaseTest {

    @Mock
    private MavenProject mavenProjectMock;

    @Mock
    private ApiSourceParam apiSourceMock;

    @Mock
    private CodeGenerationParam codeGenerationMock;

    @Mock
    private RapidGenerator rapidGeneratorMock;

    @Mock
    private DefaultValueProvider defaultValueProviderMock;

    @Mock
    private RapidDependencyHelper rapidDependencyHelperMock;

    @Mock
    private ModelMapper modelMapperMock;

    @Mock
    private DocGeneratorConfig docGeneratorConfigMock;

    @Mock
    private CodeGeneratorConfig codeGeneratorConfigMock;

    @InjectMocks
    private RapidMojo rapidMojo;

    @BeforeMethod
    @Override
    public void init() throws Exception {
        super.init();
        Whitebox.setInternalState(rapidMojo, "skip", false);
    }

    @Test
    public void testConstructor() throws Exception {
        Configuration mapperConfigurationMock = mock(Configuration.class);

        whenNew(ModelMapper.class).withNoArguments().thenReturn(modelMapperMock);
        when(modelMapperMock.getConfiguration()).thenReturn(mapperConfigurationMock);

        RapidMojo rapidMojo = new RapidMojo();

        ModelMapper modelMapper = Whitebox.getInternalState(rapidMojo, "modelMapper");

        assertSame(modelMapper, modelMapperMock);
        Mockito.verify(mapperConfigurationMock).setFieldMatchingEnabled(true);
    }

    @Test
    public void testExecute() throws Exception {
        final String generatedSourcesDirectory = "generatedSourcesDirectory";
        final String sourceJavaFolder = generatedSourcesDirectory + "/src/main/java";
        final String outputDir = "output_directory";
        File fileMock = mock(File.class);

        whenNew(File.class).withArguments(outputDir).thenReturn(fileMock);

        testExecute(generatedSourcesDirectory, outputDir);

        Mockito.verify(rapidDependencyHelperMock).unpackDependencies();
        Mockito.verify(mavenProjectMock).addCompileSourceRoot(sourceJavaFolder);
        Mockito.verify(fileMock).mkdirs();
    }

    @Test
    public void testExecuteWithoutOutputDir() throws Exception {
        final String generatedSourcesDirectory = "generatedSourcesDirectory";
        final String sourceJavaFolder = generatedSourcesDirectory + "/src/main/java";
        final String outputDir = null;

        testExecute(generatedSourcesDirectory, outputDir);

        Mockito.verify(mavenProjectMock).addCompileSourceRoot(sourceJavaFolder);
    }

    @Test(dataProvider = "testExecuteWithConfigOptionsData")
    public void testExecuteWithConfigOptions(String sourceFolder) throws Exception {
        final String generatedSourcesDirectory = "generated_sources_directory";
        final String sourceJavaFolder = (sourceFolder == null) ? "src/main/java" : sourceFolder;

        Map<String, Object> configOptionsMock = mock(Map.class);

        when(codeGeneratorConfigMock.getConfigOptions()).thenReturn(configOptionsMock);
        when(configOptionsMock.get(CodegenConstants.SOURCE_FOLDER)).thenReturn(sourceFolder);

        testExecute(generatedSourcesDirectory, null);

        Mockito.verify(mavenProjectMock).addCompileSourceRoot(generatedSourcesDirectory + "/" + sourceJavaFolder);
    }

    @Test
    public void testExecuteWithSkip() throws Exception {
        Whitebox.setInternalState(rapidMojo, "skip", true);

        rapidMojo.execute();

        Mockito.verifyZeroInteractions(mavenProjectMock, apiSourceMock, codeGenerationMock, rapidGeneratorMock,
                defaultValueProviderMock, modelMapperMock, docGeneratorConfigMock, codeGeneratorConfigMock);
    }

    @DataProvider(name = "testExecuteWithConfigOptionsData")
    private Object[][] testExecuteWithConfigOptionsData() {
        return new Object[][]{
                {null},
                {"src/main/somewhere/else"},
                {"generated-sources"}
        };
    }

    public void testExecute(String generatedSourcesDirectory, String outputDir) throws Exception {
        when(defaultValueProviderMock.getOutputDirectory()).thenReturn(outputDir);
        when(modelMapperMock.map(apiSourceMock, DocGeneratorConfig.class)).thenReturn(docGeneratorConfigMock);
        when(modelMapperMock.map(codeGenerationMock, CodeGeneratorConfig.class)).thenReturn(codeGeneratorConfigMock);
        when(codeGeneratorConfigMock.getOutputDir()).thenReturn(generatedSourcesDirectory);

        rapidMojo.execute();

        InOrder inOrder = inOrder(docGeneratorConfigMock, codeGeneratorConfigMock, rapidGeneratorMock);
        inOrder.verify(docGeneratorConfigMock).setDefaultValueProvider(defaultValueProviderMock);
        inOrder.verify(codeGeneratorConfigMock).setDefaultValueProvider(defaultValueProviderMock);
        inOrder.verify(rapidGeneratorMock).generate(docGeneratorConfigMock, codeGeneratorConfigMock);
    }

}
