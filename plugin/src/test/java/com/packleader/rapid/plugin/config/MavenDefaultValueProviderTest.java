package com.packleader.rapid.plugin.config;

import com.packleader.rapid.BaseTest;
import io.swagger.models.Info;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertEquals;

public class MavenDefaultValueProviderTest extends BaseTest {

    private static final String BUILD_DIRECTORY = "mojoBuildDir";
    private static final String OUTPUT_DIRECTORY = "mojoOutputDir";

    @Mock
    private MavenProject mavenProjectMock;

    @InjectMocks
    private MavenDefaultValueProvider mavenDefaultValueProvider;

    @BeforeMethod
    @Override
    public void init() throws Exception {
        super.init();

        Build buildMock = mock(Build.class);
        when(mavenProjectMock.getBuild()).thenReturn(buildMock);
        when(buildMock.getDirectory()).thenReturn(BUILD_DIRECTORY);
        when(buildMock.getOutputDirectory()).thenReturn(OUTPUT_DIRECTORY);
    }

    @Test
    public void testGetMavenProject() throws Exception {
        testGetter(mavenProjectMock, mavenDefaultValueProvider::getMavenProject);
    }

    @Test
    public void testGetBuildDirectory() throws Exception {
        testGetter(BUILD_DIRECTORY, mavenDefaultValueProvider::getBuildDirectory);
    }

    @Test
    public void testGetInfo() throws Exception {
        final String name = "projectName";
        final String version = "projectVersion";

        when(mavenProjectMock.getName()).thenReturn(name);
        when(mavenProjectMock.getVersion()).thenReturn(version);

        Info result = mavenDefaultValueProvider.getInfo();

        Info expected = new Info();
        expected.setTitle(name);
        expected.setVersion(version);
        assertEquals(result, expected);
    }

    @Test
    public void testGetOutputDirectory() throws Exception {
        testGetter(OUTPUT_DIRECTORY, mavenDefaultValueProvider::getOutputDirectory);
    }

    @Test
    public void testGetGeneratedSourcesDirectory() throws Exception {
        String result = mavenDefaultValueProvider.getGeneratedSourcesDirectory();

        assertEquals(result, BUILD_DIRECTORY + "/generated-sources");
    }
}
