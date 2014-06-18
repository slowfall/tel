package com.tranway.tleshine.widget;

import com.tranway.tleshine.model.ActivityListAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

/**
 * 取代ListView的LinearLayout，使之能够成功嵌套在ScrollView中
 * 
 * @author terry_龙
 */
public class CustomizedListView extends LinearLayout {

	// private BaseAdapter adapter;

	private ActivityListAdapter mAdapter;
	private OnClickListener onClickListener = null;

	/**
	 * 绑定布局
	 */
	public void bindLinearLayout() {
		int count = mAdapter.getCount();
		this.removeAllViews();
		for (int i = 0; i < count; i++) {
			View v = mAdapter.getView(i, null, null);

			v.setOnClickListener(this.onClickListener);
			addView(v, i);
		}
		Log.v("countTAG", "" + count);
	}

	public CustomizedListView(Context context) {
		super(context);

	}

	public CustomizedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

	}

	/**
	 * 获取Adapter
	 * 
	 * @return adapter
	 */
	public BaseAdapter getAdpater() {
		return mAdapter;
	}

	/**
	 * 设置数据
	 * 
	 * @param adpater
	 */
	public void setAdapter(ActivityListAdapter mAdapter) {
		this.mAdapter = mAdapter;
		bindLinearLayout();
	}

	/**
	 * 获取点击事件
	 * 
	 * @return
	 */
	public OnClickListener getOnclickListner() {
		return onClickListener;
	}

	/**
	 * 设置点击事件
	 * 
	 * @param onClickListener
	 */
	public void setOnclickLinstener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

}