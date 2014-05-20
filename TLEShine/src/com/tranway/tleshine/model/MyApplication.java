package com.tranway.tleshine.model;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
	private static Context context = null;

	@Override
	public void onCreate() {
		super.onCreate();
		MyApplication.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return MyApplication.context;
	}
}
