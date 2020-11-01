package com.codepath.apps.restclienttemplate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.codepath.apps.restclienttemplate.Constants.EXTRA_TWEET_OBJECT;
import static com.codepath.apps.restclienttemplate.Constants.TWITTER_DATE_FORMAT;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_detail);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Tweet tweet = (Tweet)getIntent().getSerializableExtra(EXTRA_TWEET_OBJECT);
        Glide.with(this).load(tweet.user.profileImageUrl).into(binding.profileImage);
        binding.name.setText(tweet.user.name);
        binding.screenName.setText(tweet.user.screenName);
        try {
            SimpleDateFormat format = new SimpleDateFormat(TWITTER_DATE_FORMAT);
            Date date = format.parse(tweet.createdAt);
            SimpleDateFormat fmt = new SimpleDateFormat("hh:mm a dd/MM/yy");
            String time = fmt.format(date);
            binding.time.setText(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        binding.text.setText(tweet.text);
        binding.retweets.setText(tweet.retweetCount + "");
        binding.likes.setText(tweet.favoriteCount + "");
    }
}