package com.okworx.ilcd.validation.profile.update;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenVersionComparator implements Comparator<String> {

    private static final List<String> QUALIFIER_ORDER = Arrays.asList(
        "alpha", "beta", "milestone", "rc", "snapshot", "", "sp"
    );

    private static final Map<String, String> QUALIFIER_ALIASES = new HashMap<>();
    static {
        QUALIFIER_ALIASES.put("a", "alpha");
        QUALIFIER_ALIASES.put("b", "beta");
        QUALIFIER_ALIASES.put("m", "milestone");
        QUALIFIER_ALIASES.put("cr", "rc");
        QUALIFIER_ALIASES.put("dev", "snapshot");
        QUALIFIER_ALIASES.put("final", "");
        QUALIFIER_ALIASES.put("ga", "");
        QUALIFIER_ALIASES.put("release", "");
    }

    private static final Pattern QUALIFIER_PATTERN = Pattern.compile("^([a-zA-Z]*)(\\d*)$");

    @Override
    public int compare(String version1, String version2) {
        // Handle null values
        if (version1 == null && version2 == null) return 0;
        if (version1 == null) return -1;
        if (version2 == null) return 1;

        // Handle empty strings
        if (version1.isEmpty() && version2.isEmpty()) return 0;
        if (version1.isEmpty()) return -1;
        if (version2.isEmpty()) return 1;

        // Convert to lowercase for case-insensitive comparison
        version1 = version1.toLowerCase();
        version2 = version2.toLowerCase();

        // Split into numeric part and qualifier
        String[] v1Parts = version1.split("[\\-_]", 2);
        String[] v2Parts = version2.split("[\\-_]", 2);

        // Compare numeric parts first
        int numericCompare = compareNumericParts(v1Parts[0], v2Parts[0]);
        if (numericCompare != 0) {
            return numericCompare;
        }

        // Extract qualifiers (or use empty string if none)
        String qualifier1 = v1Parts.length > 1 ? v1Parts[1] : "";
        String qualifier2 = v2Parts.length > 1 ? v2Parts[1] : "";

        return compareQualifiers(qualifier1, qualifier2);
    }

    private int compareNumericParts(String v1, String v2) {
        String[] v1Parts = v1.split("\\.");
        String[] v2Parts = v2.split("\\.");

        int maxLength = Math.max(v1Parts.length, v2Parts.length);

        for (int i = 0; i < maxLength; i++) {
            String s1 = i < v1Parts.length ? v1Parts[i] : "0";
            String s2 = i < v2Parts.length ? v2Parts[i] : "0";

            // Check if both parts are numeric
            boolean isNumeric1 = isNumeric(s1);
            boolean isNumeric2 = isNumeric(s2);

            if (isNumeric1 && isNumeric2) {
                // Compare as integers
                int n1 = Integer.parseInt(s1);
                int n2 = Integer.parseInt(s2);
                if (n1 != n2) {
                    return Integer.compare(n1, n2);
                }
            } else if (isNumeric1) {
                return 1;
            } else if (isNumeric2) {
                return -1;
            } else {
                // Both are non-numeric, compare lexicographically
                int strCompare = s1.compareTo(s2);
                if (strCompare != 0) {
                    return strCompare;
                }
            }
        }

        return 0;
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    private int compareQualifiers(String q1, String q2) {
        // If both qualifiers are empty, they're equal
        if (q1.isEmpty() && q2.isEmpty()) return 0;

        // Split by dots to handle qualifiers with multiple parts
        String[] q1Parts = q1.split("\\.");
        String[] q2Parts = q2.split("\\.");

        // Compare the first part (the actual qualifier)
        int baseQualifierCompare = compareQualifierBase(q1Parts[0], q2Parts[0]);
        if (baseQualifierCompare != 0) {
            return baseQualifierCompare;
        }

        // If base qualifiers are the same, compare the numeric parts
        int minLength = Math.min(q1Parts.length, q2Parts.length);

        for (int i = 1; i < minLength; i++) {
            int compareResult;
            if (isNumeric(q1Parts[i]) && isNumeric(q2Parts[i])) {
                compareResult = Integer.compare(Integer.parseInt(q1Parts[i]), Integer.parseInt(q2Parts[i]));
            } else {
                compareResult = q1Parts[i].compareTo(q2Parts[i]);
            }

            if (compareResult != 0) {
                return compareResult;
            }
        }

        // If all common parts are equal, shorter is less than longer
        return Integer.compare(q1Parts.length, q2Parts.length);
    }

    private int compareQualifierBase(String part1, String part2) {
        // Extract qualifier and numeric suffix using regex
        Matcher m1 = QUALIFIER_PATTERN.matcher(part1);
        Matcher m2 = QUALIFIER_PATTERN.matcher(part2);

        String qualifier1 = part1;
        String qualifier2 = part2;
        int numericSuffix1 = 0;
        int numericSuffix2 = 0;

        if (m1.matches()) {
            qualifier1 = m1.group(1).toLowerCase();
            if (!m1.group(2).isEmpty()) {
                numericSuffix1 = Integer.parseInt(m1.group(2));
            }
        }

        if (m2.matches()) {
            qualifier2 = m2.group(1).toLowerCase();
            if (!m2.group(2).isEmpty()) {
                numericSuffix2 = Integer.parseInt(m2.group(2));
            }
        }

        // Normalize qualifiers using aliases
        qualifier1 = normalizeQualifier(qualifier1);
        qualifier2 = normalizeQualifier(qualifier2);

        // Compare qualifiers based on their order
        int idx1 = QUALIFIER_ORDER.indexOf(qualifier1);
        int idx2 = QUALIFIER_ORDER.indexOf(qualifier2);

        if (idx1 >= 0 && idx2 >= 0) {
            // Both are known qualifiers
            if (idx1 != idx2) {
                return Integer.compare(idx1, idx2);
            }
        } else if (idx1 >= 0) {
            return -1;
        } else if (idx2 >= 0) {
            return 1;
        } else {
            // Neither is known, compare lexicographically
            int stringCompare = qualifier1.compareTo(qualifier2);
            if (stringCompare != 0) {
                return stringCompare;
            }
        }

        // If qualifiers are the same, compare numeric suffixes
        return Integer.compare(numericSuffix1, numericSuffix2);
    }

    private String normalizeQualifier(String qualifier) {
        return QUALIFIER_ALIASES.getOrDefault(qualifier, qualifier);
    }
}