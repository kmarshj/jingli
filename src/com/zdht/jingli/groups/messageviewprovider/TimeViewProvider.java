package com.zdht.jingli.groups.messageviewprovider;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zdht.core.im.IMMessageProtocol;
import com.zdht.core.im.IMMessageViewProvider;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.model.XMessage;

public class TimeViewProvider extends IMMessageViewProvider {

	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("MM月dd日 HH:mm");
	
	@Override
	public boolean acceptHandle(IMMessageProtocol message) {
		if(message.getType() == XMessage.TYPE_TIME){
			return true;
		}
		return false;
	}

	@Override
	public View getView(IMMessageProtocol message, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_time, null);
		}
		
		XMessage m = (XMessage)message;
		
		TextView textView = (TextView)convertView.findViewById(R.id.tvGroupTime);
		Date date = new Date(m.getGroupTime());
		textView.setText(TIME_FORMAT.format(date));
		
		return convertView;
	}

}
