package com.packleader.rapid.plugin;

import com.packleader.rapid.BaseTest;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest(RapidDependencyHelper.class)
public class RapidDependencyHelperTest extends BaseTest {

    @Mock
    private MavenProject mavenProjectMock;

    @Mock
    private ZipFileHelper zipFileHelperMock;

    @InjectMocks
    private RapidDependencyHelper rapidDependencyHelper;

    private static final String SPRING_BOOT_PREFIX = "BOOT-INF/classes/";

    @Test
    public void testUnpackDependencies() throws Exception {
        mockStatic(URLDecoder.class);
        mockStatic(Thread.class);
        final String buildDirectory = "build-dir";
        final String dependenciesDirectory = buildDirectory + "/unpacked-dependencies";
        final String dependency1 = "dep1.jar";
        final String dependency2 = "dep12jar";
        final String dependencyUrl1 = "file://dep1.jar";
        final String dependencyUrl2 = "file://dep12jar";
        final String charset = StandardCharsets.UTF_8.name();
        List<String> dependencies = Arrays.asList(dependency1, dependency2);

        Build buildMock = mock(Build.class);
        File destDirMock = mock(File.class);
        File dependencyFileMock1 = mock(File.class);
        File dependencyFileMock2 = mock(File.class);
        ClassWorld classWorldMock = mock(ClassWorld.class);
        Thread currentThreadMock = mock(Thread.class);
        File classDirMock = mock(File.class);
        URI classDirUriMock = mock(URI.class);
        URL classDirUrlMock = mock(URL.class);
        ClassLoader classLoaderMock = mock(ClassLoader.class);
        ClassRealm realmMock = mock(ClassRealm.class);
        ClassRealm childRealmMock = mock(ClassRealm.class);

        when(mavenProjectMock.getBuild()).thenReturn(buildMock);
        when(buildMock.getDirectory()).thenReturn(buildDirectory);
        whenNew(File.class).withArguments(dependenciesDirectory).thenReturn(destDirMock);
        when(mavenProjectMock.getCompileClasspathElements()).thenReturn(dependencies);
        when(URLDecoder.decode(dependency1, charset)).thenReturn(dependencyUrl1);
        when(URLDecoder.decode(dependency2, charset)).thenReturn(dependencyUrl2);
        whenNew(File.class).withArguments(dependencyUrl1).thenReturn(dependencyFileMock1);
        whenNew(File.class).withArguments(dependencyUrl2).thenReturn(dependencyFileMock2);
        when(dependencyFileMock1.isFile()).thenReturn(false);
        when(dependencyFileMock2.isFile()).thenReturn(true);
        whenNew(File.class).withArguments(destDirMock, SPRING_BOOT_PREFIX).thenReturn(classDirMock);
        when(classDirMock.toURI()).thenReturn(classDirUriMock);
        when(classDirUriMock.toURL()).thenReturn(classDirUrlMock);
        whenNew(ClassWorld.class).withNoArguments().thenReturn(classWorldMock);
        when(Thread.currentThread()).thenReturn(currentThreadMock);
        when(currentThreadMock.getContextClassLoader()).thenReturn(classLoaderMock);
        when(classWorldMock.newRealm("rapid.dependencies", classLoaderMock)).thenReturn(realmMock);
        when(realmMock.createChildRealm("unpacked")).thenReturn(childRealmMock);

        rapidDependencyHelper.unpackDependencies();

        Mockito.verify(zipFileHelperMock, never()).unpack(eq(dependencyFileMock1), eq(destDirMock), any());
        Mockito.verify(zipFileHelperMock).unpack(eq(dependencyFileMock2), eq(destDirMock), any());
        Mockito.verify(childRealmMock).addURL(classDirUrlMock);
        Mockito.verify(currentThreadMock).setContextClassLoader(childRealmMock);
    }

}