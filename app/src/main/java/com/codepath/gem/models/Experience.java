package com.codepath.gem.models;

import android.util.Log;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import org.parceler.Parcel;
import java.util.Date;

@ParseClassName("Experience") // entity on parse dashboard
@Parcel(analyze = Experience.class)
public class Experience extends ParseObject {
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE_ONE = "image1";
    public static final String KEY_IMAGE_TWO = "image2";
    public static final String KEY_HOST = "host";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_LOCATION = "location";

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public Experience() {}

    public String getTitle() {
//        return getString(KEY_TITLE);
        String title = "";
        try {
            title = fetchIfNeeded().getString(KEY_TITLE);

        } catch (ParseException e) {
            Log.v("Experience: title fetching error, ", e.toString());
            e.printStackTrace();
        }
        return title;
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImageOne() {
        return getParseFile(KEY_IMAGE_ONE);
    }

    public void setImageOne(ParseFile parseFile) {
        put(KEY_IMAGE_ONE, parseFile);
    }

    public ParseFile getImageTwo() {
        return getParseFile(KEY_IMAGE_TWO);
    }

    public void setImageTwo(ParseFile parseFile) {
        put(KEY_IMAGE_TWO, parseFile);
    }

    public ParseUser getHost() {
        return getParseUser(KEY_HOST);
    }

    public void setHost(ParseUser user) {
        put(KEY_HOST, user);
    }

    public static String getRelativeTimeAgo(Date date) {
        try {
            long time = date.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("TWEET", "getRelativeTimeAgo failed");
            e.printStackTrace();
        }
        return "";
    }
}
