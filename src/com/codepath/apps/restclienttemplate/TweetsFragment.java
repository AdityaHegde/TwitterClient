package com.codepath.apps.restclienttemplate;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.activeandroid.util.Log;
import com.codepath.apps.restclienttemplate.models.Tweet;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TweetsFragment extends Fragment implements OnScrollListener, TweetsLoader.OnTweetsLoadListener {
	private static final int VISIBLE_THRESHOLD = 20;
	
	private ArrayList<Tweet> tweets;
	private PullToRefreshListView lvTweets;
	private TweetAdapter tweetAdapter;
	private boolean loading = false;
	
	private TweetsLoader loader;
	
	public TweetsFragment() {
		super();
		this.loader = new TweetsLoader();
	}
	
	public TweetsFragment(TweetsLoader loader) {
		super();
		tweets = new ArrayList<Tweet>();
		this.loader = loader;
		loader.setListener(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.d("Debug", "Creating view");
		View view = inflater.inflate(R.layout.tweets, container, false);
		lvTweets = (PullToRefreshListView) view.findViewById(R.id.lvTweets);
		tweetAdapter = new TweetAdapter(getActivity(), tweets);
		lvTweets.setAdapter(tweetAdapter);
		lvTweets.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				loading = true;
				loader.refreshTweets();
			}
		});
		lvTweets.setOnScrollListener(this);
		return view;
	}

	@Override
	public void onTweetsLoadSuccess() {
		loading = false;
		tweetAdapter.notifyDataSetChanged();
		lvTweets.onRefreshComplete();
	}

	@Override
	public void onTweetsLoadFailure() {
		loading = false;
		lvTweets.onRefreshComplete();
	}

	@Override
	public ArrayList<Tweet> getTweetsArray() {
		return tweets;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(!loading && totalItemCount - firstVisibleItem < VISIBLE_THRESHOLD) {
			loading = true;
			loader.loadMoreTweets();
		}
	}
	
	public void addTweet(Tweet tweet) {
		tweets.add(0, tweet);
		loader.updateTweetIds();
		tweetAdapter.notifyDataSetChanged();
	}
}
