package com.codepath.apps.restclienttemplate;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(foreignKeys = @ForeignKey(entity=User.class, parentColumns="id", childColumns="userId"))
public class Tweet implements Serializable {
    @ColumnInfo
    @PrimaryKey
    public long id;
    @ColumnInfo
    public String createdAt;
    @ColumnInfo
    public String text;
    @ColumnInfo
    public int retweetCount;
    @ColumnInfo
    public int favoriteCount;
    @Ignore
    public User user;
    @ColumnInfo
    public long userId;
    // public Entities entities;
    // public ExtendedEntities extendedEntities;

    /*Tweet() {
        int tmp = 0;
    }*/

    Tweet(JSONObject jsonObject) {
        try {
            id = jsonObject.getLong("id");
            createdAt = jsonObject.getString("created_at");
            text = jsonObject.getString("text");
            retweetCount = jsonObject.getInt("retweet_count");
            favoriteCount = jsonObject.getInt("favorite_count");
            user = new User(jsonObject.getJSONObject("user"));
            userId = user.id;
            // entities = new Entities(jsonObject.getJSONObject("entities"));
            // extendedEntities = new ExtendedEntities(jsonObject.getJSONObject("extended_entities"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Tweet(TweetWithUser item) {
        id = item.tweet.id;
        createdAt = item.tweet.createdAt;
        text = item.tweet.text;
        retweetCount = item.tweet.retweetCount;
        favoriteCount = item.tweet.favoriteCount;
        user = item.user;
    }

    static List<Tweet> fromJSONArray(JSONArray jsonArray) {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Tweet tweet = new Tweet(jsonArray.getJSONObject(i));
                tweets.add(tweet);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tweets;
    }
}

class Entities implements Serializable {
    public List<Medium> media;

    Entities(JSONObject jsonObject) {
        if (jsonObject.has("media")) {
            media = new ArrayList<>();
            JSONArray jsonArray = null;
            try {
                jsonArray = jsonObject.getJSONArray("media");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Medium medium = new Medium(jsonArray.getJSONObject(i));
                    media.add(medium);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

class ExtendedEntities implements Serializable {
    public List<Medium> media;

    ExtendedEntities(JSONObject jsonObject) {
        if (jsonObject.has("media")) {
            media = new ArrayList<>();
            JSONArray jsonArray = null;
            try {
                jsonArray = jsonObject.getJSONArray("media");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Medium medium = new Medium(jsonArray.getJSONObject(i));
                    media.add(medium);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

class Medium implements Serializable {
    private static final String TAG = "Medium";

    public String mediaUrl;
    public String type;
    public VideoInfo videoInfo;

    Medium(JSONObject jsonObject) {
        try {
            mediaUrl = jsonObject.getString("media_url");
            type = jsonObject.getString("type");
            if (jsonObject.has("video_info")) {
                videoInfo = new VideoInfo(jsonObject.getJSONObject("video_info"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

class VideoInfo implements Serializable {
    List<Variant> variants;

    VideoInfo(JSONObject jsonObject) {
        variants = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("variants");
            for (int i = 0; i < jsonArray.length(); i++) {
                Variant variant = new Variant(jsonArray.getJSONObject(i));
                variants.add(variant);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

class Variant implements Serializable {
    public String url;

    Variant(JSONObject jsonObject) {
        try {
            url = jsonObject.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

@Entity
class User implements Serializable {
    @ColumnInfo
    @PrimaryKey
    public long id;
    @ColumnInfo
    public String name;
    @ColumnInfo
    public String screenName;
    @ColumnInfo
    public String profileImageUrl;

    User() {}

    User(JSONObject jsonObject) {
        try {
            id = jsonObject.getLong("id");
            name = jsonObject.getString("name");
            screenName = jsonObject.getString("screen_name");
            profileImageUrl = jsonObject.getString("profile_image_url_https");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<User> fromTweets(List<Tweet> tweets) {
        List<User> users = new ArrayList<>();
        for (Tweet tweet : tweets) {
            users.add(tweet.user);
        }
        return users;
    }
}
