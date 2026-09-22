package com.okworx.ilcd.validation;

import com.okworx.ilcd.validation.profile.Profile;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

public class ValidatorChainFactory {

    private static final Map<String, ValidatorSupplierInfo> aspectNameToValidatorSupplier;

    static {
        aspectNameToValidatorSupplier = new HashMap<>();
        aspectNameToValidatorSupplier.put(ArchiveValidator.ASPECT_NAME.toLowerCase(),
                new ValidatorSupplierInfo(ArchiveValidator::new, Collections.emptyMap()));
        aspectNameToValidatorSupplier.put(CategoryValidator.ASPECT_NAME.toLowerCase(),
                new ValidatorSupplierInfo(CategoryValidator::new, Collections.emptyMap()));
        aspectNameToValidatorSupplier.put(LinkValidator.ASPECT_NAME.toLowerCase(),
                new ValidatorSupplierInfo(LinkValidator::new, Map.of(LinkValidator.PARAM_IGNORE_REFERENCE_OBJECTS, true, LinkValidator.PARAM_IGNORE_COMPLEMENTINGPROCESS, true, LinkValidator.PARAM_IGNORE_INCLUDEDPROCESSES, true, LinkValidator.PARAM_IGNORE_REFS_TO_LCIAMETHODS, true, LinkValidator.PARAM_IGNORE_PRECEDINGDATASETVERSION, true)));
        aspectNameToValidatorSupplier.put(OrphansValidator.ASPECT_NAME.toLowerCase(),
                new ValidatorSupplierInfo(OrphansValidator::new, Map.of(OrphansValidator.PARAM_IGNORE_REFERENCE_OBJECTS, true, OrphansValidator.PARAM_IGNORE_LCIAMETHODS, true)));
        aspectNameToValidatorSupplier.put(ReferenceFlowValidator.ASPECT_NAME.toLowerCase(),
                new ValidatorSupplierInfo(ReferenceFlowValidator::new, Collections.emptyMap()));
        aspectNameToValidatorSupplier.put(SchemaValidator.ASPECT_NAME.toLowerCase(),
                new ValidatorSupplierInfo(SchemaValidator::new, Map.of(SchemaValidator.PARAM_IGNORE_REFERENCE_OBJECTS, true)));
        aspectNameToValidatorSupplier.put(XSLTStylesheetValidator.ASPECT_NAME.toLowerCase(),
                new ValidatorSupplierInfo(XSLTStylesheetValidator::new, Map.of(XSLTStylesheetValidator.PARAM_IGNORE_REFERENCE_OBJECTS, true)));
    }

    public static ValidatorChain fromProfileActiveAspects(Profile profile) {
        return fromProfileAspects(profile, Profile::getActiveAspects);
    }

    public static ValidatorChain fromProfileSupportedAspects(Profile profile) {
        return fromProfileAspects(profile, Profile::getSupportedAspects);
    }

    private static ValidatorChain fromProfileAspects(Profile profile, java.util.function.Function<Profile, String> aspectsGetter) {
        if (profile == null) {
            return null;
        }

        ValidatorChain chain = new ValidatorChain();

        if (aspectsGetter != null) {
            Set<String> aspectNames = parseAspectNames(aspectsGetter.apply(profile));
            for (String aspectName : aspectNames) {
                ValidatorSupplierInfo supplierInfo = aspectNameToValidatorSupplier.get(aspectName);
                if (supplierInfo != null) {
                    IValidator validator = supplierInfo.supplier.get();
                    // Apply parameters to the validator
                    if (!supplierInfo.parameters.isEmpty()) {
                        validator.getParameters().putAll(supplierInfo.parameters);
                    }
                    if (validator instanceof IDatasetsValidator) {
                        chain.add((IDatasetsValidator) validator);
                    }
                }
            }
        }

        chain.setProfile(profile);
        return chain;
    }

    @Nonnull
    private static Set<String> parseAspectNames(String aspectsString) {
        Set<String> result = new HashSet<>();

        if (aspectsString != null && !aspectsString.isEmpty()) {
            String[] aspects = aspectsString.split(",");
            for (String aspect : aspects) {
                String trimmed = aspect.trim();
                if (!trimmed.isEmpty()) {
                    result.add(trimmed.toLowerCase());
                }
            }
        }

        return result;
    }

    private static class ValidatorSupplierInfo {
        final Supplier<? extends IValidator> supplier;
        final Map<String, Object> parameters;

        ValidatorSupplierInfo(Supplier<? extends IValidator> supplier, Map<String, Object> parameters) {
            this.supplier = supplier;
            this.parameters = parameters;
        }
    }
}
