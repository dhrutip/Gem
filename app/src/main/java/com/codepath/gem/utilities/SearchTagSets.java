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
        defaultTags.add("food");
        defaultTags.add("nature");
        defaultTags.add("attractions");
        defaultTags.add("accessible");
    }

    public static void populateFoodTags() {
        foodTags.add("breakfast");
        foodTags.add("lunch");
        foodTags.add("dinner");
        foodTags.add("meal");
        foodTags.add("eat");
        foodTags.add("dine");
    }

    public static void populateNatureTags() {
        natureTags.add("trees");
        natureTags.add("mountains");
        natureTags.add("beach");
        natureTags.add("animals");
        natureTags.add("coast");
        natureTags.add("lake");
    }

    public static void populateAttractionsTags() {
        attractionsTags.add("tourist");
        attractionsTags.add("popular");
        attractionsTags.add("sightsee");
        attractionsTags.add("excursion");
        attractionsTags.add("destination");
    }

    public static void populateAccessibleTags() {
        accessibleTags.add("access");
        accessibleTags.add("wheelchair");
        accessibleTags.add("handicap");
        accessibleTags.add("disab"); // disable/d, disability
        accessibleTags.add("mobility");
    }
}
