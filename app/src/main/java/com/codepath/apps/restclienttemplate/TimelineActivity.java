package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "TimelineActivity";
    public static final int REQUEST_CODE_COMPOSE = 1967;

    TwitterClient client;
    TweetDao tweetDao;
    ActivityTimelineBinding binding;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_timeline);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);
        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();

        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        binding.tweets.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.tweets.setLayoutManager(layoutManager);

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "refreshing data");
                populateHomeTimeline();
            }
        });
        binding.swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "onLoadMore " + page);
                loadMoreData();
            }
        };
        binding.tweets.addOnScrollListener(scrollListener);

        // query local database in the background
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Showing data from database");
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();
                List<Tweet> tweetsFromDB = TweetWithUser.toTweets(tweetWithUsers);
                adapter.clear();
                adapter.addAll(tweetsFromDB);
            }
        });

        populateHomeTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivityForResult(intent, REQUEST_CODE_COMPOSE);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_COMPOSE && resultCode == RESULT_OK) {
            // extract the tweet
            Tweet tweet = (Tweet)data.getSerializableExtra("EXTRA_TWEET_OBJECT");
            // insert the tweet to the top of the recyclerview
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            binding.tweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadMoreData() {
        // 1. Send an API request to retrieve appropriate paginated data
        Tweet lastTweet = tweets.get(tweets.size() - 1);
        long maxId = lastTweet.id;
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "loadMoreData, onSuccess: ");
                // 2. Deserialize and construct new model objects from the API response
                List<Tweet> items = Tweet.fromJSONArray(json.jsonArray);
                // 3. Append the new data objects to the existing set of items inside the array of items
                // 4. Notify the adapter of the new items made with `notifyItemRangeInserted()`
                adapter.addAll(items);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "loadMoreData, onFailure: ", throwable);
            }
        }, maxId);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Log.d(TAG, "onSuccess: " + json.toString());
                final List<Tweet> tweetsFromNetwork = Tweet.fromJSONArray(json.jsonArray);
                adapter.clear();
                adapter.addAll(tweetsFromNetwork);
                binding.swipeRefresh.setRefreshing(false);

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Saving data into database");
                        List<User> users = User.fromTweets(tweetsFromNetwork);
                        tweetDao.insertModel(users.toArray(new User[0]));
                        tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure: "+response, throwable);
            }
        });
    }
}