package com.zdht.jingli.groups.adapter;

import com.zdht.jingli.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


/**
 * <主界面底部标签适配器>
 * 
 * @author luchengsong
 */
public class MainTabAdapter extends BaseAdapter {
	private final ImageView[] imageViews;

	private final Integer[] imageIDs;

	public MainTabAdapter(Context context, Integer[] imageIDs, int width, int height) {
		this.imageIDs = imageIDs;
		imageViews = new ImageView[imageIDs.length];

		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i] = new ImageView(context);
			imageViews[i].setLayoutParams(new GridView.LayoutParams(width,
					height));
		}
	}

	@Override
	public int getCount() {
		return imageIDs.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public ImageView getView(int position, View convertView, ViewGroup parent) {
		return imageViews[position];
	}

	/**
	 * 点击设置
	 * 
	 * @param selectedID
	 */
	public void setFocus(int selectedID) {
		switch (selectedID) {
		case 0:
			imageViews[0].setImageResource(R.drawable.huodong_c);
			imageViews[1].setImageResource(R.drawable.liaotian_n);
			imageViews[2].setImageResource(R.drawable.shangjia_n);
			imageViews[3].setImageResource(R.drawable.tongxulu_n);
			imageViews[4].setImageResource(R.drawable.shezhi_n);
			break;
		case 1:
			imageViews[0].setImageResource(R.drawable.huodong_n);
			imageViews[1].setImageResource(R.drawable.liaotian_c);
			imageViews[2].setImageResource(R.drawable.shangjia_n);
			imageViews[3].setImageResource(R.drawable.tongxulu_n);
			imageViews[4].setImageResource(R.drawable.shezhi_n);
			break;
		case 2:
			imageViews[0].setImageResource(R.drawable.huodong_n);
			imageViews[1].setImageResource(R.drawable.liaotian_n);
			imageViews[2].setImageResource(R.drawable.shangjia_c);
			imageViews[3].setImageResource(R.drawable.tongxulu_n);
			imageViews[4].setImageResource(R.drawable.shezhi_n);
			break;
		case 3:
			imageViews[0].setImageResource(R.drawable.huodong_n);
			imageViews[1].setImageResource(R.drawable.liaotian_n);
			imageViews[2].setImageResource(R.drawable.shangjia_n);
			imageViews[3].setImageResource(R.drawable.tongxulu_c);
			imageViews[4].setImageResource(R.drawable.shezhi_n);
			break;
		case 4:
			imageViews[0].setImageResource(R.drawable.huodong_n);
			imageViews[1].setImageResource(R.drawable.liaotian_n);
			imageViews[2].setImageResource(R.drawable.shangjia_n);
			imageViews[3].setImageResource(R.drawable.tongxulu_n);
			imageViews[4].setImageResource(R.drawable.shezhi_c);
			break;
		default:
			break;
		}
	}
}