package com.okworx.ilcd.validation.test.profile;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.profile.ProfileManifestParser;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProfileManifestParserTest {

    @Before
    public void reset() {
        ProfileManager.getInstance().reset(true);
    }

    @Test
    public void testExtractCommaSeparatedValues() throws IOException {

        ProfileManager pm = ProfileManager.getInstance();
        String line1 = "foo,bar,foobar";
        String[] result1 = ProfileManifestParser.extractCommaSeparatedValues(line1);
        assertArrayEquals(new String[]{"foo", "bar", "foobar"}, result1);
    }
}
