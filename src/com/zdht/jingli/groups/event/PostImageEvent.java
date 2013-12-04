package com.zdht.jingli.groups.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.model.UploadImage;

public class PostImageEvent extends PostFileEvent {

	public PostImageEvent(int nEventCode) {
		super(nEventCode);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run(Object... params) throws Exception {
		String strUrl = URLUtils.URL_PostFile;
		List<UploadImage> mActivityImages = (List<UploadImage>)params[0];
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("appId", URLUtils.KEY));
		String type = (String)params[1];
		list.add(new BasicNameValuePair("type", type));
		final List<String> listImagePath = new ArrayList<String>();
		for (int i = 0; i < mActivityImages.size(); i++) {
			listImagePath.add(mActivityImages.get(i).getmImagePath());
		}
		super.run(strUrl, list, listImagePath);
	}

}
