package com.zdht.jingli.groups.model;

import java.io.Serializable;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.SCApplication;



public class Sex implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final Sex MALE 	= new Sex(0);
	public static final Sex FEMALE 	= new Sex(1);
	
	private final int mValue;
	
	private Sex(int nValue){
		mValue = nValue;
	}
	
	public static Sex valueOf(int nValue){
		if(nValue == 0){
			return MALE;
		}else{
			return FEMALE;
		}
	}
	
	public static Sex valueOf(String strValue){
		if(strValue.equals(SCApplication.getApplication().getString(R.string.male))){
			return MALE;
		}else{
			return FEMALE;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null){
			return false;
		}
		if(this == o){
			return true;
		}
		
		if(o instanceof Sex){
			return getIntValue() == ((Sex)o).getIntValue();
		}
		
		return false;
	}

	public int getIntValue(){
		return mValue;
	}
	
	public String getStringValue(){
		if(this == MALE){
			return SCApplication.getApplication().getString(R.string.male);
		}else{
			return SCApplication.getApplication().getString(R.string.female);
		}
	}

	@Override
	public String toString() {
		return getStringValue();
	}
	
}
