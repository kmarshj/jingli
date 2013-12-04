package com.zdht.jingli.groups.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;

public class DialogUtils {
	
	
	public static void showAlertDialog(Context context, String title, String message, OnClickListener listenner) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if(!TextUtils.isEmpty(title)){
			builder.setTitle(title);
		}
		builder.setMessage(message);
		builder.setPositiveButton("确定", listenner);
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}
	
	public static void showAlertDialog(Context context, String message, OnClickListener listenner) {
		showAlertDialog(context, "", message, listenner);
	}
	
	public static void showAlertDialog(Context context, int titleRes, int messageResId, OnClickListener listenner) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if(titleRes > 0){
			builder.setTitle(titleRes);
		}
		builder.setMessage(messageResId);
		builder.setPositiveButton("确定", listenner);
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}
	
	public static void showAlertDialog(Context context, int messageResId, OnClickListener listenner) {
		showAlertDialog(context, 0, messageResId, listenner);
	}
	
	public static void showAlertDialog(Context context, int titleRes, String message, OnClickListener listenner) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if(titleRes > 0){
			builder.setTitle(titleRes);
		}
		builder.setMessage(message);
		builder.setPositiveButton("确定", listenner);
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}
	
	public static void showAlertDialog(Context context, String title, int messageResId, OnClickListener listenner) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if(!TextUtils.isEmpty(title)){
			builder.setTitle(title);
		}
		builder.setMessage(messageResId);
		builder.setPositiveButton("确定", listenner);
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}
}
