package com.zdht.jingli.groups.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
/**
 * 新闻资讯
 * @author think
 *
 */
public class News implements Serializable{

	/***/
	private static final long serialVersionUID = -3770252783703464287L;

	private static final String JSON_KEY_CREATED = "created";
	private static final String JSON_KEY_TITLE = "title";
	private static final String JSON_KEY_DATE = "date";
	private static final String JSON_KEY_AUTHOR = "author";
	private static final String JSON_KEY_ID = "id";
	private static final String JSON_KEY_BODY = "body";
	private static final String JSON_KEY_POSTER = "poster";
	private static final String JSON_KEY_IMAGES = "images";
	
	private String created;
	private String title;
	private String date;
	private String author;
	private String body;
	private String id;
	private String posterUrl;
	private List<String> images;
	
	public News(JSONObject json)throws JSONException{
		if(json.has(JSON_KEY_CREATED)) {
			String ms = json.getString(JSON_KEY_CREATED);
			Date date = new Date(Long.parseLong(ms));
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			created = format.format(date);
		}

		title = json.getString(JSON_KEY_TITLE);

		if(json.has(JSON_KEY_DATE)) {
			date = json.getString(JSON_KEY_DATE);
		}

		if(json.has(JSON_KEY_AUTHOR)) {
			author = json.getString(JSON_KEY_AUTHOR);
		}

		id = json.getString(JSON_KEY_ID);

		body = json.getString(JSON_KEY_BODY);
		
		if(json.has(JSON_KEY_POSTER)) {
			posterUrl = json.getString(JSON_KEY_POSTER);
		}
		images = new ArrayList<String>();
		if(json.has(JSON_KEY_IMAGES)){
			String image = json.getString(JSON_KEY_IMAGES);
			if(!TextUtils.isEmpty(image)){
				String[] imgs = image.split(",");
				for(String img : imgs) {
					images.add(img);
				}
			}
		}
	}
	
	public List<String> getImages() {
		return images;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/**
	 * @return 创建者
	 */
	public String getCreated() {
		return created;
	}
	
	/**
	 * @return 标题
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return 创建时间
	 */
	public String getDate() {
		return date;
	}
	
	/**
	 * @return 作者
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return 内容
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return posterUrl
	 */
	public String getPosterUrl() {
		return posterUrl;
	}
	
	
	/*public void setCreated(String created) {
		this.created = created;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public void setId(String id) {
		this.id = id;
	}*/
	
	
}
