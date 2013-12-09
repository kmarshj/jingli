package com.zdht.jingli.groups.messageviewprovider;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.zdht.core.im.IMMessageProtocol;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.model.XMessage;
import com.zdht.utils.SystemUtils;

public class VoiceViewRightProvider extends VoiceViewLeftProvider {

	public VoiceViewRightProvider(Context context,OnViewClickListener listener) {
		super(context,listener);
	}

	@Override
	public boolean acceptHandle(IMMessageProtocol message) {
		if(message.getType() == XMessage.TYPE_VOICE){
			XMessage hm = (XMessage)message;
			return hm.isFromSelf();
		}
		return false;
	}
	
	@Override
	protected void onUpdateView(CommonViewHolder viewHolder, XMessage m) {
		VoiceViewHolder voiceHolder = (VoiceViewHolder)viewHolder;
		/*if(m.isVoiceUploading()){
			voiceHolder.mProgressBar.setVisibility(View.VISIBLE);
			voiceHolder.mImageViewVoice.setVisibility(View.GONE);
		}else if(m.isVoiceDownloading()){
			voiceHolder.mProgressBar.setVisibility(View.VISIBLE);
			voiceHolder.mImageViewVoice.setVisibility(View.VISIBLE);
		}else{
			ImageView imageViewVoice = voiceHolder.mImageViewVoice;
			voiceHolder.mProgressBar.setVisibility(View.GONE);
			imageViewVoice.setVisibility(View.VISIBLE);
			if (m.isUploadSuccess()) {
				if (VoicePlayProcessor.getInstance().isPlaying(m)) {
					imageViewVoice.setImageResource(R.drawable.animlist_play_voice);
					AnimationDrawable ad = (AnimationDrawable)imageViewVoice.getDrawable();
					ad.start();
				} else{
					imageViewVoice.setImageResource(R.drawable.voice_played);
				}
			}else{
				imageViewVoice.setImageResource(R.drawable.voice_played);
				setShowWarningView(voiceHolder.mViewWarning, true);
			}
		}*/
		
		showSeconds(voiceHolder.mTextViewVoice, m);
	}

	@Override
	protected void onSetViewHolder(View convertView, CommonViewHolder viewHolder) {
		super.onSetViewHolder(convertView, viewHolder);
		VoiceViewHolder vHolder = (VoiceViewHolder)viewHolder;
		vHolder.mImageViewVoice.setScaleType(ScaleType.MATRIX);
		Matrix m = new Matrix();
		final int fPv = SystemUtils.dipToPixel(convertView.getContext(), 9);
		m.setRotate(180, fPv,fPv);
		vHolder.mImageViewVoice.setImageMatrix(m);
	}

	@Override
	protected View onCreateVoiceView(Context context) {
		return LayoutInflater.from(context).inflate(R.layout.message_content_voice_right, null);
	}

}
