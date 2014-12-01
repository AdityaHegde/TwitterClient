package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "user")
public class User extends Model {
	@Column(name = "user_id", unique = true)
	public String user_id;
	@Column(name = "name")
	public String name; 
	@Column(name = "screen_name")
	public String screen_name;
	@Column(name = "description")
	public String description;
	@Column(name = "profile_image_url")
	public String profile_image_url;
	
	public User() {
		super();
	}
	
	public User(JSONObject json) {
		try {
			this.user_id = json.getString("id_str");
			this.name = json.getString("name");
			this.screen_name = json.getString("screen_name");
			this.profile_image_url = json.getString("profile_image_url");
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}
}
