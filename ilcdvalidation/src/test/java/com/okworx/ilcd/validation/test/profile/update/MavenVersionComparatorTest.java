package com.okworx.ilcd.validation.test.profile.update;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.okworx.ilcd.validation.profile.update.MavenVersionComparator;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class MavenVersionComparatorTest {

  private final MavenVersionComparator comparator = new MavenVersionComparator();

  /**
   * Helper method to verify version ordering
   *
   * @param versions List of versions in expected ascending order
   */
  private void assertVersionOrder(List<String> versions) {
    for (int i = 0; i < versions.size() - 1; i++) {
      String lower = versions.get(i);
      String higher = versions.get(i + 1);

      int result = comparator.compare(lower, higher);
      assertTrue(String.format("Expected %s < %s but got %d", lower, higher, result), result < 0);

      result = comparator.compare(higher, lower);
      assertTrue(String.format("Expected %s > %s but got %d", higher, lower, result), result > 0);

      //noinspection EqualsWithItself
      result = comparator.compare(lower, lower);
      assertEquals(String.format("Expected %s == %s but got %d", lower, lower, result), 0, result);

      //noinspection EqualsWithItself
      result = comparator.compare(higher, higher);
      assertEquals(String.format("Expected %s == %s but got %d", higher, higher, result), 0,
          result);
    }
  }

  @Test
  public void testBasicVersions() {
    List<String> versions = Arrays.asList("1.0.0", "1.0.1", "1.1.0", "1.1.1", "1.10.0", "2.0.0");
    assertVersionOrder(versions);
  }

  @Test
  public void testDifferentNumberOfParts() {
    // Equal versions with different parts
    assertEquals(0, comparator.compare("1.0", "1.0.0"));
    assertEquals(0, comparator.compare("1.2", "1.2.0.0"));

    // Different versions with different parts
    assertTrue(comparator.compare("1.0", "1.0.1") < 0);
    assertTrue(comparator.compare("1", "1.0.1") < 0);
    assertTrue(comparator.compare("1.0.0", "1.1") < 0);
  }

  @Test
  public void testQualifiers() {
    List<String> versions = Arrays.asList("1.0-alpha", "1.0-beta", "1.0-milestone", "1.0-rc",
        "1.0-snapshot", "1.0", "1.0-sp");
    assertVersionOrder(versions);
  }

  @Test
  public void testQualifierAliases() {
    assertEquals(0, comparator.compare("1.0-a", "1.0-alpha"));
    assertEquals(0, comparator.compare("1.0-b", "1.0-beta"));
    assertEquals(0, comparator.compare("1.0-m", "1.0-milestone"));
    assertEquals(0, comparator.compare("1.0-cr", "1.0-rc"));
    assertEquals(0, comparator.compare("1.0-ga", "1.0-final"));
    assertEquals(0, comparator.compare("1.0-release", "1.0-final"));
  }

  @Test
  public void testComplexQualifiers() {
    List<String> versions = Arrays.asList("1.0-alpha.1", "1.0-alpha.2", "1.0-beta.1", "1.0-beta.11",
        "1.0-rc.1", "1.0-rc.2", "1.0");
    assertVersionOrder(versions);

    assertTrue(comparator.compare("1.0-beta.3", "1.0-beta.10") < 0);
    assertTrue(comparator.compare("1.0-alpha.10", "1.0-beta.1") < 0);
  }

  @Test
  public void testMixedSeparators() {
    assertEquals(0, comparator.compare("1.0-alpha", "1.0_alpha"));
    assertEquals(0, comparator.compare("1.0-SNAPSHOT", "1.0_SNAPSHOT"));
    assertEquals(0, comparator.compare("1.0-RC1", "1.0_RC1"));

    // Dots have different semantics - they're part of version numbering
    assertNotEquals(0, comparator.compare("1.0.alpha", "1.0-alpha"));
  }

  @Test
  public void testNumericVsStringParts() {
    assertTrue(comparator.compare("1.0-alpha", "1.0.1") < 0);
    assertTrue(comparator.compare("1.0-alpha", "1.0.0") < 0);
    assertTrue(comparator.compare("1.0.0-alpha", "1.0.0") < 0);
    assertTrue(comparator.compare("1.0-beta", "1.0.0") < 0);
    assertTrue(comparator.compare("1.0-SNAPSHOT", "1.0.0") < 0);
  }

  @Test
  public void testCaseSensitivity() {
    assertEquals(0, comparator.compare("1.0-SNAPSHOT", "1.0-snapshot"));
    assertEquals(0, comparator.compare("1.0-ALPHA", "1.0-alpha"));
    assertEquals(0, comparator.compare("1.0-BETA", "1.0-beta"));
    assertEquals(0, comparator.compare("1.0-MILESTONE", "1.0-milestone"));
    assertEquals(0, comparator.compare("1.0-RC", "1.0-rc"));
  }

  @Test
  public void testEdgeCases() {
    // Null values
    assertTrue(comparator.compare(null, "1.0") < 0);
    assertTrue(comparator.compare("1.0", null) > 0);
    //noinspection EqualsWithItself
    assertEquals(0, comparator.compare(null, null));

    // Empty strings
    assertTrue(comparator.compare("", "1.0") < 0);
    assertTrue(comparator.compare("1.0", "") > 0);
    //noinspection EqualsWithItself
    assertEquals(0, comparator.compare("", ""));

    // Non-numeric versions
    assertTrue(comparator.compare("a", "b") < 0);
    assertTrue(comparator.compare("a.b", "a.c") < 0);
  }

  @Test
  public void testRealWorldExamples() {
    // Examples from Maven's own POM files
    List<String> versions = Arrays.asList("1.0.0-alpha", "1.0.0-alpha.1", "1.0.0-beta.2",
        "1.0.0-beta.11", "1.0.0-rc.1", "1.0.0-rc.1.1", "1.0.0", "1.0.0-sp", "1.0.0-sp.1", "1.0.1",
        "1.1.0-M1", "1.1.0", "2.0.0", "2.0.1-SNAPSHOT", "2.0.1", "2.1.0-M1");
    assertVersionOrder(versions);
  }

  @Test
  public void testProjectSpecificExamples() {
    // From the project's POM file
    assertTrue(comparator.compare("2.10.0", "2.10.1-SNAPSHOT") < 0);
    assertTrue(comparator.compare("3.2.5", "3.3.1") < 0);
    assertTrue(comparator.compare("1.0.7", "1.8.3") < 0);
  }
}