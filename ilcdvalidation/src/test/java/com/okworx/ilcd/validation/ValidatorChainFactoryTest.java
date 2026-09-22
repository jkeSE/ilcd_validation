package com.okworx.ilcd.validation;

import static org.junit.Assert.*;

import com.okworx.ilcd.validation.profile.Profile;
import java.util.Comparator;
import org.junit.Test;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ValidatorChainFactoryTest {

  @SuppressWarnings("ConstantValue")
  @Test
  public void testFromProfileActiveAspects_NullProfile() {
    // ARRANGE
    Profile profile = null;

    // ACT
    ValidatorChain result = ValidatorChainFactory.fromProfileActiveAspects(profile);

    // ASSERT
    assertNull(result);
  }

  @Test
  public void testFromProfileActiveAspects_EmptyAspects() {
    // ARRANGE
    Profile profile = new TestProfile("", null);

    // ACT
    ValidatorChain result = ValidatorChainFactory.fromProfileActiveAspects(profile);

    // ASSERT
    assertNotNull(result);
    assertEquals(0, result.getValidators().size());
    assertNotNull(result.getProfile()); // Not null is sufficient here
  }

  @Test
  public void testFromProfileActiveAspects_CorrectValidatorTypes() {
    // ARRANGE
    String aspects = LinkValidator.ASPECT_NAME + "," +
                     SchemaValidator.ASPECT_NAME + "," +
                     CategoryValidator.ASPECT_NAME;
    Profile profile = new TestProfile(aspects, null);

    // ACT
    ValidatorChain result = ValidatorChainFactory.fromProfileActiveAspects(profile);

    // ASSERT
    assertNotNull(result);
    List<IDatasetsValidator> validators = result.getValidators();
    assertEquals(3, validators.size());

    Map<String, Integer> validatorClassCounts = new HashMap<>();
    for (IDatasetsValidator validator : validators) {
      String className = validator.getClass().getSimpleName();
      validatorClassCounts.put(className, validatorClassCounts.getOrDefault(className, 0) + 1);
    }

    assertEquals("Expected one LinkValidator", 1, validatorClassCounts.getOrDefault("LinkValidator", 0).intValue());
    assertEquals("Expected one SchemaValidator", 1, validatorClassCounts.getOrDefault("SchemaValidator", 0).intValue());
    assertEquals("Expected one CategoryValidator", 1, validatorClassCounts.getOrDefault("CategoryValidator", 0).intValue());
  }

  @SuppressWarnings("ExtractMethodRecommender")
  @Test
  public void testFromProfileActiveAspects_AllValidatorTypes() {
    // ARRANGE
    // Try to include all validator aspect names using constants -- will be outdated at some point :(
    String aspects = CategoryValidator.ASPECT_NAME + "," +
                     LinkValidator.ASPECT_NAME + "," +
                     OrphansValidator.ASPECT_NAME + "," +
                     ReferenceFlowValidator.ASPECT_NAME + "," +
                     SchemaValidator.ASPECT_NAME + "," +
                     XSLTStylesheetValidator.ASPECT_NAME;
    Profile profile = new TestProfile(aspects, null);

    // ACT
    ValidatorChain result = ValidatorChainFactory.fromProfileActiveAspects(profile);

    // ASSERT
    assertNotNull(result);
    List<IDatasetsValidator> validators = result.getValidators();
    assertEquals(6, validators.size());

    Map<String, Integer> aspectNameCounts = new HashMap<>();
    for (IDatasetsValidator validator : validators) {
      String aspectName = validator.getAspectName();
      aspectNameCounts.put(aspectName, aspectNameCounts.getOrDefault(aspectName, 0) + 1);
    }

    assertEquals("Expected one " + CategoryValidator.ASPECT_NAME, 1,
        aspectNameCounts.getOrDefault(CategoryValidator.ASPECT_NAME, 0).intValue());
    assertEquals("Expected one " + LinkValidator.ASPECT_NAME, 1,
        aspectNameCounts.getOrDefault(LinkValidator.ASPECT_NAME, 0).intValue());
    assertEquals("Expected one " + OrphansValidator.ASPECT_NAME, 1,
        aspectNameCounts.getOrDefault(OrphansValidator.ASPECT_NAME, 0).intValue());
    assertEquals("Expected one " + ReferenceFlowValidator.ASPECT_NAME, 1,
        aspectNameCounts.getOrDefault(ReferenceFlowValidator.ASPECT_NAME, 0).intValue());
    assertEquals("Expected one " + SchemaValidator.ASPECT_NAME, 1,
        aspectNameCounts.getOrDefault(SchemaValidator.ASPECT_NAME, 0).intValue());
    assertEquals("Expected one " + XSLTStylesheetValidator.ASPECT_NAME, 1,
        aspectNameCounts.getOrDefault(XSLTStylesheetValidator.ASPECT_NAME, 0).intValue());
  }

  @Test
  public void testFromProfileActiveAspects_UnknownAspect() {
    // ARRANGE
    Profile profile = new TestProfile(LinkValidator.ASPECT_NAME + ",Unknown Aspect", null);

    // ACT
    ValidatorChain result = ValidatorChainFactory.fromProfileActiveAspects(profile);

    // ASSERT
    assertNotNull(result);
    assertEquals(1, result.getValidators().size());
    assertEquals(LinkValidator.ASPECT_NAME, result.getValidators().get(0).getAspectName());
  }

  @Test
  public void testFromProfileSupportedAspects_CorrectAspectNames() {
    // ARRANGE
    String aspects = SchemaValidator.ASPECT_NAME + "," + CategoryValidator.ASPECT_NAME;
    Profile profile = new TestProfile(null, aspects);

    // ACT
    ValidatorChain result = ValidatorChainFactory.fromProfileSupportedAspects(profile);

    // ASSERT
    assertNotNull(result);
    List<IDatasetsValidator> validators = result.getValidators();
    assertEquals(2, validators.size());

    // Sort validators by aspect name for consistent checking
    validators.sort(Comparator.comparing(IValidator::getAspectName));

    assertEquals(CategoryValidator.ASPECT_NAME, validators.get(0).getAspectName());
    assertEquals(SchemaValidator.ASPECT_NAME, validators.get(1).getAspectName());
  }

  @Test
  public void testDuplicateAspectNames() {
    // ARRANGE
    String duplicateAspects = LinkValidator.ASPECT_NAME + "," + LinkValidator.ASPECT_NAME;
    Profile profile = new TestProfile(duplicateAspects, null);

    // ACT
    ValidatorChain result = ValidatorChainFactory.fromProfileActiveAspects(profile);

    // ASSERT
    assertNotNull(result);
    List<IDatasetsValidator> validators = result.getValidators();
    // Should only have one validator despite the duplicate aspect name
    assertEquals(1, validators.size());
    assertEquals(LinkValidator.ASPECT_NAME, validators.get(0).getAspectName());
  }

  @Test
  public void testFromProfileSupportedAspects_NullAspects() {
    // ARRANGE
    Profile profile = new TestProfile(null, null);

    // ACT
    ValidatorChain result = ValidatorChainFactory.fromProfileSupportedAspects(profile);

    // ASSERT
    assertNotNull(result);
    assertEquals(0, result.getValidators().size());
    assertNotNull(result.getProfile());
  }

  @Test
  public void testWhitespaceHandling() {
    // ARRANGE
    String aspectsWithWhitespace = " " + LinkValidator.ASPECT_NAME + " , " + SchemaValidator.ASPECT_NAME + " ";
    Profile profile = new TestProfile(aspectsWithWhitespace, null);

    // ACT
    ValidatorChain result = ValidatorChainFactory.fromProfileActiveAspects(profile);

    // ASSERT
    assertNotNull(result);
    List<IDatasetsValidator> validators = result.getValidators();
    assertEquals(2, validators.size());

    // Check that validators have the correct aspect names, regardless of input whitespace
    Map<String, Boolean> foundAspects = new HashMap<>();
    for (IDatasetsValidator validator : validators) {
      foundAspects.put(validator.getAspectName(), true);
    }

    assertTrue("Links validator not found", foundAspects.containsKey(LinkValidator.ASPECT_NAME));
    assertTrue("Schema validator not found", foundAspects.containsKey(SchemaValidator.ASPECT_NAME));
  }

  static class TestProfile extends Profile {
    private final String activeAspects;
    private final String supportedAspects;

    public TestProfile(String activeAspects, String supportedAspects) {
      super(null);
      this.activeAspects = activeAspects;
      this.supportedAspects = supportedAspects;
    }

    @Override
    public String getActiveAspects() {
      return activeAspects;
    }

    @Override
    public String getSupportedAspects() {
      return supportedAspects;
    }
  }
}