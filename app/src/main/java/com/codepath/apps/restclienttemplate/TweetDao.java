package com.codepath.apps.restclienttemplate;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
interface TweetDao {
    @Query("SELECT Tweet.id AS tweet_id, Tweet.createdAt AS tweet_createdAt, Tweet.text AS tweet_text, " +
            "Tweet.retweetCount AS tweet_retweetCount, Tweet.favoriteCount AS tweet_favoriteCount, User.* " +
            "FROM Tweet INNER JOIN User ON Tweet.userId = User.id ORDER BY Tweet.createdAt DESC LIMIT 5")
    List<TweetWithUser> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);
}
