package com.codepath.apps.restclienttemplate;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class ComposeActivity extends ActionBarActivity {
	private EditText etTweet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		setupView();
	}

	private void setupView() {
		etTweet = (EditText) findViewById(R.id.etTweet);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compose, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_tweet) {
			Intent data = new Intent();
			data.putExtra("tweet", etTweet.getText().toString());
			setResult(RESULT_OK, data);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
