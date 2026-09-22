package com.okworx.ilcd.validation.profile;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.nio.file.Path;

public class MavenCoordinatesReader {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(MavenCoordinatesReader.class);

    public static MavenCoordinates readMavenCoordinatesFromJar(Path jarPath) throws IOException {
        log.trace("Reading Maven coordinates from JAR {}", jarPath);

        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            // Look for pom.properties files in the JAR
            Enumeration<JarEntry> entries = jarFile.entries();
            
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                log.trace("Reading entry {}", entry.getName());

                // Check if the entry is a pom.properties file
                if (name.endsWith("pom.properties") && name.startsWith("META-INF/maven/")) {
                    try (InputStream is = jarFile.getInputStream(entry)) {
                        log.trace("opening stream for entry {} ", entry.getName());
                        Properties properties = new Properties();
                        properties.load(is);
                        
                        String groupId = properties.getProperty("groupId");
                        String artifactId = properties.getProperty("artifactId");
                        String version = properties.getProperty("version");

                        log.trace("{}:{}:{}", groupId, artifactId, version);

                        if (groupId != null && artifactId != null && version != null) {
                            return new MavenCoordinates(groupId, artifactId, version);
                        }
                    } catch (IOException e) {
                        log.error("Could not read Maven coordinates from JAR {}", jarPath, e);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Could not read Maven coordinates from JAR {}", jarPath, e);
        }
        
        return null; // No Maven coordinates found
    }
}