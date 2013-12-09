package com.zdht.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.adapter.CommonPagerAdapter;
import com.zdht.jingli.groups.adapter.SetBaseAdapter;
import com.zdht.jingli.groups.utils.ExpressionCoding;
import com.zdht.utils.SystemUtils;

public class ActivityChatEditView extends BaseEditView implements OnClickListener,
															View.OnFocusChangeListener,
															ViewPager.OnPageChangeListener,
															AdapterView.OnItemClickListener,
															DialogInterface.OnClickListener{

	public ActivityChatEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ActivityChatEditView(Context context) {
		super(context);
		init();
	}

	private static final int EXPRESSION_ONEPAGE_COUNT = 23;
	
	private View			mViewInput;
	
	/** 表情视图 */
	private View			mViewExpressionSet;
	private ViewPager		mViewPagerExpression;
	private PageIndicator 	mPageIndicatorExpression;
	
	private View			mViewMoreSet;
	
	private OnLiveEditListener 	mOnEditListner;
	
	private void init(){
		View v = LayoutInflater.from(getContext()).inflate(R.layout.activity_chat_edit, null);
		// 输入框
		mEditText = (EditText)v.findViewById(R.id.activity_etTalk);
		mEditText.setOnFocusChangeListener(this);
		mEditText.setOnClickListener(this);
//		mEditText.setFocusableInTouchMode(false);
		SystemUtils.addEditTextLengthFilter(mEditText, 500);
		// 输入法管理器
//		mInputMethodManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		mViewInput = v.findViewById(R.id.activity_viewInput);
		mViewExpressionSet = v.findViewById(R.id.activity_viewExpressionSet);
		mViewPagerExpression = (ViewPager)v.findViewById(R.id.activity_vpExpression);
		mPageIndicatorExpression = (PageIndicator)v.findViewById(R.id.activity_pageIndicator);
		// 表情button
		View exp = v.findViewById(R.id.activity_btnExpression);
		exp.setOnClickListener(this);
		v.findViewById(R.id.activity_btnSend).setOnClickListener(this);
		SystemUtils.measureView(exp);
		
		// 加载视图
		ImageButton camera = (ImageButton)v.findViewById(R.id.activity_btnCamera);
		camera.setOnClickListener(this);
		SystemUtils.measureView(camera);
		int height = exp.getMeasuredHeight();
		int width = height * camera.getMeasuredWidth() / camera.getMeasuredHeight();
		
		camera.setLayoutParams(new LinearLayout.LayoutParams(width, height));
		
//		mViewMoreSet = v.findViewById(R.id.viewMoreSet);
		/*
		mViewSwitch = v.findViewById(R.id.btnSwitch);
		mViewSwitch.setOnClickListener(this);
		v.findViewById(R.id.btnAdd).setOnClickListener(this);
		v.findViewById(R.id.btnPhoto).setOnClickListener(this);
		mBtnPressTalk.setOnClickListener(this);
		
		
		
		initRecordPrompt();*/
		
		addView(v);
		
		initExpressionView();
		
//		initMoreSetView();
	}
	
	/**
	 * 初始化表情视图
	 */
	private void initExpressionView(){
		// 
		addPullUpView(mViewExpressionSet);
		
		mViewPagerExpression.setOnPageChangeListener(this);
		ExpressionPagerAdapter pagerAdapter = new ExpressionPagerAdapter();
		final int nResIds[] = getExpressionResIds();
		int nPageCount = nResIds.length / EXPRESSION_ONEPAGE_COUNT;
		if(nResIds.length % EXPRESSION_ONEPAGE_COUNT > 0){
			++nPageCount;
		}
		pagerAdapter.setPageCount(nPageCount);
		mPageIndicatorExpression.setSelectColor(0xff6f8536);
		mPageIndicatorExpression.setNormalColor(0xffafafaf);
		mPageIndicatorExpression.setPageCount(nPageCount);
		mPageIndicatorExpression.setPageCurrent(0);
		
		mViewPagerExpression.setAdapter(pagerAdapter);
		
		hidePullUpView(mViewExpressionSet, false);
	}
	
	protected int[] getExpressionResIds(){
		return ExpressionCoding.getExpressionResIds();
	}

	@Override
	public void onClick(View v) {
		final int nId = v.getId();
		if(nId == R.id.activity_btnExpression){// 表情图标
			if(isPullUpViewVisible(mViewExpressionSet)){
				System.out.println("隐藏表情");
				hidePullUpView(mViewExpressionSet, true);
			}else{
				System.out.println("显示表情");
				showPullUpview(mViewExpressionSet);
//				mEditText.setFocusableInTouchMode(true);
			}
		}else if(nId == R.id.etTalk){
			showInputMethod();
		}else if(nId == R.id.activity_btnSend){// 发送按钮
			String strMessage = mEditText.getText().toString();
			if (!TextUtils.isEmpty(strMessage)) {
				if(mOnEditListner != null){
					mOnEditListner.onSendText(strMessage);
					mEditText.getEditableText().clear();
				}
			}
		}else if(nId == R.id.btnAdd){
			if(isPullUpViewVisible(mViewMoreSet)){
				hidePullUpView(mViewMoreSet, true);
			}else{
				showPullUpview(mViewMoreSet);
			}
		}else if(nId == R.id.btnPhoto){
			if(mOnEditListner != null){
				mOnEditListner.onSendPhoto();
			}
		}else if(nId == R.id.activity_btnCamera){
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setTitle(R.string.upload_image).setItems(R.array.set_avatar, this);
			builder.create().show();
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus){
			hidePullUpView(mViewExpressionSet, false);
//			hidePullUpView(mViewMoreSet, false);
		}else{
			hideInputMethod();
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		mPageIndicatorExpression.setPageCurrent(arg0);
	}
	
	public void setOnLiveEditListener(OnLiveEditListener listener){
		mOnEditListner = listener;
	}
	
	public void onPause(){
//		mRecordViewHelper.onPause();
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
//		mRecordViewHelper.onDestroy();
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		/*if(visibility == View.VISIBLE){
			mRecordViewHelper.onResume();
		}else{
			mRecordViewHelper.onPause();
		}*/
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Integer resid = (Integer)parent.getItemAtPosition(position);
		if(resid.intValue() == 0){
			int nIndex = mEditText.getSelectionStart();
			if(nIndex > 0){
				final Editable editable = mEditText.getEditableText();
				ImageSpan[] spans = editable.getSpans(0, mEditText.length(), ImageSpan.class);
				final int length = spans.length;
				boolean bDelete = false;
				for (int i = 0; i < length; i++) {
					final int s = editable.getSpanStart(spans[i]);
					final int e = editable.getSpanEnd(spans[i]);
					if (e == nIndex) {
						editable.delete(s, e);
						bDelete = true;
						break;
					}
				}
				if(!bDelete){
					editable.delete(nIndex - 1, nIndex);
				}
			}
		}else{
			try{
				SpannableStringBuilder ssb = new SpannableStringBuilder(
						ExpressionCoding.getCodingByResId(resid));
				
				Drawable d = getResources().getDrawable(resid);
				d.setBounds(0, 0, (int)(d.getIntrinsicWidth() * 0.6), 
						(int)(d.getIntrinsicHeight() * 0.6));
				ssb.setSpan(new ImageSpan(d),
						0, ssb.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
				mEditText.append(ssb);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private class ExpressionPagerAdapter extends CommonPagerAdapter{
		
		@Override
		protected View getView(View v, int nPos) {
			ExpressionImageAdapter adapter;
			if(v == null){
				final Context context = getContext();
				final GridView gridView = new GridView(context);
				gridView.setColumnWidth(SystemUtils.dipToPixel(context, 32));
				gridView.setNumColumns(8);
				gridView.setVerticalSpacing(SystemUtils.dipToPixel(context, 10));
				gridView.setCacheColorHint(0x00000000);
				gridView.setSelector(new ColorDrawable(0x00000000));
				gridView.setStretchMode(GridView.STRETCH_SPACING);
				final int nPaddingHorizontal = SystemUtils.dipToPixel(context, 10);
				gridView.setPadding(nPaddingHorizontal, nPaddingHorizontal, nPaddingHorizontal, 0);
				
				adapter = new ExpressionImageAdapter(context);
				gridView.setAdapter(adapter);
				gridView.setOnItemClickListener(ActivityChatEditView.this);
				gridView.setTag(adapter);
				
				v = gridView;
			}else{
				adapter = (ExpressionImageAdapter)v.getTag();
			}
			
			final int nResIds[] = getExpressionResIds();
			final int nStart = nPos * EXPRESSION_ONEPAGE_COUNT;
			int nEnd = nStart + EXPRESSION_ONEPAGE_COUNT;
			if(nEnd > nResIds.length)nEnd = nResIds.length;
			
			adapter.clear();
			for(int nIndex = nStart;nIndex < nEnd;++nIndex){
				adapter.addItem(Integer.valueOf(nResIds[nIndex]));
			}
			adapter.addItem(0);
			
			return v;
		}
	}
	
	private static class ExpressionImageAdapter extends SetBaseAdapter<Integer>{

		private Context mContext;
		
		public ExpressionImageAdapter(Context context){
			mContext = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				final int nSize = SystemUtils.dipToPixel(mContext, 32);
				convertView = new ImageView(mContext);
				convertView.setLayoutParams(new GridView.LayoutParams(nSize,nSize));
			}
			
			Integer id = (Integer)getItem(position);
			final ImageView imageView = (ImageView)convertView;
			if(id.intValue() == 0){
				imageView.setImageResource(R.drawable.emotion_del);
			}else{
				imageView.setImageResource(id.intValue());
			}
			
			return imageView;
		}
		
	}
	
	public static interface OnLiveEditListener{
		public void 	onSendText(String s);
		
		public void 	onSendPhoto();
		
		public void 	onSendCamera();
	}

	@Override
	public void onClick(DialogInterface arg0, int which) {
		// TODO Auto-generated method stub
		if(mOnEditListner == null) {
			return;
		}
		switch(which) {
		case 0:
			mOnEditListner.onSendCamera();
			break;
		case 1:
			mOnEditListner.onSendPhoto();
			break;
		case 2:
			break;
		}
	}
}
