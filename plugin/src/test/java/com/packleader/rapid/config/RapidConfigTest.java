package com.packleader.rapid.config;

import com.packleader.rapid.BaseTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.*;

public class RapidConfigTest extends BaseTest {

    private static final String BUILD_DIRECTORY = "mojoBuildDir";

    @Mock
    private DefaultValueProvider defaultValueProviderMock;

    @InjectMocks
    private RapidConfig rapidConfig;

    @BeforeMethod
    @Override
    public void init() throws Exception {
        super.init();

        Whitebox.setInternalState(rapidConfig, "buildDirectory", BUILD_DIRECTORY);
    }

    @Test
    public void testGetBuildDirectory() throws Exception {
        testGetter(BUILD_DIRECTORY, rapidConfig::getBuildDirectory);
    }

    @Test
    public void testGetBuildDirectoryForDefault() throws Exception {
        final String buildDir = "mojo_build_dir";

        Whitebox.setInternalState(rapidConfig, "buildDirectory", (Object) null);
        when(defaultValueProviderMock.getBuildDirectory()).thenReturn(buildDir);

        String result = rapidConfig.getBuildDirectory();

        assertSame(result, buildDir);
    }

    @Test
    public void testGetBuildDirectoryForNull() throws Exception {
        Whitebox.setInternalState(rapidConfig, "buildDirectory", (Object) null);
        Whitebox.setInternalState(rapidConfig, "defaultValueProvider", (Object) null);

        String result = rapidConfig.getBuildDirectory();

        assertNull(result);
    }

    @Test
    public void testGetSwaggerPath() throws Exception {
        String result = rapidConfig.getSwaggerPath();

        assertEquals(result, BUILD_DIRECTORY + "/generated-swagger-ui");
    }

    @Test
    public void testGetSwaggerFile() throws Exception {
        String result = rapidConfig.getSwaggerFile();

        assertEquals(result, BUILD_DIRECTORY + "/generated-swagger-ui/swagger.json");
    }
}
