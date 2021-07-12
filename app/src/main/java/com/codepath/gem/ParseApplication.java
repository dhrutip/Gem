package com.codepath.gem;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("Qt5xfXdvveLqh7c97NYTqGCB5rZd6nCa573xqcHt")
                .clientKey("8S3cUrHvACeCPxKg4KmlEpZhO5RRoaFJu9A7Plqd")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
