package com.packleader.rapid.plugin;

import lombok.SneakyThrows;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Component(role = RapidDependencyHelper.class)
public class RapidDependencyHelper {

    @Requirement
    private MavenProject mavenProject;

    @Requirement
    private ZipFileHelper zipFileHelper;

    private static final String CHARSET = StandardCharsets.UTF_8.name();
    private static final String SPRING_BOOT_PREFIX = "BOOT-INF/classes/";

    @SneakyThrows
    public void unpackDependencies() {
        File destDir = new File( mavenProject.getBuild().getDirectory() + "/unpacked-dependencies");

        unpackDependencies(mavenProject.getCompileClasspathElements(), destDir);

        File classDir = new File(destDir, SPRING_BOOT_PREFIX);
        addToClassLoader(classDir);
    }

    private void unpackDependencies(Collection<String> dependencies, File destDir) throws IOException {
        for(String dependency : dependencies) {
            dependency = URLDecoder.decode(dependency, CHARSET);
            File dependencyFile = new File(dependency);
            if(dependencyFile.isFile()) {
                zipFileHelper.unpack(dependencyFile, destDir, e -> e.getName().startsWith(SPRING_BOOT_PREFIX));
            }
        }
    }

    private void addToClassLoader(File classPath) throws DuplicateRealmException, MalformedURLException {
        ClassWorld world = new ClassWorld();
        Thread currentThread = Thread.currentThread();
        ClassRealm realm = world.newRealm("rapid.dependencies", currentThread.getContextClassLoader());
        ClassRealm childRealm = realm.createChildRealm("unpacked");

        classPath.mkdirs();
        URL url = classPath.toURI().toURL();
        childRealm.addURL(url);
        currentThread.setContextClassLoader(childRealm);
    }

}
