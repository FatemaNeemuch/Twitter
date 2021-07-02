package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
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
    public List<String> imageURLs;
    public long id;
    public int favoriteCount;
    public boolean favorited;
    public int retweetCount;
    public boolean retweeted;

    //empty constructor needed by the Parceler Library
    public Tweet() {}

    //get tweet and user information from jsonObject
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        if(jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        } else {
            tweet.body = jsonObject.getString("text");
        }
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.relativeTime = tweet.getRelativeTimeAgo(tweet.createdAt);
        tweet.id = jsonObject.getLong("id");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.imageURLs = new ArrayList<>();
        if (jsonObject.has("extended_entities")){
            JSONArray mediaArray = jsonObject.getJSONObject("extended_entities").getJSONArray("media");
            for (int i = 0; i < mediaArray.length(); i++){
                tweet.imageURLs.add(mediaArray.getJSONObject(i).getString("media_url_https"));
            }
        }else if (jsonObject.getJSONObject("entities").has("media")){
            JSONArray mediaArray = jsonObject.getJSONObject("entities").getJSONArray("media");
            for (int i = 0; i < mediaArray.length(); i++){
                tweet.imageURLs.add(mediaArray.getJSONObject(i).getString("media_url_https"));
            }
        }
        Log.i(TAG, "Media added" + tweet.imageURLs.toString());
        return tweet;
    }

    //how old the tweet is
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            //time the tweet was posted
            long dateMillis = sf.parse(rawJsonDate).getTime();
            //current time
            long now = System.currentTimeMillis();
            //difference in time between when the tweet was posted and the current time
            long diff = now - dateMillis;
            //tweet posted less than a minute ago
            if (diff < MINUTE_MILLIS){
                return "now";
            //tweet posted between 1 and 2 minutes ago
            }else if (diff < 2*MINUTE_MILLIS){
                return "1m";
            //tweet posted within the hour
            }else if (diff < 50*MINUTE_MILLIS){
                return diff/MINUTE_MILLIS + "m";
            //tweet posted between 60 mins and 90mins
            }else if (diff < 90*MINUTE_MILLIS){
                return "1h";
            //tweet posted within the day
            }else if (diff < 24*HOUR_MILLIS){
                return diff/HOUR_MILLIS + "h";
            //tweet posted between 24 and 48 hours
            }else if (diff < 48*HOUR_MILLIS){
                return "yesterday";
            //tweet posted more than 48 hours ago
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
