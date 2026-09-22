package com.okworx.ilcd.validation.test;

import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.profile.ProfileManager;

import java.net.URL;

public final class TestConfig {

    public static final String EL_PROFILE_VERSION = "1.0.7";

    public static final String EF_PROFILE_GROUPID = "com.okworx.ilcd.validation.profiles";
    public static final String EF_PROFILE_ARTIFACTID = "EF-3.1";
    public static final String EF_PROFILE_VERSION = "3.2.5";

    public static final String EPD_PROFILE_VERSION = "1.0.43";
    public static final String TEST_BASE_PROFILE_VERSION = "1.0.5";
    public static final String TEST_COMPOSITION_PROFILE_VERSION = "1.0.0";
    public static final String TEST_STYLESHEET_PROFILE_VERSION = "1.0.0";
    public static final String TEST_SCHEMA_PROFILE_VERSION = "1.0.0";
    public static final String TEST_CATEGORIES_PROFILE_VERSION = "1.0.0";
    public static final String TEST_REFOBJECTS_PROFILE_VERSION = "1.0.0";

    private TestConfig() {
    }

    public static URL getEPDProfileURL() {
        try {
            return new URL("file:target/profiles/EPD-1.1-OEKOBAUDAT-" + EPD_PROFILE_VERSION + ".jar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getEFProfileURL() {
        try {
            return new URL("file:target/profiles/EF-3.1-" + EF_PROFILE_VERSION + ".jar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getELProfileURL() {
        try {
            return new URL("file:target/profiles/ILCD-1.1-EL-" + EL_PROFILE_VERSION + ".jar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getBaseTestProfileURL() {
        try {
            return new URL("file:target/profiles/test-base-" + TEST_BASE_PROFILE_VERSION + ".jar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getAddStylesheetTestProfileURL() {
        try {
            return new URL("file:target/profiles/test-add-stylesheet-" + TEST_STYLESHEET_PROFILE_VERSION + ".jar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getCompositionTestProfileURL() {
        try {
            return new URL("file:target/profiles/test-composite-" + TEST_COMPOSITION_PROFILE_VERSION + ".jar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getRefObjectsTestProfileURL() {
        try {
            return new URL("file:target/profiles/test-add-refobjects-" + TEST_REFOBJECTS_PROFILE_VERSION + ".jar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getSchemaTestProfileURL() {
        try {
            return new URL("file:target/profiles/test-add-schema-" + TEST_SCHEMA_PROFILE_VERSION + ".jar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getCategoriesTestProfileURL() {
        try {
            return new URL("file:target/profiles/test-add-categories-" + TEST_CATEGORIES_PROFILE_VERSION + ".jar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Profile getEPDProfile() {
        try {
            return ProfileManager.getInstance().registerProfile(getEPDProfileURL());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Profile getEFProfile() {
        try {
            return ProfileManager.getInstance().registerProfile(getEFProfileURL());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Profile getELProfile() {
        try {
            return ProfileManager.getInstance().registerProfile(getELProfileURL());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
