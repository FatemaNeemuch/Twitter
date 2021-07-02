package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.graphics.ImageDecoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    //class constants
    public static final String TAG = "TweetsAdapter";

    //Pass in the context and list of tweets
    Context context;
    List<Tweet> tweets;
    TwitterClient client;

    public TweetsAdapter(Context context, List<Tweet> tweets, TwitterClient client){
        this.context = context;
        this.tweets = tweets;
        this.client = client;
    }

    //For each row, inflate the layout
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    //Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        //get the data at the position
        Tweet tweet = tweets.get(position);
        //Bind the tweet with view holder
        holder.bind(tweet);
    }

    //get number of tweets
    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    //Define a view holder
    public class ViewHolder extends RecyclerView.ViewHolder{

        //instance variable
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvRelativeTime;
        TextView tvName;
        ImageView ivMediaImage;
        ImageView ivLike;
        ImageView ivRetweet;
        TextView tvLikeCount;
        TextView tvRetweetCount;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            //reference to views
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            tvName = itemView.findViewById(R.id.tvName);
            ivMediaImage = itemView.findViewById(R.id.ivMediaImage);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivRetweet = itemView.findViewById(R.id.ivRetweet);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvRetweetCount = itemView.findViewById(R.id.tvRetweetCount);
        }

        public void bind(Tweet tweet) {
            //bind views to the data they are supposed to show
            tvScreenName.setText("@" + tweet.user.screenName);
            tvBody.setText(tweet.body);
            tvRelativeTime.setText(tweet.relativeTime);
            tvName.setText(tweet.user.name);
            tvLikeCount.setText(tweet.favoriteCount + "");
            tvRetweetCount.setText(tweet.retweetCount + "");
            //embedded media
            if (!tweet.imageURLs.isEmpty()){
                //if media exists, show the first image in the array
                ivMediaImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(tweet.imageURLs.get(0))
                        .fitCenter()
                        .transform(new RoundedCornersTransformation(30,10))
                        .into(ivMediaImage);
            }else{
                //if media does exist, don't show anything
                ivMediaImage.setVisibility(View.GONE);
            }
            //make the like button
            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //switch the like status on click
                    tweet.favorited = !tweet.favorited;
                    //if liked
                    if (tweet.favorited){
                        //tell client that a tweet was liked
                        client.favoriteCreate(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                //update likes count
                                tweet.favoriteCount++;
                                tvLikeCount.setText(tweet.favoriteCount + "");
                                //update image
                                Glide.with(context).load(R.drawable.ic_vector_heart).into(ivLike);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "OnFailureFC" + response, throwable);
                            }
                        });
                    }else{
                        //if unliked
                        //tell client tweet was unliked
                        client.favoriteDestroy(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                //update like count
                                tweet.favoriteCount--;
                                tvLikeCount.setText(tweet.favoriteCount + "");
                                //update image
                                Glide.with(context).load(R.drawable.ic_vector_heart_stroke).into(ivLike);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "OnFailureFD" + response, throwable);
                            }
                        });
                    }
                }
            });
            //make retweet button
            ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //switch the retweet status on click
                    tweet.retweeted = !tweet.retweeted;
                    //if retweeted
                    if (tweet.retweeted){
                        //tell client tweet was retweeted
                        client.retweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                //update retweet count
                                tweet.retweetCount++;
                                tvRetweetCount.setText(tweet.retweetCount + "");
                                //update image
                                Glide.with(context).load(R.drawable.ic_vector_retweet).into(ivRetweet);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "OnFailureRT" + response, throwable);
                            }
                        });
                    }else{
                        //if unretweeted
                        //tell client tweet was unretweeted
                        client.unRetweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                //update retweet count
                                tweet.retweetCount--;
                                tvRetweetCount.setText(tweet.retweetCount + "");
                                //update image
                                Glide.with(context).load(R.drawable.ic_vector_retweet_stroke).into(ivRetweet);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "OnFailureURT" + response, throwable);
                            }
                        });
                    }
                }
            });
            //like image
            if (tweet.favorited){
                Glide.with(context).load(R.drawable.ic_vector_heart).into(ivLike);
            }else{
                Glide.with(context).load(R.drawable.ic_vector_heart_stroke).into(ivLike);
            }
            //retweet image
            if (tweet.retweeted){
                Glide.with(context).load(R.drawable.ic_vector_retweet).into(ivRetweet);
            }else{
                Glide.with(context).load(R.drawable.ic_vector_retweet_stroke).into(ivRetweet);
            }
            //profile picture
            Glide.with(context)
                    .load(tweet.user.profileImageURL)
                    .fitCenter()
                    .transform(new RoundedCornersTransformation(30,10))
                    .into(ivProfileImage);
        }
    }
}
