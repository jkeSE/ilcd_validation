package com.okworx.ilcd.validation.profile;

import com.okworx.ilcd.validation.exception.InvalidProfileException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;


/**
 * This class complements a profile with all properties from the MANIFEST.
 * Also, it handles the extraction and parsing of included profiles
 *
 * @author Dominik Ehrler
 * @version $Id: $Id
 */
public class ProfileManifestParser {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(ProfileManifestParser.class);

    /*
     * It extracts the properties from the Attributes Object and handles included profiles as well
     *
     * @param profile profile object with path and jarPath set, but otherwise empty
     * @param atts Manifest attributes object
     * @returns complete profile object
     */
    public static Profile parseManifest(Profile profile, Attributes atts) {
        return parseManifest(profile, atts, true);
    }


    /*
     * It extracts the properties from the Attributes Object.
     * When considerIncludedProfiles is true, it handles first-level included profiles.
     *
     * @param profile profile object with path and jarPath set, but otherwise empty
     * @param atts Manifest attributes object
     * @returns complete profile object
     */
    public static Profile parseManifest(Profile profile, Attributes atts, boolean considerIncludedProfiles) {
        profile.setProfileMetaDataVersion(atts.getValue("Profile-ILCD-Validation-Version"));
        log.debug("Profile metadata version is " + profile.getProfileMetaDataVersion());

        profile.setName(atts.getValue("Profile-Name"));
        profile.setVersion(atts.getValue("Profile-Version"));

        if (log.isDebugEnabled()) {
            log.debug("registering profile " + atts.getValue("Profile-Name"));
            log.debug("version " + atts.getValue("Profile-Version"));
        }

        if (supportsMetaDataVersion(profile, 1.4))
            if (log.isDebugEnabled())
                log.debug("description: " + atts.getValue("Profile-Description"));
        profile.setDescription(atts.getValue("Profile-Description"));

        if (supportsMetaDataVersion(profile, 1.5)) {
            if (log.isDebugEnabled())
                log.debug("active aspects: " + atts.getValue("Profile-ActiveAspects"));
            profile.setActiveAspects(atts.getValue("Profile-ActiveAspects"));
        }

        if(supportsMetaDataVersion(profile, 1.6)) {
            if(log.isDebugEnabled())
                log.debug("changelogs (technical / semantic): " + atts.getValue("Profile-Changelog-Technical") + " / " + atts.getValue("Profile-Changelog-Semantic"));
            setChangelogs(profile, atts);
        }

        if (supportsMetaDataVersion(profile, 1.7)) {
            if (log.isDebugEnabled())
                log.debug("supported aspects: " + atts.getValue("Profile-SupportedAspects"));
            profile.setSupportedAspects(atts.getValue("Profile-SupportedAspects"));
        }

        if (supportsMetaDataVersion(profile, 1.8)) {
            log.debug("readme: {} " , atts.getValue("Profile-Readme"));
            profile.setReadme(atts.getValue("Profile-Readme"));
        }

        if (considerIncludedProfiles) {
            setIncludes(profile, atts);
        } else {
            profile.setIncludes(new Profile.IncludedProfile[0]);
        }

        setCategories(profile, atts);

        setSchemas(profile, atts);

        setStylesheets(profile, atts);

        setReferenceElementaryFlows(profile, atts);

        setReferenceObjectsOther(profile, atts);


        return profile;
    }

    /*
     * Parses the Profile-Included-[0-9]-Jar / -Select attributes of the manifest.
     * Also it extracts and creates the included profiles.
     *
     * @param profile Profile object
     * @param atts Manifest attributes object
     * @returns complemented profile
     */
    private static Profile setIncludes(Profile profile, Attributes atts) {
        ArrayList<Profile.IncludedProfile> includes = new ArrayList<>();

        for (int i = 1; atts.getValue("Profile-Include-" + i + "-Jar") != null; i++) {
            String select = atts.getValue("Profile-Include-" + i + "-Select");
            String childJarFilename = atts.getValue("Profile-Include-" + i + "-Jar");

            Profile includedProfile = extractAndCreateIncludedProfile(profile, childJarFilename);

            includes.add(new Profile.IncludedProfile(includedProfile, select));
        }
        profile.setIncludes(includes.toArray(new Profile.IncludedProfile[includes.size()]));

        return profile;
    }

