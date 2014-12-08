package com.codepath.apps.restclienttemplate;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;


public class TweetAdapter extends ArrayAdapter<Tweet> {

	public TweetAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.tweet, parent, false);
		}
		
		final Tweet t = (Tweet) getItem(position);

		TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
		TextView tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
		TextView tvCreateAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
		TextView tvTweet = (TextView) convertView.findViewById(R.id.tvTweet);
		ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
		
		tvUserName.setText(t.user.name);
		tvScreenName.setText("@"+t.user.screen_name);
		tvCreateAt.setText(DateUtils.getRelativeTimeSpanString(t.created_at, new Date().getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));
		tvTweet.setText(t.text);
		ImageLoader.getInstance().displayImage(t.user.profile_image_url, ivProfileImage);
		
		ivProfileImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HomeActivity.openProfileActivity(getContext(), t.user.user_id);
			}
		});
		
		return convertView;
	}

}
