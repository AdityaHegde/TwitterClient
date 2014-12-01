package com.codepath.apps.restclienttemplate.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Tweets")
public class Tweet extends Model {
	@Column(name = "tweet_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	public int tweet_id;
	@Column(name = "user", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
	public User user;
	@Column(name = "created_at")
	public long created_at;
	@Column(name = "text")
	public String text;
	
	public Tweet() {
		super();
	}
	
	public Tweet(JSONObject json) {
		try {
			this.tweet_id = json.getInt("id");
			
			SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
			this.created_at = format.parse(json.getString("created_at")).getTime();
			
			this.user = new User(json.getJSONObject("user"));
			this.text = json.getString("text");
		} catch(JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<Tweet> fromJson(JSONArray jsonArr, ArrayList<Tweet> tweets) {
		
		for(int i = 0; i < jsonArr.length(); i++) {
			try {
				Tweet tweet = new Tweet(jsonArr.getJSONObject(i));
				tweet.save();
				tweets.add(tweet);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return tweets;
	}
	
	public static ArrayList<Tweet> fromDB(ArrayList<Tweet> tweets) {
		List<Tweet> ts = new Select().from(Tweet.class).execute();
		
		for(int i = 0; i < ts.size(); i++) {
			tweets.add((Tweet) ts.get(i));
		}
		
		return tweets;
	}
}
