package com.packleader.rapid.plugin.config;

import com.packleader.rapid.config.DefaultValueProvider;
import io.swagger.models.Info;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(role = DefaultValueProvider.class)
public class MavenDefaultValueProvider implements DefaultValueProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(MavenDefaultValueProvider.class);

    @Requirement
    private MavenProject mavenProject;

    public MavenProject getMavenProject() {
        return mavenProject;
    }

    @Override
    public String getBuildDirectory() {
        return mavenProject.getBuild().getDirectory();
    }

    @Override
    public String getOutputDirectory() {
        return mavenProject.getBuild().getOutputDirectory();
    }

    @Override
    public Info getInfo() {
        return buildInfo();
    }

    @Override
    public String getGeneratedSourcesDirectory() {
        return getBuildDirectory() + "/generated-sources";
    }

    private Info buildInfo() {
        String title = mavenProject.getName();
        String version = mavenProject.getVersion();

        return buildInfo(title, version);
    }

    private Info buildInfo(String title, String version) {
        LOGGER.info("Building Info property from Maven parameters {}, {}", title, version);

        Info info = new Info();
        info.setTitle(title);
        info.setVersion(version);
        return info;
    }
}
