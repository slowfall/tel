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
import com.tranway.tleshine.model.Exercise.Intensity;

public class ExerciseIntensityView extends GridView {
	private static final String TAG = ExerciseIntensityView.class.getSimpleName();

	private int position = 0;
	private Context context;
	private ArrayList<Intensity> mList;
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
		mList = new ArrayList<Intensity>();
		mList.add(Intensity.LIGHT);
		mList.add(Intensity.MODERATE);
		mList.add(Intensity.STRENUOUS);
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

	public void setSelectPositon(Intensity intensity) {
		if (mList == null || !mList.contains(intensity)) {
			return;
		}
		int index = mList.indexOf(intensity);
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
	public Intensity getSelectExerciseIntensity() {
		if (mList == null || mList.size() == 0 || position >= mList.size()) {
			return null;
		}
		return mList.get(position);
	}

	private class GridViewAdapter extends BaseAdapter {
		private Context context;
		private List<Intensity> list = new ArrayList<Intensity>();

		private int selected = 0;
		private int redId = R.drawable.exercise_bg;

		public GridViewAdapter(Context context, List<Intensity> list) {
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
			imgView.setImageResource(redId);
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
//			imgView.setScaleType(ScaleType.CENTER_INSIDE);

			return imgView;
		}

	}
}
