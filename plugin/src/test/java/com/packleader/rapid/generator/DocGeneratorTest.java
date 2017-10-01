package com.packleader.rapid.generator;

import com.github.kongchen.swagger.docgen.mavenplugin.ApiDocumentMojo;
import com.github.kongchen.swagger.docgen.mavenplugin.ApiSource;
import com.packleader.rapid.BaseTest;
import com.packleader.rapid.config.DocGeneratorConfig;
import com.packleader.rapid.config.RapidConfig;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.logging.Logger;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

@PrepareForTest({DocGenerator.class, DocGeneratorConfig.class, RapidConfig.class})
public class DocGeneratorTest extends BaseTest {

    private static final String API_ERRORS_CLASS = "com.wordnik.swagger.annotations.ApiErrors";
    private static final String API_LISTING_CLASS = "com.wordnik.swagger.model.ApiListing";

    @Mock
    private Logger loggerMock;

    @Mock
    private ModelMapper modelMapperMock;

    @Mock
    private ApiDocumentMojo apiDocumentMojoMock;

    @Mock
    private DocGeneratorConfig docGeneratorConfigMock;

    @InjectMocks
    private DocGenerator docGenerator;

    @Test
    public void testConstructor() throws Exception {
        whenNew(ModelMapper.class).withNoArguments().thenReturn(modelMapperMock);
        whenNew(ApiDocumentMojo.class).withNoArguments().thenReturn(apiDocumentMojoMock);

        DocGenerator docGenerator = new DocGenerator();

        ModelMapper modelMapper = Whitebox.getInternalState(docGenerator, "modelMapper");
        ApiDocumentMojo apiDocumentMojo = Whitebox.getInternalState(docGenerator, "docMojo");

        assertSame(modelMapper, modelMapperMock);
        assertSame(apiDocumentMojo, apiDocumentMojoMock);
        verifyZeroInteractions(modelMapperMock, apiDocumentMojoMock);
    }

    @Test
    public void testGenerate() throws Exception {
        mockStatic(Class.class);
        final String swaggerDocFile = "swagger_doc_file";
        DocGeneratorConfig docGeneratorConfigMock = mock(DocGeneratorConfig.class);
        ApiSource apiSourceMock = mock(ApiSource.class);
        DefaultLog defaultLogMock = mock(DefaultLog.class);
        SwaggerParser swaggerParserMock = mock(SwaggerParser.class);
        Swagger swaggerMock = mock(Swagger.class);
        Map<String, Path> pathMapMock = mock(Map.class);

        mockClassForName(API_ERRORS_CLASS, false);
        mockClassForName(API_LISTING_CLASS, false);
        when(modelMapperMock.map(docGeneratorConfigMock, ApiSource.class)).thenReturn(apiSourceMock);
        whenNew(DefaultLog.class).withArguments(loggerMock).thenReturn(defaultLogMock);
        when(docGeneratorConfigMock.getSwaggerFile()).thenReturn(swaggerDocFile);
        whenNew(SwaggerParser.class).withNoArguments().thenReturn(swaggerParserMock);
        when(swaggerParserMock.read(swaggerDocFile)).thenReturn(swaggerMock);
        when(swaggerMock.getPaths()).thenReturn(pathMapMock);

        docGenerator.generate(docGeneratorConfigMock);

        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(apiDocumentMojoMock).setLog(defaultLogMock);
        Mockito.verify(apiDocumentMojoMock).setApiSources(listCaptor.capture());
        Mockito.verify(apiDocumentMojoMock).execute();
        List<ApiSource> apiSourceList = listCaptor.getValue();
        assertEquals(apiSourceList.size(), 1);
        assertSame(apiSourceList.get(0), apiSourceMock);
    }

    @Test(dataProvider = "testCheckPreconditionsWithExceptionData", expectedExceptions = MojoExecutionException.class)
    public void testGenerateFailedPrecondition(boolean isApiErrorsPresent, boolean isApiListingPresent) throws Exception {
        mockStatic(Class.class);

        mockClassForName(API_ERRORS_CLASS, isApiErrorsPresent);
        mockClassForName(API_LISTING_CLASS, isApiListingPresent);

        docGenerator.generate(docGeneratorConfigMock);
    }

    @Test(expectedExceptions = MojoExecutionException.class,
          expectedExceptionsMessageRegExp = "No Swagger annotations were detected.  Ensure that <locations> and <springmvc> are configured correctly")
    public void testGenerateNoOutput() throws Exception {
        final String swaggerDocFile = "swagger_doc_file";
        DocGeneratorConfig docGeneratorConfigMock = mock(DocGeneratorConfig.class);
        SwaggerParser swaggerParserMock = mock(SwaggerParser.class);
        Swagger swaggerMock = mock(Swagger.class);

        when(docGeneratorConfigMock.getSwaggerFile()).thenReturn(swaggerDocFile);
        whenNew(SwaggerParser.class).withNoArguments().thenReturn(swaggerParserMock);
        when(swaggerParserMock.read(swaggerDocFile)).thenReturn(swaggerMock);
        when(swaggerMock.getPaths()).thenReturn(null);

        docGenerator.generate(docGeneratorConfigMock);
    }

    @DataProvider(name = "testCheckPreconditionsWithExceptionData")
    public static Object[][] testCheckPreconditionsWithExceptionData() {
        return new Object[][]{
                {false, true},
                {true, false},
                {true, true}};
    }

    private void mockClassForName(String className, boolean isPresent) throws Exception {
        if (!isPresent) {
            when(Class.forName(className)).thenThrow(new ClassNotFoundException());
        }

    }
}
