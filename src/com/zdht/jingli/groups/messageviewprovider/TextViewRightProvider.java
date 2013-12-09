package com.zdht.jingli.groups.messageviewprovider;

import com.zdht.core.im.IMMessageProtocol;
import com.zdht.jingli.groups.model.XMessage;

public class TextViewRightProvider extends TextViewLeftProvider {

	public TextViewRightProvider(OnViewClickListener listener) {
		super(listener);
	}

	@Override
	public boolean acceptHandle(IMMessageProtocol message) {
		if(message.getType() == XMessage.TYPE_TEXT){
			XMessage hm = (XMessage)message;
			return hm.isFromSelf();
		}
		return false;
	}
}
