package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    //class constants
    public static final int MAX_TWEET_LENGTH = 140;
    public static final String TAG = "ComposeActivity";

    //instance variables
    EditText etCompose;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_simple.xml -> ActivitySimpleBinding
        ActivityComposeBinding binding = ActivityComposeBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        //reference for views
        etCompose = binding.etCompose;

        // send network request
        client = TwitterApp.getRestClient(this);

        //go back to timeline if cancel compose tweet
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //set click listener on compose button
        binding.btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                //make sure tweet to post isn't empty
                if (tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Your tweet cannot be empty", Toast.LENGTH_SHORT).show();
//                    Snackbar snack = Snackbar.make(etCompose, "Your tweet cannot be empty", Snackbar.LENGTH_LONG);
//                    snack.setAction("Undo", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            etCompose.setText("");
//                        }
//                    });
//                    View snackView = snack.getView();
//                    RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)view.getLayoutParams();
//                    //figure out how to make it show at the top
//                    //params.
//                    view.setLayoutParams(params);
//                    snack.show();
                    return;
                }

                //make sure tweet to post isn't too long
                if (tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Your tweet is too long", Toast.LENGTH_SHORT).show();
//                    Snackbar snack = Snackbar.make(etCompose, "Your tweet is too long", Snackbar.LENGTH_LONG);
//                    snack.setAction("Undo", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            etCompose.setText("");
//                        }
//                    });
//                    View snackView = snack.getView();
//                    RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)view.getLayoutParams();
//                    //figure out how to make it show at the top
//                    //params.
//                    view.setLayoutParams(params);
//                    snack.show();
                    return;
                }
                //Make an API call to Twitter to publish the tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        JSONObject jsonObject = json.jsonObject;
                        try {
                            //get tweet object
                            Tweet tweet = Tweet.fromJSON(jsonObject);
                            Log.i(TAG, "published tweet says: " + tweet.body);
                            //create an intent to go back to TimelineActivity
                            Intent intent = new Intent();
                            //Pass tweet back as a result
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            //set result code and bundle data for response
                            setResult(RESULT_OK, intent);
                            //closes the activity, pass data to parent
                            finish();
                        } catch (JSONException e) {
                            Log.e(TAG, "exception", e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });
            }
        });
    }
}