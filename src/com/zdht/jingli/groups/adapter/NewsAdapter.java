package com.zdht.jingli.groups.adapter;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.model.News;
import com.zdht.jingli.groups.provider.PosterBmpProvider;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 新闻资讯类适配器
 * @author think
 *
 */
public class NewsAdapter  extends SetBaseAdapter<News> {

	private Context mContext;
	private ViewHolder viewHolder;
	private boolean hasMore;
	
	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}
	
	public boolean hasMore() {
		return hasMore;
	}

	public NewsAdapter(Context context) {
		mContext = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		viewHolder = null;
		if(convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (viewHolder == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.adapter_news_item, null);
			viewHolder = new ViewHolder();
			viewHolder.poster = (ImageView)convertView.
					findViewById(R.id.adapter_news_item_poster);
			viewHolder.title = (TextView) convertView
					.findViewById(R.id.adapter_news_item_title);
			viewHolder.content = (TextView) convertView
					.findViewById(R.id.adapter_news_item_content);
			viewHolder.time = (TextView) convertView
					.findViewById(R.id.adapter_news_item_time);
			convertView.setTag(viewHolder);
		}
		
		final News news = (News)getItem(position);
		String url = news.getPosterUrl();
		if(!TextUtils.isEmpty(url)){
			Bitmap bitmap = PosterBmpProvider.getInstance().loadImage(url);
			if(bitmap == null) {
				viewHolder.poster.setImageResource(R.drawable.chat_img);
			} else {
				viewHolder.poster.setImageBitmap(bitmap);
			}
		} else {
			viewHolder.poster.setImageResource(R.drawable.chat_img);
		}
		
		// 设置新闻名称
		viewHolder.title.setText(news.getTitle());
		// 设置新闻内容
		viewHolder.content.setText(Html.fromHtml(news.getBody()));
		// 时间
		String date = news.getDate();
		if(!TextUtils.isEmpty(news.getDate())){
			viewHolder.time.setText(date);
		}
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView poster;
		TextView  title;
		TextView  content;
		TextView  time;
	}
}