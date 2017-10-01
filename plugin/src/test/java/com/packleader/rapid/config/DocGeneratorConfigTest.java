package com.packleader.rapid.config;

import com.packleader.rapid.BaseTest;
import io.swagger.models.Info;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.Test;

import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.*;

public class DocGeneratorConfigTest extends BaseTest {

    @Mock(name = "locations")
    private List<String> locationsMock;

    @Mock
    private Info infoMock;

    @Mock(name = "securityDefinitions")
    private List<SecurityDefinitionConfig> securityDefinitionsMock;

    @Mock
    private DefaultValueProvider defaultValueProviderMock;

    @InjectMocks
    private DocGeneratorConfig docGeneratorConfig;

    @Test
    public void testGetInfo() throws Exception {
        testGetter(infoMock, docGeneratorConfig::getInfo);
    }

    @Test
    public void testGetInfoForDefault() throws Exception {
        Info infoMock = mock(Info.class);

        Whitebox.setInternalState(docGeneratorConfig, "info", (Object) null);
        when(defaultValueProviderMock.getInfo()).thenReturn(infoMock);

        Info result = docGeneratorConfig.getInfo();

        assertSame(result, infoMock);
    }

    @Test
    public void testGetInfoForNull() throws Exception {
        Whitebox.setInternalState(docGeneratorConfig, "info", (Object) null);
        Whitebox.setInternalState(docGeneratorConfig, "defaultValueProvider", (Object) null);

        Info result = docGeneratorConfig.getInfo();

        assertNull(result);
    }

    @Test
    public void testGetSwaggerDirectory() throws Exception {
        final String buildDirectory = "mojoBuildDir";

        Whitebox.setInternalState(docGeneratorConfig, "buildDirectory", buildDirectory);

        String result = docGeneratorConfig.getSwaggerDirectory();

        assertEquals(result, buildDirectory + "/generated-swagger-ui");
    }
}
