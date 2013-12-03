package com.zdht.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.widget.EditText;

/*import com.zdht.school.SCApplication;
*/
public class SystemUtils {
	
	private static float sDensity = 0;
	
	private static int WIDTH 	= 0;
	private static int HEIGHT 	= 0;
	private static int DPI      = 0;
	
	/**
	 * 计算视图的宽高。
	 * @param view
	 */
	public static void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
       
        int nWidthSpec = ViewGroup.getChildMeasureSpec(0,
                0 + 0, p.width);
        int nHeight = p.height;
        int nHeightSpec;
       
        if (nHeight > 0) {
            nHeightSpec = MeasureSpec.makeMeasureSpec(nHeight, MeasureSpec.EXACTLY); 
        } else {
            nHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(nWidthSpec, nHeightSpec);
    }
	
	public static int getScreenWidth(){
//		if(WIDTH == 0){
//			WindowManager wm = (WindowManager)SCApplication.getApplication()
//					.getSystemService(Context.WINDOW_SERVICE);
//			DisplayMetrics dm = new DisplayMetrics();
//			wm.getDefaultDisplay().getMetrics(dm);
//			DPI = dm.densityDpi;
//			WIDTH = dm.widthPixels;
//			HEIGHT = dm.heightPixels;
//		}
		return WIDTH;
	}
	
	public static int getScreenHeight(){
		if(HEIGHT == 0){
			getScreenWidth();
		}
		return HEIGHT;
	}
	
	public static int getScreenDpi(){
		if(DPI == 0){
			getScreenWidth();
		}
		return DPI;
	}
	
	public static boolean isWifi(Context context){
		ConnectivityManager cm = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI){
			return true;
		}
		return false;
	}
	
	public static boolean isNetworkAvailable(Context context){
		ConnectivityManager cm = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni != null && ni.isConnected()){
			return true;
		}
		return false;
	}
	
	public static boolean isNetworkAvailable(Intent intent){
		Parcelable p = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		if(p != null){
			NetworkInfo ni = (NetworkInfo)p;
			if(ni.getState() == NetworkInfo.State.CONNECTED){
				return true;
			}
		}
		return false;
	}
	
	public static int randomRange(int nStart,int nEnd){
		if(nStart >= nEnd){
			return nEnd;
		}
		return nStart + (int)(Math.random() * (nEnd - nStart));
	}
	
	public static boolean isExternalStorageMounted(){
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
	
	public static void dismissProgressDialog(ProgressDialog pd){
		try{
			if(pd != null){
				pd.dismiss();
			}
		}catch(Exception e){
		}
	}
	
	public static String getExternalCachePath(Context context){
		return Environment.getExternalStorageDirectory().getPath() + 
				"/Android/data/" + context.getPackageName() + "/cache";
	}
	
	public static int dipToPixel(Context context,int nDip){
		if(sDensity == 0){
			final WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics dm = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(dm);
			sDensity = dm.density;
		}
		return (int)(sDensity * nDip);
	}
	
	public static String getDeviceUUID(Context context){
		final TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String strIMEI = tm.getDeviceId();
		if(TextUtils.isEmpty(strIMEI)){
			strIMEI = "1";
		}
		
		String strMacAddress = getMacAddress(context);
		if(TextUtils.isEmpty(strMacAddress)){
			strMacAddress = "1";
		}
		
		return strIMEI + strMacAddress;
	}
	
	public static String getMacAddress(Context context){
		final WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		return wm.getConnectionInfo().getMacAddress();
	}
	
	public static void copyToClipBoard(Context context,String strText){
		final ClipboardManager manager = (ClipboardManager)context.getSystemService(
				Context.CLIPBOARD_SERVICE);
		manager.setText(strText);
	}
	
	public static boolean isEmail(String strEmail){
		Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(strEmail);
		return matcher.matches();
	}
	
	public static boolean isMobile(String mobiles){     
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");     
        Matcher m = p.matcher(mobiles);     
        return m.matches();     
    }
	
	public static String getVersionName(Context context){
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); 
			en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); 
			enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    return inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	    	ex.printStackTrace();
	    }
	    return null;
	}
	
	public static Location getLocation(Context context){
		final LocationManager locationManager = (LocationManager)context
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		final String strProvider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(strProvider);
		if(location == null){
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if(location == null){
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return location;
	}
	
	public static void	addEditTextLengthFilter(EditText editText,int nLengthLimit){
		InputFilter filters[] = editText.getFilters();
		if(filters == null){
			editText.getEditableText().setFilters(
					new InputFilter[]{new InputFilter.LengthFilter(nLengthLimit)});
		}else{
			final int nSize = filters.length + 1;
			InputFilter newFilters[] = new InputFilter[nSize];
			int nIndex = 0;
			for(InputFilter filter : filters){
				newFilters[nIndex++] = filter;
			}
			newFilters[nIndex] = new InputFilter.LengthFilter(nLengthLimit);
			editText.getEditableText().setFilters(newFilters);
		}
	}
	
	
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		if(bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}
	
	public static String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	
}
