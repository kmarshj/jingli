package com.zdht.jingli.groups.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter {
	
	private List<View> viewList;
	
	private List<String> titleList;

	public ViewPagerAdapter(List<View> viewList, List<String> titleList) {
		this.viewList = viewList;
		this.titleList = titleList;
	}
	
	public void setViewList(List<View> viewList) {
		this.viewList = viewList;
	}
	
	public void setTitleList(List<String> titleList) {
		this.titleList = titleList;
	}
	
	public void addListItem(View view) {
		if(viewList == null) {
			viewList = new ArrayList<View>();
		}
		viewList.add(view);
		notifyDataSetChanged();
	}
	
	public List<View> getViewList() {
		return viewList;
	}
	
	public List<String> getTitleList() {
		return titleList;
	}
	
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position,
			Object object) {
		container.removeView(viewList.get(position));
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		if(titleList == null) {
			return null;
		}
		return titleList.get(position);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewList.get(position));
		return viewList.get(position);
	}

}
