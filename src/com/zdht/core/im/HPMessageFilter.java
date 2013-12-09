package com.zdht.core.im;

import com.zdht.jingli.groups.model.XMessage;

public class HPMessageFilter {
	
	public static boolean accept(String strAcceptFromId,XMessage m){
		return strAcceptFromId.equals(m.getFromId());
	}
}
