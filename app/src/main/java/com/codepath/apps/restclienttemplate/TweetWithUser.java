package com.codepath.apps.restclienttemplate;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

class TweetWithUser {
    // Embedded notation flattens the properties of the User object into the object, preserving encapsulation
    @Embedded
    User user;
    @Embedded(prefix = "tweet_")
    Tweet tweet;

    public static List<Tweet> toTweets(List<TweetWithUser> items) {
        List<Tweet> tweets = new ArrayList<>();
        for (TweetWithUser item : items) {
            Tweet tweet = new Tweet(item);
            tweets.add(tweet);
        }
        return tweets;
    }
}
