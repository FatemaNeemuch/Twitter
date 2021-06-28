package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    //instance variable
    public String name;
    public String screenName;
    public String profileImageURL;

    //get user information from jsonObject
    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageURL = jsonObject.getString("profile_image_url_https");
        return user;
    }
}
