package com.tranway.tleshine.model;

import java.util.Map;
import java.util.Map.Entry;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.impl.client.BasicCookieStore;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

public class TLEHttpRequest {
	private static final String LOG_CAT = TLEHttpRequest.class.getSimpleName();
	private static final String SERVER_ADDRESS = "http://113.105.115.30";
	private static final int SERVER_PORT = 5520;	

	public static final String STATUS = "status";
	public static final String STATUS_CODE = "statusCode";
	public static final String MSG = "msg";
	public static final int STATE_SUCCESS = 0;
	public static final int STATE_FAILED = 1;

	private String mainUrl;
	private OnHttpRequestListener mListener;
	private FinalHttp finalHttp;
	private static TLEHttpRequest singleInstance;

	public static TLEHttpRequest instance() {
		if (singleInstance == null) {
			singleInstance = new TLEHttpRequest();
		}
		return singleInstance;
	}

	private TLEHttpRequest() {
		finalHttp = new FinalHttp();
		finalHttp.configCookieStore(new BasicCookieStore());
		mainUrl = SERVER_ADDRESS + ":" + SERVER_PORT + "/api/use";
	}

	public interface OnHttpRequestListener {
		public void onFailure(String url, int errorNo, String errorMsg);

		public void onSuccess(String url, JSONObject data);
	}

	public void setOnHttpRequestListener(OnHttpRequestListener listener) {
		mListener = listener;
	}

	public void get(String endUrl, Map<String, String> data) {
		String url = mainUrl + endUrl;
		Uri uri = Uri.parse(url);
		Builder builder = uri.buildUpon();
		if (data != null) {
			for (Entry<String, String> entry : data.entrySet()) {
				builder.appendQueryParameter(entry.getKey(), entry.getValue());
			}
		}
		String buildUrl = builder.build().toString();
		Log.d(LOG_CAT, buildUrl);
		// String encodeUrl = URLEncoder.encode(url, HTTP.UTF_8);
		finalHttp.get(buildUrl, new MyAjaxCallBack(buildUrl));
	}

	public void post(String endUrl, Map<String, String> data) {
		AjaxParams params = new AjaxParams();
		for (Entry<String, String> entry : data.entrySet()) {
			params.put(entry.getKey(), entry.getValue());
		}
		String url = mainUrl + endUrl;
		// String encodedUrl = URLEncoder.encode(url, HTTP.UTF_8);
		Log.d(LOG_CAT, url);
		finalHttp.post(url, params, new MyAjaxCallBack(endUrl));
	}

	private class MyAjaxCallBack extends AjaxCallBack<String> {
		private String myUrl;

		public MyAjaxCallBack(String url) {
			myUrl = url;
		}

		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			Log.e(LOG_CAT, t.toString());
			if (mListener != null) {
				mListener.onFailure(myUrl, errorNo, strMsg);
			}
			super.onFailure(t, errorNo, strMsg);
		}

		@Override
		public void onSuccess(String t) {
			Log.d(LOG_CAT, t);
			try {
				JSONObject json = new JSONObject(t);
				mListener.onSuccess(myUrl, json);
			} catch (JSONException e) {
				mListener.onFailure(myUrl, STATE_FAILED, "failed");
				e.printStackTrace();
			}
			super.onSuccess(t);
		}
	}
}
