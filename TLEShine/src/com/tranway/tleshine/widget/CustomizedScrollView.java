package com.tranway.tleshine.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class CustomizedScrollView extends ScrollView {

	@SuppressWarnings("deprecation")
	public CustomizedScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 添加了一个手势选择器
		gestureDetector = new GestureDetector(new Yscroll());
		setFadingEdgeLength(0);
	}

	GestureDetector gestureDetector;
	View.OnTouchListener onTouchListener;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// return super.onInterceptTouchEvent(ev);
		// return super.onInterceptTouchEvent(ev) &&
		// gestureDetector.onTouchEvent(ev);
		return gestureDetector.onTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// Log.d("touchevent", "touchevent"+super.onTouchEvent(ev));
		// return super.onTouchEvent(ev);
		return false;
	}

	class Yscroll extends SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			// 控制手指滑动的距离
			if (Math.abs(distanceY) >= Math.abs(distanceX)) {
				return true;
			}
			return false;
		}

	}
}
