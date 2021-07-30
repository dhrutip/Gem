package com.codepath.gem.utilities

import java.util.*

class KotlinSearchTagSets {

    companion object {
        @JvmField val defaultTags: MutableSet<String> = HashSet()
        @JvmField val foodTags: MutableSet<String> = HashSet()
        @JvmField val natureTags: MutableSet<String> = HashSet()
        @JvmField val attractionsTags: MutableSet<String> = HashSet()
        @JvmField val accessibleTags: MutableSet<String> = HashSet()

        @JvmStatic
        fun populateDefaultTags() {
            if (defaultTags.size == 0) {
                defaultTags.add("food")
                defaultTags.add("nature")
                defaultTags.add("attractions")
                defaultTags.add("accessible")
            }
        }

        @JvmStatic
        fun populateFoodTags() {
            if (foodTags.size == 0) {
                foodTags.add("breakfast")
                foodTags.add("lunch")
                foodTags.add("dinner")
                foodTags.add("brunch")
                foodTags.add("meal")
                foodTags.add("eat")
                foodTags.add("dine")
            }
        }

        @JvmStatic
        fun populateNatureTags() {
            if (natureTags.size == 0) {
                natureTags.add("trees")
                natureTags.add("mountain")
                natureTags.add("beach")
                natureTags.add("animal")
                natureTags.add("coast")
                natureTags.add("lake")
            }
        }

        @JvmStatic
        fun populateAttractionsTags() {
            if (attractionsTags.size == 0) {
                attractionsTags.add("tourist")
                attractionsTags.add("popular")
                attractionsTags.add("sightsee")
                attractionsTags.add("excursion")
                attractionsTags.add("destination")
            }
        }

        @JvmStatic
        fun populateAccessibleTags() {
            if (accessibleTags.size == 0) {
                accessibleTags.add("access")
                accessibleTags.add("wheelchair")
                accessibleTags.add("handicap")
                accessibleTags.add("disab") // disable/d, disability
                accessibleTags.add("mobility")
            }
        }
    }
}