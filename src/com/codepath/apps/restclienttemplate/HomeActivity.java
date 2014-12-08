package com.codepath.apps.restclienttemplate;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.activeandroid.util.Log;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

@SuppressWarnings("deprecation")
//TODO : user the new method for API 21 to create tabs
public class HomeActivity extends ActionBarActivity implements TabListener {
	private static String HOME_TIMELINE_API = "statuses/home_timeline.json";
	private static String MENTIONS_TIMELINE_API = "statuses/mentions_timeline.json";
	
	private TweetsLoader tweetsLoader;
	private TweetsFragment tweetsFragment;
	
	private boolean isHome;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		setupTabs();
	}

	private void setupTabs() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab home = actionBar
			.newTab()
			.setText("Home")
			.setTag("home")
			.setTabListener(this);

		actionBar.addTab(home);
		actionBar.selectTab(home);

		Tab mentions = actionBar
			.newTab()
			.setText("Mentions")
			.setTag("mentions")
			.setTabListener(this);

		actionBar.addTab(mentions); 
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
		switch(id) {
			case R.id.action_compose:
				Intent i = new Intent(getBaseContext(), ComposeActivity.class);
				startActivityForResult(i, 0);
				return true;
			case R.id.action_profile:
				openProfileActivity(getBaseContext(), null);
				return true;
			default: break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("post");
		if (resultCode == RESULT_OK) {
			RestClient client = RestClientApp.getRestClient();
			client.postTweet(data.getStringExtra("tweet"), new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject json) {
					if(isHome) {
						Tweet tweet = new Tweet(json);
						tweetsFragment.addTweet(tweet);
					}
				}
			});
		}
	}
	
	public static void openProfileActivity(Context context, String user_id) {
		Intent i = new Intent(context, ProfileActivity.class);
		if(user_id == null) {
			i.putExtra("isOwnProfile", true);
		}
		else {
			i.putExtra("user_id", user_id);
		}
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		System.out.println("Tab selected");
		FragmentTransaction sft = getSupportFragmentManager().beginTransaction();
		if(tweetsLoader == null) {
			tweetsLoader = new TweetsLoader();
		}
		if(tab.getTag().toString().equals("home")) {
			tweetsLoader.api = HOME_TIMELINE_API;
			isHome = true;
		}
		else {
			tweetsLoader.api = MENTIONS_TIMELINE_API;
			isHome = false;
		}
		if(tweetsFragment == null) {
			System.out.println("Creating fragment");
			tweetsFragment = new TweetsFragment(tweetsLoader);
			sft.replace(R.id.tweets_placeholder, tweetsFragment);
		}
		else {
			System.out.println("Reattaching fragment");
			sft.attach(tweetsFragment);
			tweetsLoader.loadAllTweets();
		}
		sft.commit();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		System.out.println("Detaching fragment");
		FragmentTransaction sft = getSupportFragmentManager().beginTransaction();
		if(tweetsFragment != null) {
			sft.detach(tweetsFragment);
		}
		sft.commit();
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		System.out.println("Tab reselected");
	}
}
