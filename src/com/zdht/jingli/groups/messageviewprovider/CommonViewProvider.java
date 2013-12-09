package com.zdht.jingli.groups.messageviewprovider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zdht.core.im.IMMessageProtocol;
import com.zdht.core.im.IMMessageViewProvider;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.localinfo.LocalInfoManager;
import com.zdht.jingli.groups.model.XMessage;
import com.zdht.jingli.groups.provider.AvatarBmpProvider;

public abstract class CommonViewProvider extends IMMessageViewProvider implements
													View.OnClickListener,
													View.OnLongClickListener{

	public CommonViewProvider(OnViewClickListener listener){
		setOnViewClickListener(listener);
	}
	
	@Override
	public View getView(IMMessageProtocol message, View convertView, ViewGroup parent) {
		CommonViewHolder viewHolder = null;
		XMessage m = (XMessage)message;
		
		if(convertView == null){
			viewHolder = onCreateViewHolder();
			convertView = onCreateView(m,parent.getContext());
			onSetViewHolder(convertView, viewHolder);
			viewHolder.mImageViewAvatar.setOnClickListener(this);
			viewHolder.mContentView.setOnClickListener(this);
			viewHolder.mContentView.setOnLongClickListener(this);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (CommonViewHolder)convertView.getTag();
		}
		
		viewHolder.mImageViewAvatar.setTag(m);
		viewHolder.mContentView.setTag(m);
		
		if(m.isFromSelf()){
			viewHolder.mImageViewAvatar.setImageBitmap(AvatarBmpProvider.getInstance().loadImage(
					LocalInfoManager.getInstance().getAvatar()));
		}else{
			viewHolder.mImageViewAvatar.setImageBitmap(AvatarBmpProvider.getInstance().loadImage(m.getAvatar()));
		}
		
		if(m.isFromSelf()){
			viewHolder.mTextViewNickname.setText(LocalInfoManager.getInstance().getmLocalInfo().getRealName());
		}else{
			viewHolder.mTextViewNickname.setText(m.getUserName());
		}	
		
		onSetContentViewBackground(viewHolder.mContentView, m);
		
		onUpdateWarningView(viewHolder.mViewWarning, m);
		
		onUpdateView(viewHolder, m);
		
		return convertView;
	}

	protected View onCreateView(XMessage m,Context context){
		if(m.isFromSelf()){
			return LayoutInflater.from(context).inflate(R.layout.message_common_right, null);
		}else{
			return LayoutInflater.from(context).inflate(R.layout.message_common_left, null);
		}
	}
	
	protected CommonViewHolder onCreateViewHolder(){
		return new CommonViewHolder();
	}
	
	protected void onSetViewHolder(View convertView,CommonViewHolder viewHolder){
		viewHolder.mImageViewAvatar = (ImageView)convertView.findViewById(R.id.ivAvatar);
		viewHolder.mTextViewNickname = (TextView)convertView.findViewById(R.id.tvNickname);
		viewHolder.mContentView = (FrameLayout)convertView.findViewById(R.id.viewContent);
		viewHolder.mViewWarning = (ImageView)convertView.findViewById(R.id.ivWarning);
	}
	
	protected void onSetContentViewBackground(View contentView,XMessage m){
		if(!m.isFromSelf()){
			contentView.setBackgroundResource(R.drawable.chat_other);
		}
	}
	
	protected void onUpdateWarningView(ImageView viewWarning,XMessage m){
		if(m.isFromSelf()){
			if(m.isSended()){
				if(m.isSendSuccess()){
					viewWarning.setVisibility(View.GONE);
				}else{
					viewWarning.setVisibility(View.VISIBLE);
				}
			}else{
				viewWarning.setVisibility(View.GONE);
			}
		}else{
			viewWarning.setVisibility(View.GONE);
		}
	}
	
	protected void setShowWarningView(ImageView viewWarning,boolean bShow){
		if(bShow){
			viewWarning.setVisibility(View.VISIBLE);
		}else{
			viewWarning.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onClick(View v) {
		if(mOnViewClickListener != null){
			mOnViewClickListener.onViewClicked((IMMessageProtocol)v.getTag(), v.getId());
		}
	}

	@Override
	public boolean onLongClick(View v) {
		if(mOnViewClickListener != null){
			return mOnViewClickListener.onViewLongClicked((IMMessageProtocol)v.getTag(), v.getId());
		}
		return false;
	}

	@Override
	public boolean acceptHandle(IMMessageProtocol message) {
		return false;
	}

	protected abstract void onUpdateView(CommonViewHolder viewHolder,XMessage m);
	
	protected static class CommonViewHolder{
		
		public ImageView 	mImageViewAvatar;
		
		public TextView		mTextViewNickname;
		
		public FrameLayout	mContentView;
		
		public ImageView	mViewWarning;
	}
}
