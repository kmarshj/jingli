package com.zdht.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.zdht.jingli.R;
import com.zdht.jingli.groups.adapter.CommonPagerAdapter;
import com.zdht.jingli.groups.adapter.SetBaseAdapter;
import com.zdht.jingli.groups.utils.ExpressionCoding;
import com.zdht.jingli.groups.utils.RecordViewHelper;
import com.zdht.utils.SystemUtils;

public class HPChatEditView extends BaseEditView implements OnClickListener,
															View.OnFocusChangeListener,
															ViewPager.OnPageChangeListener,
															RecordViewHelper.OnRecordListener,
															AdapterView.OnItemClickListener{

	public HPChatEditView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HPChatEditView(Context context) {
		super(context);
		init();
	}

	private static final int EXPRESSION_ONEPAGE_COUNT = 23;
	
	private ImageButton 	mBtnPressTalk;
	private View			mViewInput;
	
	private View			mViewExpressionSet;
	private ViewPager		mViewPagerExpression;
	private PageIndicator 	mPageIndicatorExpression;
	private View			mViewSwitch;
	
	private View			mViewMoreSet;
	
	private OnEditListener 	mOnEditListner;
	
	private RecordViewHelper mRecordViewHelper;
	
	private void init(){
		View v = LayoutInflater.from(getContext()).inflate(R.layout.chatedit, null);
		
		mBtnPressTalk = (ImageButton)v.findViewById(R.id.btnPressTalk);
		mEditText = (EditText)v.findViewById(R.id.etTalk);
		mViewInput = v.findViewById(R.id.viewInput);
		mViewExpressionSet = v.findViewById(R.id.viewExpressionSet);
		mViewPagerExpression = (ViewPager)v.findViewById(R.id.vpExpression);
		mPageIndicatorExpression = (PageIndicator)v.findViewById(R.id.pageIndicator);
		mViewMoreSet = v.findViewById(R.id.viewMoreSet);
		
		mViewSwitch = v.findViewById(R.id.btnSwitch);
		mViewSwitch.setOnClickListener(this);
		v.findViewById(R.id.btnAdd).setOnClickListener(this);
		v.findViewById(R.id.btnExpression).setOnClickListener(this);
		v.findViewById(R.id.btnSend).setOnClickListener(this);
		v.findViewById(R.id.btnCamera).setOnClickListener(this);
		v.findViewById(R.id.btnPhoto).setOnClickListener(this);
		mBtnPressTalk.setOnClickListener(this);
		
		mEditText.setOnFocusChangeListener(this);
		mEditText.setOnClickListener(this);
		mEditText.setFocusableInTouchMode(false);
		SystemUtils.addEditTextLengthFilter(mEditText, 500);
		
		mInputMethodManager = (InputMethodManager)getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		
		initRecordPrompt();
		
		addView(v);
		
		initExpressionView();
		
		initMoreSetView();
	}
	
	private void initRecordPrompt(){
		mRecordViewHelper = new RecordViewHelper();
		mRecordViewHelper.onCreate(getContext(), mBtnPressTalk, new Handler());
		mRecordViewHelper.setOnRecordListener(this);
	}
	
	private void initExpressionView(){
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
	
	private void initMoreSetView(){
		addPullUpView(mViewMoreSet);
		hidePullUpView(mViewMoreSet, false);
	}
	
	protected int[] getExpressionResIds(){
		return ExpressionCoding.getExpressionResIds();
	}

	@Override
	public void onClick(View v) {
		final int nId = v.getId();
		if(nId == R.id.btnSwitch){
			if(mViewInput.getVisibility() == View.VISIBLE){
				v.setBackgroundResource(R.drawable.msg_bar_text);
				mViewInput.setVisibility(View.GONE);
				mBtnPressTalk.setVisibility(View.VISIBLE);
				hidePullUpView(mViewExpressionSet, true);
				hidePullUpView(mViewMoreSet, true);
				hideInputMethod();
			}else{
				v.setBackgroundResource(R.drawable.msg_bar_voice);
				mViewInput.setVisibility(View.VISIBLE);
				showInputMethod();
				mBtnPressTalk.setVisibility(View.GONE);
			}
		}else if(nId == R.id.btnExpression){
			if(isPullUpViewVisible(mViewExpressionSet)){
				hidePullUpView(mViewExpressionSet, true);
			}else{
				showPullUpview(mViewExpressionSet);
				mViewSwitch.setBackgroundResource(R.drawable.msg_bar_voice);
				mViewInput.setVisibility(View.VISIBLE);
				mBtnPressTalk.setVisibility(View.GONE);
				mEditText.setFocusableInTouchMode(true);
				mEditText.requestFocus();
			}
		}else if(nId == R.id.etTalk){
			showInputMethod();
		}else if(nId == R.id.btnSend){
			String strMessage = mEditText.getText().toString();
			if (!TextUtils.isEmpty(strMessage)) {
				if(mOnEditListner != null){
					if(mOnEditListner.onSendCheck()){
						mOnEditListner.onSendText(strMessage);
					
						mEditText.getEditableText().clear();
					}
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
		}else if(nId == R.id.btnCamera){
			if(mOnEditListner != null){
				mOnEditListner.onSendCamera();
			}
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
//		if(hasFocus){
//			hidePullUpView(mViewExpressionSet, false);
//			hidePullUpView(mViewMoreSet, false);
//		}else{
//			hideInputMethod();
//		}
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
	
	public void setOnEditListener(OnEditListener listener){
		mOnEditListner = listener;
	}
	
	public void onPause(){
		mRecordViewHelper.onPause();
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
		mRecordViewHelper.onDestroy();
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		if(visibility == View.VISIBLE){
			mRecordViewHelper.onResume();
		}else{
			mRecordViewHelper.onPause();
		}
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
	
	public void onRecordStarted() {
	}

	public void onRecordEnded(String strRecordPath) {
		if(mOnEditListner != null){
			mOnEditListner.onSendVoice(strRecordPath);
		}
	}

	public void onRecordFailed(boolean bFailByNet) {
		if(mOnEditListner != null){
			mOnEditListner.onRecordFail(bFailByNet);
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
				gridView.setOnItemClickListener(HPChatEditView.this);
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
	
	public static interface OnEditListener{
		public boolean 	onSendCheck();
		
		public void		onRecordFail(boolean bFailByNet);
		
		public void 	onSendText(CharSequence s);
		
		public void	 	onSendVoice(String strPathName);
		
		public void 	onSendPhoto();
		
		public void 	onSendCamera();
	}
}