    /*
     * Extracts the given profile-jar, which is included in the given profile, and creates a profile object
     *
     * @param profile Profile object
     * @param childJarFilename the filename of the profile inside the included directory
     * @returns Profile object which represents the included profile
     */
    private static Profile extractAndCreateIncludedProfile(Profile profile, String childJarFilename) {

        if (log.isDebugEnabled())
            log.debug("extracting " + childJarFilename + " from profile " + profile.getName());

        String destination = profile.getPath().toString() + "_includes";
        File destinationDir = new File(destination);
        if (!destinationDir.exists()) destinationDir.mkdir();

        Profile includedProfile = null;
        InputStream is = null;

        try {
            String destinationFile = destination + File.separator + childJarFilename;
           	log.debug("destination file is " + destinationFile);
            
           	if (profile.getJarFile() == null) {
                throw new InvalidProfileException();
            }

           	log.debug("profile's JAR file is " + profile.getJarFile().getName());

            ZipEntry includedJar = profile.getJarFile().getEntry("includes" + "/" + childJarFilename);
            if (includedJar == null) {
                throw new FileNotFoundException("Profile could not be found!");
            }

            is = profile.getJarFile().getInputStream(includedJar);

            FileUtils.copyInputStreamToFile(is, new File(destinationFile));

            JarFile jarfile = new JarFile(new URL("file:" + destinationFile).getFile());
            includedProfile = new Profile(new URL("file:" + destinationFile));
            includedProfile.setPath(new File(destinationFile));
            ProfileManifestParser.parseManifest(includedProfile, jarfile.getManifest().getAttributes("ILCD-Validator-Profile"), false);
            includedProfile.setJarFile(jarfile);
        } catch (FileNotFoundException e) {
            log.error("Could not find included profile: " + childJarFilename);
            log.debug(e);
        } catch (IOException e) {
            log.error("Error while reading included Profile: " + childJarFilename);
            log.debug(e);
        } catch (InvalidProfileException e) {
            log.error("No JAR file found on profile: " + childJarFilename);
            log.debug(e);
        } finally {
            try {
                is.close();
            } catch (NullPointerException | IOException e) {
                log.error("Could not close input stream");
                log.debug(e);
            }
        }
        return includedProfile;
    }

    /*
     * Sets the Profile-Schema property of the given profile and all included profiles.
     *
     * @param profile Profile object
     * @param atts Manifest attributes object
     * @returns complemented profile
     */
    private static Profile setSchemas(Profile profile, Attributes atts) {
        if (atts.getValue("Profile-Schemas") != null && atts.getValue("Profile-SchemasPath") != null) {
            String schemasPath = atts.getValue("Profile-SchemasPath");
            String[] schemas = extractCommaSeparatedValues(atts.getValue("Profile-Schemas"));
            Profile.Bundle<String[]>[] schemaBundles = new Profile.Bundle[]{new Profile.Bundle(profile.getPath().toString(), schemasPath, schemas)};

            profile.setSchemaBundles(schemaBundles);
        }

        for (Profile.IncludedProfile ip : profile.getIncludes()) {
            if (ip.getSelect().contains("XML Schema Validity")) {
                if (profile.getSchemaBundles() != null) {
                    profile.setSchemaBundles(ArrayUtils.addAll(profile.getSchemaBundles(), ip.getProfile().getSchemaBundles()));
                } else {
                    profile.setSchemaBundles(ip.getProfile().getSchemaBundles());
                }
            }
        }

        return profile;
    }


    /*
     * Sets the ValidationStylesheet and StylesheetsPath properties of the given profile and all included profiles.
     *
     * @param profile Profile object
     * @param atts Manifest attributes object
     * @returns complemented profile
     */
    private static Profile setStylesheets(Profile profile, Attributes atts) {
        profile.setStylesheetsPath(atts.getValue("Profile-StylesheetsPath"));
        if (atts.getValue("Profile-ValidationStylesheet") != null) {
            String stylesheetName = atts.getValue("Profile-ValidationStylesheet");

            Profile.Bundle<String>[] newStylesheetBundle = new Profile.Bundle[]{new Profile.Bundle<String>(profile.getPath().toString(), profile.getStylesheetsPath(), profile.getDescription(), stylesheetName)};
            profile.setStylesheetBundles(newStylesheetBundle);
        }
        
        for (Profile.IncludedProfile ip : profile.getIncludes()) {
            if (ip.getSelect().contains("Profile Specific Rules") || ip.getSelect().contains("Custom Validity")) {
                if (profile.getStylesheetBundles() != null) {
                    profile.setStylesheetBundles(ArrayUtils.addAll(profile.getStylesheetBundles(), ip.getProfile().getStylesheetBundles()));
                } else {
                    profile.setStylesheetBundles(ip.getProfile().getStylesheetBundles());
                }
            }
        }

        return profile;
    }


