package com.okworx.ilcd.validation.profile.update;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.okworx.ilcd.validation.profile.MavenCoordinates;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class MavenVersionChecker {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(MavenVersionChecker.class);

    private static final String MAVEN_CENTRAL_BASE_URL = "https://repo1.maven.org/maven2/";
    private static final String MAVEN_METADATA_XML = "maven-metadata.xml";
    
    /**
     * Check if a newer version exists for the artifact with the given Maven coordinates
     * 
     * @param coordinates The Maven coordinates of the artifact
     * @return The newer version number if one exists, null otherwise
     */
    public static String checkForNewerVersion(MavenCoordinates coordinates) {
        return checkForNewerVersion(
            coordinates.getGroupId(),
            coordinates.getArtifactId(),
            coordinates.getVersion()
        );
    }
    
    /**
     * Check if a newer version exists for the artifact with the given Maven coordinates
     * 
     * @param groupId The group ID of the artifact
     * @param artifactId The artifact ID
     * @param currentVersion The current version in use
     * @return The newer version number if one exists, null otherwise
     */
    public static String checkForNewerVersion(String groupId, String artifactId, String currentVersion) {
        try {
            String basePath = groupId.replace('.', '/') + "/" + artifactId + "/";
            String metadataUrl = MAVEN_CENTRAL_BASE_URL + basePath + MAVEN_METADATA_XML;
            
            // Get the latest version from Maven metadata
            String latestVersion = getLatestVersion(metadataUrl);
            
            if (latestVersion == null) {
                log.error("Could not determine latest version for {}:{} ",groupId, artifactId);
                return null;
            }
            
            // Compare versions using our comparator
            MavenVersionComparator comparator = new MavenVersionComparator();
            if (comparator.compare(latestVersion, currentVersion) > 0) {
                // Verify the JAR actually exists
                String jarUrl = MAVEN_CENTRAL_BASE_URL + basePath + 
                                latestVersion + "/" + 
                                artifactId + "-" + latestVersion + ".jar";
                
                if (urlExists(jarUrl)) {
                    return latestVersion;
                }
            }
            
            return null; // No newer version available
        } catch (Exception e) {
            log.error("Could not check for newer version of {}:{} ",groupId, artifactId, e);
            return null;
        }
    }

    /**
     * Fetch the latest version from Maven metadata
     */
    private static String getLatestVersion(String metadataUrl) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(metadataUrl);
        
        // Try to get the release version first
        NodeList releaseNodes = doc.getElementsByTagName("release");
        if (releaseNodes.getLength() > 0) {
            return releaseNodes.item(0).getTextContent();
        }
        
        // If no release version, get all versions and find the latest
        NodeList versionNodes = doc.getElementsByTagName("version");
        if (versionNodes.getLength() > 0) {
            List<String> versions = new ArrayList<>();
            for (int i = 0; i < versionNodes.getLength(); i++) {
                versions.add(versionNodes.item(i).getTextContent());
            }
            
            // Sort versions semantically
            versions.sort(new MavenVersionComparator());
            
            // Return the highest version (last in sorted list)
            return versions.get(versions.size() - 1);
        }
        
        return null;
    }
    
    /**
     * Check if a URL exists and is accessible
     */
    private static boolean urlExists(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            log.error("Could not check if URL {} exists", urlString, e);
            return false;
        }
    }
}