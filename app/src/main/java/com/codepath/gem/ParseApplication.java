package com.codepath.gem;

import android.app.Application;

import com.codepath.gem.models.Commitment;
import com.codepath.gem.models.Conversation;
import com.codepath.gem.models.Experience;
import com.codepath.gem.models.Message;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Experience.class);
        ParseObject.registerSubclass(Commitment.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Conversation.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("Qt5xfXdvveLqh7c97NYTqGCB5rZd6nCa573xqcHt")
                .clientKey("8S3cUrHvACeCPxKg4KmlEpZhO5RRoaFJu9A7Plqd")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
