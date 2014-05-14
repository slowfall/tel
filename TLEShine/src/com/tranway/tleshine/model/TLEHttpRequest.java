package com.tranway.tleshine.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

public class TLEHttpRequest {
	private static final String LOG_CAT = TLEHttpRequest.class.getSimpleName();
	private static final String SERVER_ADDRESS = "http://192.168.1.111";
	private static final int SERVER_PORT = 8080;

	public static final String JSON = "json";
	public static final String DATA = "data";
	public static final String STATE = "state";
	public static final String MSG = "msg";

	private static final int STATE_SUCCESS = 1;

	private String mainUrl;
	private OnHttpRequestListener mListener;
	private HttpClient httpClient;
	private FinalHttp finalHttp;
	private static TLEHttpRequest singleInstance;
	
	public static TLEHttpRequest instance() {
		if (singleInstance == null) {
			singleInstance = new TLEHttpRequest();
		}
		return singleInstance;
	}
	
	private TLEHttpRequest() {
		httpClient = new DefaultHttpClient();
		finalHttp = new FinalHttp();
		finalHttp.configCookieStore(new BasicCookieStore());
		mainUrl = SERVER_ADDRESS + ":" + SERVER_PORT + "/";
	}

	public interface OnHttpRequestListener {
		public void onFailure(String url, int errorNo, String errorMsg);

		public void onSuccess(String url, JSONObject data);
	}

	public void setOnHttpRequestListener(OnHttpRequestListener listener) {
		mListener = listener;
	}

	public void httpGet(String endUrl) {

	}

	public void httpPost(String endUrl, Map<String, String> data) {
		if (data == null || data.size() <= 0) {
			return;
		}

		new HttpThread(endUrl, data).start();
	}

	private class HttpThread extends Thread {
		private String myEndUrl;
		private Map<String, String> myData;

		public HttpThread(String endUrl, Map<String, String> data) {
			myEndUrl = endUrl;
			myData = data;
		}

		@Override
		public void run() {
			String url = mainUrl + myEndUrl;
			try {
				// String encodeUrl = URLEncoder.encode(url, HTTP.UTF_8);
				HttpPost httpPost = new HttpPost(url);
				Log.d(LOG_CAT, url);
				List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
				for (Entry<String, String> entry : myData.entrySet()) {
					BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
					pairs.add(pair);
				}
				httpPost.setEntity(new UrlEncodedFormEntity(pairs));
				HttpResponse response = httpClient.execute(httpPost);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					String result = EntityUtils.toString(entity);
					Log.d(LOG_CAT, result);

					JSONObject json = new JSONObject(result);
					JSONObject jsonRoot = json.getJSONObject(JSON);
					String data = jsonRoot.getString(DATA);
					JSONObject jsonData;
					if ("".equals(data)) {
						jsonData = new JSONObject();
					} else {
						jsonData = jsonRoot.getJSONObject(DATA);
					}
					int state = jsonRoot.getInt(STATE);
					String msg = jsonRoot.getString(MSG);
					if (mListener != null) {
						if (state != STATE_SUCCESS) {
							mListener.onFailure(myEndUrl, state, msg);
							return;
						} else {
							mListener.onSuccess(myEndUrl, jsonData);
						}
					}
				} else {
					if (mListener != null) {
						mListener.onFailure(myEndUrl, statusCode, response.getStatusLine().getReasonPhrase());
					}
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.run();
		}
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
				JSONObject jsonRoot = json.getJSONObject(JSON);
				int state = jsonRoot.getInt(STATE);
				String msg = jsonRoot.getString(MSG);
				if (mListener != null) {
					if (state != STATE_SUCCESS) {
						mListener.onFailure(myUrl, state, msg);
						return;
					} else {
						String jsonDataStr = jsonRoot.getString(DATA);
						JSONObject jsonData = null;
						if (!"".equals(jsonDataStr)) {
							jsonData = jsonRoot.getJSONObject(DATA);
						}
						mListener.onSuccess(myUrl, jsonData);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.onSuccess(t);
		}
	}
}
