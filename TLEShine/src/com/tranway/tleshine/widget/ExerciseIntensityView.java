package com.tranway.tleshine.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.tranway.tleshine.R;

public class ExerciseIntensityView extends GridView {
	private static final String TAG = ExerciseIntensityView.class.getSimpleName();

	private int position = 0;
	private Context context;
	private ArrayList<Integer> mList;
	private GridViewAdapter mAdapter;

	public ExerciseIntensityView(Context context) {
		super(context);
		this.context = context;
		initializationGridView();
	}

	public ExerciseIntensityView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setChildrenDrawingOrderEnabled(true);
		initializationGridView();
	}

	private void initializationGridView() {
		Log.d(TAG, "init gridview list");
		if (context == null) {
			return;
		}
		mList = new ArrayList<Integer>();
		mList.add(R.drawable.icon_achieve_goal_method);
		mList.add(R.drawable.icon_achieve_goal_method);
		mList.add(R.drawable.icon_achieve_goal_method);
		mAdapter = new GridViewAdapter(context, mList);
		this.setAdapter(mAdapter);
	}

	@Override
	protected void setChildrenDrawingOrderEnabled(boolean enabled) {
		super.setChildrenDrawingOrderEnabled(enabled);
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		if (i == childCount - 1) {
			return position;
		}
		if (i == position) {
			return childCount - 1;
		}
		return i;
	}

	public void setSelectPosition(int pos) {
		this.position = pos;
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged(position);
		}
	}

	public void setSelectPositon(int index) {
		if (mList == null || index < mList.size()) {
			return;
		}
		setSelection(index);
	}

	/**
	 * get current select item index
	 * 
	 * @return position
	 */
	public int getSelectItem() {
		return this.position;
	}

	/**
	 * get current select exercise intensity
	 * 
	 * @return select intensity or null
	 */
	public int getSelectExerciseIndex() {
		if (mList == null || mList.size() == 0 || position >= mList.size()) {
			return 0;
		}
		return this.position;
	}

	private class GridViewAdapter extends BaseAdapter {
		private Context context;
		private List<Integer> list = new ArrayList<Integer>();

		private int selected = 0;

		public GridViewAdapter(Context context, List<Integer> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void notifyDataSetChanged(int id) {
			selected = id;
			super.notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (context == null) {
				return null;
			}
			int alpha = 255;

			ImageView imgView = new ImageView(context);
			imgView.setAdjustViewBounds(false);
			imgView.setImageResource(list.get(position));
			// if (selected == position) {
			// alpha = 255;
			// Animation anim = AnimationUtils.loadAnimation(context,
			// R.anim.scale);
			// imgView.setLayoutParams(new GridView.LayoutParams(150, 200));
			// imgView.startAnimation(anim);
			// } else {
			// alpha = 80;
			// imgView.setLayoutParams(new GridView.LayoutParams(100, 150));
			// ` }

			// if(selected == position) {
			// // the special one.Scale Large
			// imgView.setScaleType(ScaleType.CENTER_CROP);
			// } else {
			// // the rest.Scale small
			// imgView.setScaleType(ScaleType.CENTER_INSIDE);
			// }

			if (selected == position) {
				alpha = 255;
				Animation testAnim = AnimationUtils.loadAnimation(context, R.anim.scale);
				imgView.startAnimation(testAnim);
			} else {
				alpha = 100;
				imgView.setScaleType(ScaleType.CENTER_INSIDE);
			}
			imgView.setImageAlpha(alpha);
			// imgView.setLayoutParams(new
			// GridView.LayoutParams(LayoutParams.WRAP_CONTENT,
			// LayoutParams.WRAP_CONTENT));
			// imgView.setScaleType(ScaleType.CENTER_INSIDE);

			return imgView;
		}

	}
}
