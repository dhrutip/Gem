package com.codepath.gem.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Conversation") // entity on parse dashboard
public class Conversation extends ParseObject {
    public static final String KEY_USER_SENDER = "userSender";
    public static final String KEY_USER_RECEIVER = "userReceiver";

    public ParseUser getUserSender() {
        return getParseUser(KEY_USER_SENDER);
    }

    public void setUserSender(ParseUser userSender) {
        put(KEY_USER_SENDER, userSender);
    }

    public ParseUser getUserReceiver() {
        return getParseUser(KEY_USER_RECEIVER);
    }

    public void setUserReceiver(ParseUser userReceiver) {
        put(KEY_USER_RECEIVER, userReceiver);
    }
}
