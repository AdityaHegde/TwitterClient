package com.codepath.apps.restclienttemplate;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileActivity extends ActionBarActivity {
	private User user;
	private TweetsLoader loader;
	private TweetsFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		loadUser();
	}

	private void loadUser() {
		RestClient client = RestClientApp.getRestClient();
		Intent i = getIntent();
		if(i.getBooleanExtra("isOwnProfile", false)) {
			client.getLoogedInUserData(new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject json) {
					user = new User(json);
					setupActivity();
				}
			});
		}
		else {
			client.getUserData(getIntent().getStringExtra("user_id"), new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject json) {
					user = new User(json);
					setupActivity();
				}
			});
		}
	}

	private void setupActivity() {
		setTitle("@" + user.screen_name);
		
		ImageView ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
		TextView tvName = (TextView) findViewById(R.id.tvName);
		TextView tvDesc = (TextView) findViewById(R.id.tvDescription);
		TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
		TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
		
		ImageLoader.getInstance().displayImage(user.profile_image_url, ivProfilePic);
		tvName.setText(user.name);
		tvDesc.setText(user.description);
		tvFollowers.setText(user.followers_count + " Followers");
		tvFollowing.setText(user.following_count + " Following");
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		loader = new TweetsLoader("statuses/user_timeline.json");
		loader.params.put("user_id", user.user_id);
		fragment = new TweetsFragment(loader);
		ft.replace(R.id.tweets_placeholder, fragment);
		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
