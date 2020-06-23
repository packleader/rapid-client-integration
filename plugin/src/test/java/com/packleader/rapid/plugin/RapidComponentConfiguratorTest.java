package com.packleader.rapid.plugin;

import com.packleader.rapid.BaseTest;
import org.apache.maven.model.Resource;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest(RapidComponentConfigurator.class)
public class RapidComponentConfiguratorTest extends BaseTest {

    @Mock
    private ConverterLookup converterLookupMock;

    @Mock
    private Object componentMock;

    @Mock
    private PlexusConfiguration configurationMock;

    @Mock
    private ExpressionEvaluator expressionEvaluatorMock;

    @Mock
    private ClassRealm containerRealmMock;

    @Mock
    private ConfigurationListener listenerMock;

    @InjectMocks
    private RapidComponentConfigurator rapidComponentConfigurator;

    @Test
    public void testConfigureComponent() throws Exception {
        final String resourceMock1Directory = "mockDirectory";
        Resource resourceMock1 = mock(Resource.class);
        Resource resourceMock2 = mock(Resource.class);
        List<Resource> resourceList = Arrays.asList(resourceMock1, resourceMock2);
        File fileMock = mock(File.class);
        URI uriMock = mock(URI.class);
        URL urlMock = mock(URL.class);
        ObjectWithFieldsConverter converterMock = mock(ObjectWithFieldsConverter.class);
        ClassLoader classLoaderMock = mock(ClassLoader.class);

        when(expressionEvaluatorMock.evaluate("${project.compileClasspathElements}")).thenReturn(Collections.emptyList());
        when(expressionEvaluatorMock.evaluate("${project.build.resources}")).thenReturn(resourceList);
        when(resourceMock1.getDirectory()).thenReturn(resourceMock1Directory);
        whenNew(File.class).withArguments(resourceMock1Directory).thenReturn(fileMock);
        when(fileMock.isDirectory()).thenReturn(true);
        when(fileMock.toURI()).thenReturn(uriMock);
        when(uriMock.toURL()).thenReturn(urlMock);
        whenNew(ObjectWithFieldsConverter.class).withNoArguments().thenReturn(converterMock);
        when(containerRealmMock.getClassLoader()).thenReturn(classLoaderMock);

        rapidComponentConfigurator.configureComponent(componentMock, configurationMock, expressionEvaluatorMock, containerRealmMock, listenerMock);

        Mockito.verify(containerRealmMock).addConstituent(urlMock);
        Mockito.verify(converterMock, times(2)).processConfiguration(converterLookupMock, componentMock, classLoaderMock, configurationMock,
                expressionEvaluatorMock, listenerMock);
    }

    @Test(expectedExceptions = ComponentConfigurationException.class, expectedExceptionsMessageRegExp = "Unable to add resources to class realm")
    public void testConfigureComponentForExpressionEvaluationException() throws Exception {
        when(expressionEvaluatorMock.evaluate("${project.compileClasspathElements}")).thenReturn(Collections.emptyList());
        when(expressionEvaluatorMock.evaluate("${project.build.resources}")).thenThrow(ExpressionEvaluationException.class);

        rapidComponentConfigurator.configureComponent(componentMock, configurationMock, expressionEvaluatorMock, containerRealmMock, listenerMock);
    }

    @Test(expectedExceptions = ComponentConfigurationException.class, expectedExceptionsMessageRegExp = "Unable to add resources to class realm")
    public void testConfigureComponentForIOException() throws Exception {
        final String resourceMockDirectory = "mockDirectory";
        Resource resourceMock1 = mock(Resource.class);
        List<Resource> resourceList = Arrays.asList(resourceMock1);
        File fileMock = mock(File.class);
        URI uriMock = mock(URI.class);

        when(expressionEvaluatorMock.evaluate("${project.compileClasspathElements}")).thenReturn(Collections.emptyList());
        when(expressionEvaluatorMock.evaluate("${project.build.resources}")).thenReturn(resourceList);
        when(resourceMock1.getDirectory()).thenReturn(resourceMockDirectory);
        whenNew(File.class).withArguments(resourceMockDirectory).thenReturn(fileMock);
        when(fileMock.isDirectory()).thenReturn(true);
        when(fileMock.toURI()).thenReturn(uriMock);
        when(uriMock.toURL()).thenThrow(MalformedURLException.class);

        rapidComponentConfigurator.configureComponent(componentMock, configurationMock, expressionEvaluatorMock, containerRealmMock, listenerMock);
    }

    @Test
    public void testConfigureComponentForNotFound() throws Exception {
        final String resourceMockDirectory = "mockDirectory";
        Resource resourceMock1 = mock(Resource.class);
        List<Resource> resourceList = Arrays.asList(resourceMock1);
        File fileMock = mock(File.class);

        when(expressionEvaluatorMock.evaluate("${project.compileClasspathElements}")).thenReturn(Collections.emptyList());
        when(expressionEvaluatorMock.evaluate("${project.build.resources}")).thenReturn(resourceList);
        when(resourceMock1.getDirectory()).thenReturn(resourceMockDirectory);
        whenNew(File.class).withArguments(resourceMockDirectory).thenReturn(fileMock);
        when(fileMock.isDirectory()).thenReturn(false);

        rapidComponentConfigurator.configureComponent(componentMock, configurationMock, expressionEvaluatorMock, containerRealmMock, listenerMock);

        Mockito.verify(fileMock, never()).toURI();
        Mockito.verify(containerRealmMock, never()).addConstituent(any());
    }
}
