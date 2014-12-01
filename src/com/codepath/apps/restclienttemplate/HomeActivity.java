package com.codepath.apps.restclienttemplate;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class HomeActivity extends ActionBarActivity implements OnScrollListener {
	private static final int VISIBLE_THRESHOLD = 10;
	private ArrayList<Tweet> tweets;
	private TweetAdapter tweetAdapter;
	private PullToRefreshListView lvTweets;
	private boolean loading = false;
	private int max_tweet_id = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initUIL();
		setupAdapter();
	}

	private void setupAdapter() {
		lvTweets = (PullToRefreshListView) findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		tweetAdapter = new TweetAdapter(getBaseContext(), tweets);
		lvTweets.setAdapter(tweetAdapter);
		lvTweets.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				loading = true;
				loadTweets(-1);
			}
		});
		lvTweets.setOnScrollListener(this);
	}

	private void loadTweets(final int max_id) {
		if(isNetworkAvailable()) {
			RestClient client = RestClientApp.getRestClient();
			Log.d("Debug", "Call made");
			client.getHomeTimeline(max_id, new JsonHttpResponseHandler() {
				public void onSuccess(int arg0, JSONArray jsonArray) {
					Log.d("Debug", "Call returned");
					if(max_id == -1) {
						tweets.clear();
					}
					Tweet.fromJson(jsonArray, tweets);
					if(tweets.size() > 0) {
						max_tweet_id = ((Tweet)tweets.get(tweets.size() - 1)).tweet_id;
					}
					else {
						max_tweet_id = -1;
					}
					tweetAdapter.notifyDataSetChanged();
					lvTweets.onRefreshComplete();
					loading = false;
				}
				
				@Override
				public void onSuccess(int arg0, JSONObject arg1) {
					Log.d("Debug", arg1.toString());
					super.onSuccess(arg0, arg1);
				}
				
				@Override
				public void onFailure(Throwable arg0, String arg1) {
					Log.d("Debug", arg0.toString());
					Log.d("Debug", arg1);
					super.onFailure(arg0, arg1);
				}
				
				@Override
				public void onStart() {
					Log.d("Debug", "start");
					super.onStart();
				}
				
				@Override
				public void onFinish() {
					Log.d("Debug", "finish");
					super.onFinish();
				}
			});
		}
		else {
			tweets.clear();
			Tweet.fromDB(tweets);
			max_tweet_id = ((Tweet)tweets.get(tweets.size() - 1)).tweet_id;
			tweetAdapter.notifyDataSetChanged();
			lvTweets.onRefreshComplete();
			loading = false;
		}
	}
	
	public Boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == R.id.action_compose) {
			Intent i = new Intent(getBaseContext(), ComposeActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    private void initUIL() {
    	ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getBaseContext())
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.denyCacheImageMultipleSizesInMemory()
		.discCacheFileNameGenerator(new Md5FileNameGenerator())
		.discCacheSize(50 * 1024 * 1024)
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.build();
    	ImageLoader.getInstance().init(config);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		//Log.d("Debug", totalItemCount + " - " + firstVisibleItem);
		//Log.d("Debug", loading + "");
		if(!loading && totalItemCount - firstVisibleItem < VISIBLE_THRESHOLD) {
			loading = true;
			loadTweets(this.max_tweet_id);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			RestClient client = RestClientApp.getRestClient();
			client.postTweet(data.getStringExtra("tweet"), new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject json) {
					Tweet tweet = new Tweet(json);
					tweets.add(0, tweet);
					tweetAdapter.notifyDataSetChanged();
				}
			});
		}
	}
}