    /*
     * Sets the Categories property of the last defined Categories (the order is profile > included 1 > included 2...)
     *
     * @param profile Profile object
     * @param atts Manifest attributes object
     * @returns complemented profile
     */
    private static Profile setCategories(Profile profile, Attributes atts) {
        if (atts.getValue("Profile-Categories") != null) {
            Profile.Bundle<String> newCategoriesBundle = new Profile.Bundle<String>(profile.getPath().toString(), profile.getStylesheetsPath(), atts.getValue("Profile-Categories"));
            profile.setCategoriesBundle(newCategoriesBundle);
        }

        for (Profile.IncludedProfile ip : profile.getIncludes()) {
            if(ip.getSelect().contains("Categories")) {
                if (ip.getProfile().getCategoriesBundle() != null) {
                    profile.setCategoriesBundle(ip.getProfile().getCategoriesBundle());
                }
            }
        }

        return profile;
    }


    /*
     * Sets the ReferenceElementaryFlow property of the last defined property (the order is profile > included 1 > included 2...)
     *
     * @param profile Profile object
     * @param atts Manifest attributes object
     * @returns complemented profile
     */
    private static Profile setReferenceElementaryFlows(Profile profile, Attributes atts) {
        String reference = atts.getValue("Profile-Reference");
        String refElementaryFlows = atts.getValue("Profile-ReferenceElementaryFlows");
        if (StringUtils.isNotEmpty(reference) && StringUtils.isNotEmpty(refElementaryFlows)) {
            Profile.Bundle<String> elementaryFlowBundle = new Profile.Bundle<String>(profile.getPath().getPath(), reference, refElementaryFlows);
            profile.setReferenceElementaryFlows(elementaryFlowBundle);
        }


        for (Profile.IncludedProfile ip : profile.getIncludes()) {
            if(ip.getSelect().contains("Reference Flows")) {
                if (ip.getProfile().getReferenceElementaryFlows() != null) {
                    profile.setReferenceElementaryFlows(ip.getProfile().getReferenceElementaryFlows());
                }
            }
        }

        return profile;
    }


    /*
     * Sets the ReferenceObject property of the given profile and all included profiles.
     *
     * @param profile Profile object
     * @param atts Manifest attributes object
     * @returns complemented profile
     */
    private static Profile setReferenceObjectsOther(Profile profile, Attributes atts) {
        String reference = atts.getValue("Profile-Reference");
        String refObjects = atts.getValue("Profile-ReferenceObjectsOther");
        if (StringUtils.isNotEmpty(reference) && StringUtils.isNotEmpty(refObjects)) {
            Profile.Bundle<String>[] bundles = new Profile.Bundle[]{new Profile.Bundle<String>(profile.getPath().toString(), reference, refObjects)};
            profile.setReferenceObjectsOther(bundles);
        }


        for (Profile.IncludedProfile ip : profile.getIncludes()) {
            if (ip.getSelect().contains("Reference Objects")) {
                if (profile.getReferenceObjectsOther() != null) {
                    profile.setReferenceObjectsOther(ArrayUtils.addAll(profile.getReferenceObjectsOther(), ip.getProfile().getReferenceObjectsOther()));
                } else {
                    profile.setReferenceObjectsOther(ip.getProfile().getReferenceObjectsOther());
                }
            }
        }

        return profile;
    }

    private static Profile setChangelogs(Profile profile, Attributes atts) {
        profile.setSemanticChangelog(atts.getValue("Profile-Changelog-Semantic"));
        profile.setTechnicalChangelog(atts.getValue("Profile-Changelog-Technical"));

        return profile;
    }

    /**
     * <p>extractCommaSeparatedValues.</p>
     *
     * @param csList a {@link java.lang.String} object.
     * @return an array of {@link java.lang.String} objects.
     */
    public static String[] extractCommaSeparatedValues(String csList) {
        StringTokenizer t = new StringTokenizer(csList, ",");

        int entries = t.countTokens();

        String[] result = new String[entries];

        int i = 0;
        while (t.hasMoreElements()) {
            result[i] = (String) t.nextElement();
            i++;
        }

        return result;
    }
    
	/**
	 * checks if a profile supports a given metadata version
	 * 
	 * @param profile the profile
	 * @param version the metadata version
	 * @return true if this profile supports the given metadata version
	 */
    private static boolean supportsMetaDataVersion(Profile profile, Double version) {
        if (version == null || profile.getProfileMetaDataVersion() == null)
            return false;
        return (profile.getProfileMetaDataVersion() >= version);
    }

}
