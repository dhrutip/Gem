package com.codepath.gem.fragments;

import static org.junit.Assert.*;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Unit test to verify accurate tagging of keywords in Home Fragment
 */
@RunWith(AndroidJUnit4.class)
public class HomeFragmentTagTest {

    private HomeFragment hf;
    public static final String TEST_DESC_NO_KEYWORDS = "this test description has no keywords";
    public static final String TEST_DESC_KEYWORDS = "this test description has the custom keyword butterfly";
    public static final String TEST_DESC_DEFAULT_KEYWORDS = "i like trees";
    public static final String TEST_DESC_DEFAULT_NO_KEYWORDS = "i like french fries";
    public static final String TEST_TAG_CUSTOM = "butterfly";
    public static final String TEST_TAG_DEFAULT = "nature";

    @Before
    public void createHomeFragment() {
        hf = new HomeFragment();
        hf.populateDefaultTags();
        hf.populateNatureTags();
    }

    @Test
    public void testDefaultTrueTag() {
        assertTrue(hf.isTagged(TEST_DESC_DEFAULT_KEYWORDS, TEST_TAG_DEFAULT));
    }

    @Test
    public void testDefaultFalseTag() {
        assertFalse(hf.isTagged(TEST_DESC_DEFAULT_NO_KEYWORDS, TEST_TAG_DEFAULT));
    }

    @Test
    public void testNoKeywordsTag() {
        assertFalse(hf.isTagged(TEST_DESC_NO_KEYWORDS, TEST_TAG_CUSTOM));
    }

    @Test
    public void testKeywordsTag() {
        assertTrue(hf.isTagged(TEST_DESC_KEYWORDS, TEST_TAG_CUSTOM));
    }
}