package com.codepath.gem.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message") // entity on parse dashboard
public class Message extends ParseObject {
    public static final String KEY_MESSAGE_SENDER = "sender";
    public static final String KEY_CONVERSATION = "conversation";
    public static final String KEY_BODY = "body";

    public ParseUser getSender() {
        return getParseUser(KEY_MESSAGE_SENDER);
    }

    public void setSender(ParseUser sender) {
        put(KEY_MESSAGE_SENDER, sender);
    }

    public String getBody() {
        return getString(KEY_BODY);
    }

    public void setBody(String body) {
        put(KEY_BODY, body);
    }

    public Conversation getConversation() {
        return (Conversation) getParseObject(KEY_CONVERSATION);
    }

    public void setConversation(Conversation conversation) {
        put(KEY_CONVERSATION, conversation);
    }
}
