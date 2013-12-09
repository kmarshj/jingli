package com.zdht.jingli.groups.messageviewprovider;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ProgressBar;

import com.zdht.core.im.IMMessageProtocol;
import com.zdht.jingli.R;
import com.zdht.jingli.groups.MessagePhotoProvider;
import com.zdht.jingli.groups.model.XMessage;

public class PhotoViewRightProvider extends PhotoViewLeftProvider {

	public PhotoViewRightProvider(OnViewClickListener listener) {
		super(listener);
	}

	@Override
	public boolean acceptHandle(IMMessageProtocol message) {
		if(message.getType() == XMessage.TYPE_PHOTO){
			XMessage m = (XMessage)message;
			return m.isFromSelf();
		}
		return false;
	}

	@Override
	protected void onUpdateView(CommonViewHolder viewHolder, XMessage m) {
		PhotoViewHolder pHolder = (PhotoViewHolder)viewHolder;
		Bitmap bmp = MessagePhotoProvider.loadThumbPhoto(m);
		if(bmp == null){
			pHolder.mImageViewPhoto.setImageResource(R.drawable.chat_img);
		}else{
			pHolder.mImageViewPhoto.setImageBitmap(bmp);
		}
		if(m.isPhotoUploading()){
			final ProgressBar pb = pHolder.mProgressBar;
			pb.setVisibility(View.VISIBLE);
			pb.setProgress(m.getPhotoUploadPercentage());
		}else if(m.isThumbPhotoDownloading()){
			pHolder.mProgressBar.setVisibility(View.VISIBLE);
			pHolder.mProgressBar.setProgress(m.getThumbPhotoDownloadPercentage());
		}else if(m.isUploadSuccess()){
			pHolder.mProgressBar.setVisibility(View.GONE);
		}else{
			pHolder.mProgressBar.setVisibility(View.GONE);
			setShowWarningView(pHolder.mViewWarning, true);
		}
	}
}
