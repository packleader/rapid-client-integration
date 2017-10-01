package com.packleader.rapid.plugin;


import com.github.kongchen.swagger.docgen.mavenplugin.IncludeProjectDependenciesComponentConfigurator;
import org.apache.maven.model.Resource;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @plexus.component role="org.codehaus.plexus.component.configurator.ComponentConfigurator"
 * role-hint="include-project-dependencies-rapid"
 * @plexus.requirement role="org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup"
 * role-hint="default"
 */
public class RapidComponentConfigurator extends IncludeProjectDependenciesComponentConfigurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RapidComponentConfigurator.class);

    @Override
    public void configureComponent(Object component, PlexusConfiguration configuration,
                                   ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm,
                                   ConfigurationListener listener)
            throws ComponentConfigurationException {
        super.configureComponent(component, configuration, expressionEvaluator, containerRealm, listener);

        addResourcesToClassRealm(expressionEvaluator, containerRealm);

        ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter();
        converter.processConfiguration(converterLookup, component, containerRealm.getClassLoader(), configuration,
                expressionEvaluator, listener);
    }

    private void addResourcesToClassRealm(ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm) throws ComponentConfigurationException {
        try {
            List<Resource> resourceList = (List<Resource>) expressionEvaluator.evaluate("${project.build.resources}");
            for (Resource resource : resourceList) {
                addResourceToClassRealm(resource, containerRealm);
            }
        } catch (ExpressionEvaluationException | IOException e) {
            throw new ComponentConfigurationException("Unable to add resources to class realm", e);
        }
    }

    private void addResourceToClassRealm(Resource resource, ClassRealm containerRealm) throws IOException {
        final String directory = resource.getDirectory();

        if (directory != null) {
            File file = new File(directory);
            if(file.isDirectory()) {
                URL url = file.toURI().toURL();
                LOGGER.info("Adding resource classpath " + url);
                containerRealm.addConstituent(url);
            }
        }
    }

}
