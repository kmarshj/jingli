package com.zdht.jingli.groups.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.model.RecentChat;
import com.zdht.jingli.groups.model.XMessage;
import com.zdht.jingli.groups.provider.AvatarBmpProvider;
import com.zdht.jingli.groups.utils.ExpressionCoding;
import com.zdht.utils.DateUtils;

public class RecentChatAdapter extends SetBaseAdapter<RecentChat> {
	
	private static final SimpleDateFormat DATEFORMAT_HM 	= new SimpleDateFormat("H:mm");
	private static final SimpleDateFormat DATEFORMAT_MD 	= new SimpleDateFormat("M月d日");
	private static final SimpleDateFormat DATEFORMAT_YMD 	= new SimpleDateFormat("y年M月d日");
	
	private Context mContext;
	
	public RecentChatAdapter(Context context){
		mContext = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_recentchat, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageViewAvatar = (ImageView)convertView.findViewById(R.id.ivAvatar);
			viewHolder.mTextViewName = (TextView)convertView.findViewById(R.id.tvName);
			viewHolder.mTextViewTime = (TextView)convertView.findViewById(R.id.tvTime);
			viewHolder.mTextViewMessage = (TextView)convertView.findViewById(R.id.tvMessage);
			viewHolder.mTextViewUnreadMessageCount = (TextView)convertView.findViewById(R.id.tvNumber);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		RecentChat recentChat = (RecentChat)getItem(position);
		
		final int nUnreadMessageCount = recentChat.getUnreadMessageCount();
		if(nUnreadMessageCount > 0){
			viewHolder.mTextViewUnreadMessageCount.setVisibility(View.VISIBLE);
			viewHolder.mTextViewUnreadMessageCount.setText(String.valueOf(nUnreadMessageCount));
		}else{
			viewHolder.mTextViewUnreadMessageCount.setVisibility(View.GONE);
		}
		
		XMessage m = recentChat.getLastMessage();
		
		if(m != null){
			viewHolder.mImageViewAvatar.setImageBitmap(AvatarBmpProvider.getInstance().loadImage(m.getAvatar()));
			viewHolder.mTextViewName.setText(recentChat.getName());
			long lSendTime = m.getSendTime();
			if(lSendTime != 0){
				viewHolder.mTextViewTime.setText(getSendTimeShow(lSendTime));
			}
			final int nMessageType = m.getType();
			if(nMessageType == XMessage.TYPE_VOICE){
				viewHolder.mTextViewMessage.setText(R.string.inquiry_voice);
			}else if(nMessageType == XMessage.TYPE_PHOTO){
				viewHolder.mTextViewMessage.setText(R.string.inquiry_photo);
			}else{
				viewHolder.mTextViewMessage.setText(
						ExpressionCoding.spanMessage(mContext,
								m.getContent(), 0.6f,ImageSpan.ALIGN_BOTTOM));
			}
		}else{
			viewHolder.mTextViewTime.setText("");
			viewHolder.mTextViewMessage.setText("");
		}
		
		return convertView;
	}
	
	private String getSendTimeShow(long lSendTime){
		String strRet = null;
		try {
			Date date = new Date(lSendTime);
			if(DateUtils.isToday(lSendTime)){
				strRet = DATEFORMAT_HM.format(date);
			}else if(DateUtils.isInCurrentYear(lSendTime)){
				strRet = DATEFORMAT_MD.format(date);
			}else{
				strRet = DATEFORMAT_YMD.format(date);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return strRet;
	}

	private static class ViewHolder{
		public ImageView 	mImageViewAvatar;
		
		public TextView		mTextViewName;
		
		public TextView		mTextViewTime;
		
		public TextView		mTextViewMessage;
		
		public TextView		mTextViewUnreadMessageCount;
	}
}
