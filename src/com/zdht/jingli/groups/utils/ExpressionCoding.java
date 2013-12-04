package com.zdht.jingli.groups.utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.SCApplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.SparseArray;


public class ExpressionCoding {
	
	private static int EXPRESSION_CODING[];

	private static final SparseArray<String> mMapResIdToCoding = new SparseArray<String>();
	
	private static final HashMap<String, Integer> mMapCodingToResId = new HashMap<String, Integer>();
	
	private static boolean mInit = false;
	
	private static Pattern mPattern = Pattern.compile("\\[([a-zA-Z0-9\\u4e00-\\u9fa5]+?)\\]");
	
	private static SpannableStringBuilder	mSpannableStringBuilder = new SpannableStringBuilder();
	
	public static String getCodingByResId(int nResId){
		if(!mInit){
			init();
			mInit = true;
		}
		return mMapResIdToCoding.get(nResId);
	}
	
	public static int getResIdByCoding(String strCoding){
		if(!mInit){
			init();
			mInit = true;
		}
		Integer id = mMapCodingToResId.get(strCoding);
		return id == null ? 0 : id.intValue();
	}
	
	public static int[] getExpressionResIds(){
		if(!mInit){
			init();
			mInit = true;
		}
		return EXPRESSION_CODING;
	}
	
	public static SpannableStringBuilder spanMessage(Context context,
			String strMessage,float fScale,int verticalAlignment){
		mSpannableStringBuilder.clear();
		mSpannableStringBuilder.append(strMessage);
		Matcher matcher = mPattern.matcher(strMessage);
		while(matcher.find()){
			final int nId = ExpressionCoding.getResIdByCoding(matcher.group());
			if(nId != 0){
				Drawable d = context.getResources().getDrawable(nId);
				d.setBounds(0, 0, (int)(d.getIntrinsicWidth() * fScale),
						(int)(d.getIntrinsicHeight() * fScale));
				mSpannableStringBuilder.setSpan(
								new ImageSpan(d,verticalAlignment), 
								matcher.start(),
								matcher.end(), 
								SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return mSpannableStringBuilder;
	}
	
	private static void init(){
		final int nCount = 105;
		EXPRESSION_CODING = new int[nCount];
		final String strPackageName = SCApplication.getApplication().getPackageName();
		final Resources res = SCApplication.getApplication().getResources();
		for(int nIndex = 0;nIndex < nCount;++nIndex){
			int nResId = res.getIdentifier(String.format("smiley_%d", nIndex), 
					"drawable", strPackageName);
			EXPRESSION_CODING[nIndex] = nResId;
		}
		
		String strCodings[] = SCApplication.getApplication().getResources()
				.getStringArray(R.array.expression_coding);
		int nIndex = 0;
		for(String strCoding : strCodings){
			mMapCodingToResId.put(strCoding, EXPRESSION_CODING[nIndex]);
			mMapResIdToCoding.put(EXPRESSION_CODING[nIndex], strCoding);
			++nIndex;
		}
	}
}
