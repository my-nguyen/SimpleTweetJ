package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.codepath.apps.restclienttemplate.Constants.EXTRA_TWEET_OBJECT;
import static com.codepath.apps.restclienttemplate.Constants.TWITTER_DATE_FORMAT;

class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    private static final String TAG = "TweetsAdapter";

    Context context;
    List<Tweet> tweets;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemTweetBinding binding = ItemTweetBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    void addAll(List<Tweet> items) {
        tweets.addAll(items);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemTweetBinding binding;

        ViewHolder(ItemTweetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Tweet tweet = tweets.get(position);
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra(EXTRA_TWEET_OBJECT, tweet);
            context.startActivity(intent);
        }

        public void bind(Tweet tweet) {
            Glide.with(context).load(tweet.user.profileImageUrl).into(binding.profileImage);
            binding.name.setText(tweet.user.name);
            binding.screenName.setText(tweet.user.screenName);
            String timeAgo = getRelativeTimeAgo2(tweet.createdAt);
            binding.timeAgo.setText(timeAgo);
            binding.text.setText(tweet.text);
            // binding.text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        private String getRelativeTimeAgo1(String rawJsonDate) {
            SimpleDateFormat format = new SimpleDateFormat(TWITTER_DATE_FORMAT, Locale.ENGLISH);
            format.setLenient(true);

            String relativeDate = "";
            try {
                long dateMillis = format.parse(rawJsonDate).getTime();
                relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return relativeDate;
        }

        private static final long SECOND_MILLIS = 1000;
        private static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
        private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
        private static final long DAY_MILLIS = 24 * HOUR_MILLIS;

        private String getRelativeTimeAgo2(String rawJsonDate) {
            SimpleDateFormat sf = new SimpleDateFormat(TWITTER_DATE_FORMAT, Locale.ENGLISH);
            sf.setLenient(true);

            try {
                long time = sf.parse(rawJsonDate).getTime();
                long now = System.currentTimeMillis();

                final long diff = now - time;
                if (diff < MINUTE_MILLIS) {
                    return diff/SECOND_MILLIS + "s";
                } else if (diff < HOUR_MILLIS) {
                    return diff/MINUTE_MILLIS + "m";
                } else if (diff < DAY_MILLIS) {
                    return diff/HOUR_MILLIS + "h";
                } else {
                    return diff/DAY_MILLIS + "d";
                }
            } catch (ParseException e) {
                Log.i(TAG, "getRelativeTimeAgo failed");
                e.printStackTrace();
            }

            return "";
        }
    }
}
