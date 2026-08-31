package de.quadflal.sdfccbackend.adapter.out.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses OpenAPI-style sort criteria ("property,asc" or "property,desc").
 * Spring's default binding of a single {@code sort=property,direction} query parameter to
 * {@code List<String>} splits the value on commas, so both a single combined token
 * ("property,direction") and two consecutive flat tokens ("property", "direction") are supported.
 */
public final class SortCriteriaParser {

    public record SortCriterion(String property, boolean descending) {}

    private SortCriteriaParser() {
    }

    public static List<SortCriterion> parse(List<String> rawSort) {
        List<SortCriterion> result = new ArrayList<>();
        if (rawSort == null || rawSort.isEmpty()) {
            return result;
        }

        int i = 0;
        while (i < rawSort.size()) {
            String token = rawSort.get(i);
            if (token.contains(",")) {
                String[] parts = token.split(",", 2);
                result.add(new SortCriterion(parts[0], isDescending(parts[1])));
                i++;
            } else if (i + 1 < rawSort.size() && isDirection(rawSort.get(i + 1))) {
                result.add(new SortCriterion(token, isDescending(rawSort.get(i + 1))));
                i += 2;
            } else {
                result.add(new SortCriterion(token, false));
                i++;
            }
        }
        return result;
    }

    private static boolean isDirection(String value) {
        return "asc".equalsIgnoreCase(value) || "desc".equalsIgnoreCase(value);
    }

    private static boolean isDescending(String value) {
        return "desc".equalsIgnoreCase(value);
    }
}
