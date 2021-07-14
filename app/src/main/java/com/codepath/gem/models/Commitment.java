package com.codepath.gem.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Commitment") // entity on parse dashboard
public class Commitment extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_EXPERIENCE = "experience";
    public static final String KEY_CREATED_AT = "createdAt";

    public Commitment() {}

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public ParseObject getExperience() {
        return getParseObject(KEY_EXPERIENCE);
    }
}
