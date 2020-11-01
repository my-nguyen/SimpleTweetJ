package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    private static final String TAG = "ComposeActivity";
    private static final int MAX_TWEET_LENGTH = 140;

    ActivityComposeBinding binding;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_compose);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);
        binding.tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.compose.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                } else if (text.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ComposeActivity.this, text, Toast.LENGTH_LONG).show();
                    client.publishTweet(text, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "onSuccess to publish tweet");
                            Tweet tweet = new Tweet(json.jsonObject);
                            Log.d(TAG, "Published tweet says: " + tweet);
                            Intent intent = new Intent();
                            intent.putExtra("EXTRA_TWEET_OBJECT", tweet);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onFailure to publish tweet", throwable);
                        }
                    });
                }
            }
        });
    }
}