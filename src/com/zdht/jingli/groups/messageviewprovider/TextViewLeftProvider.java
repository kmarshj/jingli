package com.zdht.jingli.groups.messageviewprovider;

import android.content.Context;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zdht.core.im.IMMessageProtocol;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.model.XMessage;
import com.zdht.jingli.groups.utils.ExpressionCoding;
import com.zdht.utils.SystemUtils;

public class TextViewLeftProvider extends CommonViewProvider {

	
	public TextViewLeftProvider(OnViewClickListener listener) {
		super(listener);
	}

	@Override
	public boolean acceptHandle(IMMessageProtocol message) {
		if(message.getType() == XMessage.TYPE_TEXT){
			XMessage hm = (XMessage)message;
			return !hm.isFromSelf();
		}
		return false;
	}
	
	@Override
	protected void onSetViewHolder(View convertView, CommonViewHolder viewHolder) {
		super.onSetViewHolder(convertView, viewHolder);
		final Context context = convertView.getContext();
		TextViewHolder textViewHolder = (TextViewHolder)viewHolder;
		textViewHolder.mTextView = (TextView)LayoutInflater.from(context)
				.inflate(R.layout.message_content_text, null);
		textViewHolder.mTextView.setClickable(false);
		textViewHolder.mTextView.setMaxWidth(
				SystemUtils.getScreenWidth() - SystemUtils.dipToPixel(context, 110));
		textViewHolder.mContentView.addView(textViewHolder.mTextView);
	}

	@Override
	protected CommonViewHolder onCreateViewHolder() {
		return new TextViewHolder();
	}

	@Override
	protected void onUpdateView(CommonViewHolder viewHolder, XMessage m) {
		TextViewHolder tViewHolder = (TextViewHolder)viewHolder;
		final String strContent = m.getContent();
		tViewHolder.mTextView.setText(
				ExpressionCoding.spanMessage(tViewHolder.mTextView.getContext(),
				strContent, 
				0.6f,
				ImageSpan.ALIGN_BASELINE));
	}

	protected static class TextViewHolder extends CommonViewHolder{
		public TextView mTextView;
	}
}
