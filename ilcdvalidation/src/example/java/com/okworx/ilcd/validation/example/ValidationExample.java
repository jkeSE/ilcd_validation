package com.okworx.ilcd.validation.example;

import java.io.File;
import java.net.MalformedURLException;

import com.okworx.ilcd.validation.*;
import com.okworx.ilcd.validation.events.EventsList;
import com.okworx.ilcd.validation.events.Severity;
import com.okworx.ilcd.validation.profile.MavenCoordinates;
import com.okworx.ilcd.validation.profile.Profile;

import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.exception.InvalidProfileException;
import com.okworx.ilcd.validation.profile.ProfileManager;

public class ValidationExample {

    public static void main(String[] args) throws InvalidProfileException, MalformedURLException {
        ValidationExample s = new ValidationExample();
        s.runValidation();
    }

	private void setup() {
        /*
            We're setting up the ProfileManager with a custom cache directory where profiles will be stored and
            disabling the default profile registration.
         */
        new ProfileManager.ProfileManagerBuilder()
                .registerDefaultProfiles(false, false)
                .cacheDir(new File("/tmp/ilcdvalidation-cache"))
                .build();
    }
	
	public void runValidation() throws InvalidProfileException, MalformedURLException {
        /*
            setup ProfileManager
         */
        setup();


        /*
            Let's get the latest profile for PEF/OEF 3.1 using its Maven coordinates. Omitting the version number will get us the latest version from Maven Central.
         */
        Profile profile = ProfileManager.getInstance().registerProfile(new MavenCoordinates("com.okworx.ilcd.validation.profiles", "EF-3.1", null));

        /*
            Alternatively, we could also load a profile from the file system.
         */
        //Profile profile = ProfileManager.getInstance().registerProfile(new URL("file:/path/to/ilcdvalidation/target/profiles/EF-3.1-3.2.5.jar"));

        /*
            Alternatively, if we want to validate ILCD+EPD data, we can use the ECO Platform Digital Data Requirements (DDR) 1.1 profile.
            Omitting the version number will get us the latest version.
         */
        //Profile profile = ProfileManager.getInstance().registerProfile(new MavenCoordinates("com.okworx.ilcd.validation.profiles", "EPD-1.2-ECOPLATFORM_DDR_1.1", null));

        /*
            We can also use a specific version number, of course.
         */
        //Profile profile = ProfileManager.getInstance().registerProfile(new MavenCoordinates("com.okworx.ilcd.validation.profiles", "EPD-1.2-ECOPLATFORM_DDR_1.1", "1.0.0"));


        System.out.println( "Validating against profile " + profile.getName() + " v" + profile.getVersion());


        /*
            This factory method will create a ValidatorChain for the given profile and its default aspects with standard defaults already set for each validator.
         */
        ValidatorChain validatorChain = ValidatorChainFactory.fromProfileActiveAspects(profile);


        /*
            Now we can set the objects to validate, which can be either a single file (ZIP or XML) or a directory.
         */
        validatorChain.setObjectsToValidate(new File("src/test/resources/datasets/ReferenceFlows/test_pass.zip"));

        /*
            Or, if we rather wanted to validate some ILCD+EPD data:
         */
        //validatorChain.setObjectsToValidate(new File("src/test/resources/datasets/EPD/EPD.zip"));


        /*
            Optionally, we can set the validator to run in batch mode, which will generate a nice
            spreadsheet with the detailed validation results in the same directory as the data.
         */
        validatorChain.setBatchMode(true);

        /*
           If we want to add some extra properties to the validation context which will be written
           to the spreadsheet log, we can do that here.
         */
        validatorChain.getExtraProperties().put("by:", "ILCD Validation Library validation example");


        /*
           Now we can run the validation.
         */
        boolean result = validatorChain.validate();


        /*
            The validation messages are in the EventsList object, we're writing them to the console here.
         */
        printResults(validatorChain.getEventsList());

        System.out.println("\nValidation result: " + (result ? "PASSED" : "FAILED"));


        /*
            There are also some statistics available.
         */
        System.out.println(validatorChain.getEventsList().size() + " validation events (" + validatorChain.getEventsList().getErrorCount() + " errors, " + validatorChain.getEventsList().getWarningCount() + " warnings)");
		System.out.println(validatorChain.getStatistics().getTotalInvalidCount() + " datasets found to be invalid ");
        System.out.println(validatorChain.getStatistics().getValidProcessesCount() + " valid process datasets");
	}

    private void printResults(EventsList eventsList) {
        System.out.println("\n\nValidation events:");
        for (IValidationEvent event : eventsList.getEvents()) {
            if (Severity.WARNING.equals(event.getSeverity())) {
                /* here, you could call log.warn(event) */
                System.out.println(event.getSeverity() + " | " + event.getAspect() + " | " + event.getReference().getDatasetType() + " | " + event.getReference().getUuid() + " | "  + event.getMessage());
            } else if (Severity.ERROR.equals(event.getSeverity())) {
                /* here, you could call log.error(event) */
                System.out.println(event.getSeverity() + " | " + event.getAspect() + " | " + event.getReference().getDatasetType() + " | " + event.getReference().getUuid() + " | "  + event.getMessage());
            }
        }
        System.out.println("\n\n");
    }

}
