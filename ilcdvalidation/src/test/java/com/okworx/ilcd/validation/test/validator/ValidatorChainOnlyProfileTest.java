package com.okworx.ilcd.validation.test.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.okworx.ilcd.validation.ValidatorChain;
import com.okworx.ilcd.validation.ValidatorChainFactory;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.profile.MavenCoordinates;
import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.profile.ProfileManager;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ValidatorChainOnlyProfileTest extends AbstractValidatorTest {

    protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

    protected ValidatorChain validator;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        ProfileManager.getInstance().reset(true);

        String groupId = "com.okworx.ilcd.validation.profiles";
        String artifactId = "EF-1.0";
        String version = "1.0.11";
        MavenCoordinates mc = new MavenCoordinates(groupId, artifactId, version);

        ProfileManager pm = ProfileManager.getInstance();

        Profile p = pm.registerProfile(mc);
        validator = ValidatorChainFactory.fromProfileActiveAspects(p);

    }

    @After
    public void tearDown() throws Exception {
    }

    private boolean runValidation(File file) {

        validator.reset();

        log.info("setting file");

        validator.setObjectsToValidate(file);

        log.info("validating");

        boolean result = validator.validate();

        for (IValidationEvent event : validator.getEventsList().getEvents()) {
            log.info(event);
        }

        return result;
    }

    @Test
    public void testPass() {
        boolean result = runValidation(new File("src/test/resources/archives/valid.zip"));

        assertTrue(result);
    }
}
