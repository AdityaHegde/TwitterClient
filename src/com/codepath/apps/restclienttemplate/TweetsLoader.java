package com.codepath.apps.restclienttemplate;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class TweetsLoader {
	private ArrayList<Tweet> tweets;

	private long max_tweet_id = -1;
	private long since_tweet_id = -1;
	
	private long lastLoadAll = 0;
	private boolean lastLoadAllGotResult = false;
	private long lastLoadMore = 0;
	private boolean lastLoadMoreGotResult = false;
	private long lastRefresh = 0;
	private boolean lastRefreshGotResult = false;
	
	private static long LOAD_ALL_TIMEWINDOW = 5000;
	private static long LOAD_MORE_TIMEWINDOW = 5000;
	private static long REFRESH_MORE_TIMEWINDOW = 5000;
	
	private OnTweetsLoadListener listener;
	
	public String api = "statuses/home_timeline.json";
	public RequestParams params = new RequestParams();
	
	public TweetsLoader() {
	}
	
	public TweetsLoader(String api) {
		this.api = api;
	}
	
	public void setListener(OnTweetsLoadListener listener) {
		this.listener = listener;
		this.tweets = listener.getTweetsArray();
	}
	
	public interface OnTweetsLoadListener {
		public void onTweetsLoadSuccess();
		public void onTweetsLoadFailure();
		public ArrayList<Tweet> getTweetsArray();
	}
	
	void loadTweets(final long max_id, final long since_id) {
		RestClient client = RestClientApp.getRestClient();
		Log.d("Debug", "Call made");
		client.getTimeline(max_id, since_id, this, new JsonHttpResponseHandler() {
			public void onSuccess(JSONArray jsonArray) {
				ArrayList<Tweet> retTweets = Tweet.fromJson(jsonArray);
				boolean haveResults = retTweets.size() > 0;
				long curTime = new Date().getTime();
				Log.d("Debug", "Call returned with " + retTweets.size() + " tweets");
				if(since_id == -1) {
					if(max_id == -1) {
						tweets.clear();
						lastLoadAll = curTime;
						lastLoadAllGotResult = haveResults;
					}
					else {
						lastLoadMore = curTime;
						lastLoadMoreGotResult = haveResults;
					}
					tweets.addAll(retTweets);
				}
				else {
					tweets.addAll(0, retTweets);
					lastRefresh = curTime;
					lastRefreshGotResult = haveResults;
				}
				updateTweetIds();
				listener.onTweetsLoadSuccess();
			}
			
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				Log.d("Debug", arg1);
				listener.onTweetsLoadFailure();
				super.onFailure(arg0, arg1);
			}
			
			@Override
			public void onFailure(Throwable arg0, JSONArray arg1) {
				Log.d("Debug", arg1.toString());
				listener.onTweetsLoadFailure();
				super.onFailure(arg0, arg1);
			}
			
			@Override
			public void onFailure(Throwable arg0, JSONObject arg1) {
				Log.d("Debug", arg1.toString());
				listener.onTweetsLoadFailure();
				super.onFailure(arg0, arg1);
			}
		});
	}
	
	private boolean canCall(boolean lastGotResult, long lastCall, long callWindow) {
		long curTime = new Date().getTime();
		return lastGotResult || curTime - lastCall >= callWindow; 
	}
	
	public void loadAllTweets() {
		if(canCall(lastLoadAllGotResult, lastLoadAll, LOAD_ALL_TIMEWINDOW)) {
			loadTweets(-1, -1);
		}
	}
	
	public void refreshTweets() {
		if(canCall(lastRefreshGotResult, lastRefresh, REFRESH_MORE_TIMEWINDOW)) {
			loadTweets(-1, since_tweet_id);
		}
	}
	
	public void loadMoreTweets() {
		if(canCall(lastLoadMoreGotResult, lastLoadMore, LOAD_MORE_TIMEWINDOW)) {
			loadTweets(max_tweet_id, -1);
		}
	}
	
	public void updateTweetIds(ArrayList<Tweet> tweets) {
		if(tweets.size() > 0) {
			max_tweet_id = ((Tweet)tweets.get(tweets.size() - 1)).tweet_id;
			since_tweet_id = ((Tweet)tweets.get(0)).tweet_id;
		}
		else {
			max_tweet_id = -1;
			since_tweet_id = -1;
		}
	}
	
	public void updateTweetIds() {
		updateTweetIds(tweets);
	}
}
