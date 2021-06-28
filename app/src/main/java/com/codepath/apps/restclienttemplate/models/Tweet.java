package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Tweet {

    //class constants
    public static final String TAG = "Tweet";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    //instance variables
    public String body;
    public String createdAt;
    public User user;
    public String relativeTime;

    //get tweet and user information from jsonObject
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.relativeTime = tweet.getRelativeTimeAgo(tweet.createdAt);
        return tweet;
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();
            long diff = now - dateMillis;
            if (diff < MINUTE_MILLIS){
                return "just now";
            }else if (diff < 2*MINUTE_MILLIS){
                return "a minute ago";
            }else if (diff < 50*MINUTE_MILLIS){
                return diff/MINUTE_MILLIS + "m";
            }else if (diff < 90*MINUTE_MILLIS){
                return "an hour ago";
            }else if (diff < 24*HOUR_MILLIS){
                return diff/HOUR_MILLIS + "h";
            }else if (diff < 48*HOUR_MILLIS){
                return "yesterday";
            }else{
                return diff/DAY_MILLIS + "d";
            }
        } catch (ParseException e) {
            Log.e(TAG, "relative date error", e);
        }

        return "";
    }

    //create a list with all the tweets and their information
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJSON(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
