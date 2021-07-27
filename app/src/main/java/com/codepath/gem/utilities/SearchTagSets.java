package com.codepath.gem.utilities;

import java.util.HashSet;
import java.util.Set;

public class SearchTagSets {

    public final static Set<String> defaultTags = new HashSet<>();
    public final static Set<String> foodTags = new HashSet<>();
    public final static Set<String> natureTags = new HashSet<>();
    public final static Set<String> attractionsTags = new HashSet<>();
    public final static Set<String> accessibleTags = new HashSet<>();

    public static void populateDefaultTags() {
        if (defaultTags.size() == 0) {
            defaultTags.add("food");
            defaultTags.add("nature");
            defaultTags.add("attractions");
            defaultTags.add("accessible");
        }
    }

    public static void populateFoodTags() {
        if (foodTags.size() == 0) {
            foodTags.add("breakfast");
            foodTags.add("lunch");
            foodTags.add("dinner");
            foodTags.add("brunch");
            foodTags.add("meal");
            foodTags.add("eat");
            foodTags.add("dine");
        }
    }

    public static void populateNatureTags() {
        if (natureTags.size() == 0) {
            natureTags.add("trees");
            natureTags.add("mountain");
            natureTags.add("beach");
            natureTags.add("animal");
            natureTags.add("coast");
            natureTags.add("lake");
        }
    }

    public static void populateAttractionsTags() {
        if (attractionsTags.size() == 0) {
            attractionsTags.add("tourist");
            attractionsTags.add("popular");
            attractionsTags.add("sightsee");
            attractionsTags.add("excursion");
            attractionsTags.add("destination");
        }
    }

    public static void populateAccessibleTags() {
        if (accessibleTags.size() == 0) {
            accessibleTags.add("access");
            accessibleTags.add("wheelchair");
            accessibleTags.add("handicap");
            accessibleTags.add("disab"); // disable/d, disability
            accessibleTags.add("mobility");
        }
    }
}
