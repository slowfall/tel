package com.tranway.tleshine.widget.chartview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tranway.tleshine.R;

public class LabelAdapter extends BaseAdapter {
	public enum LabelOrientation {
		HORIZONTAL, VERTICAL
	}

	private Context mContext;
	private LabelOrientation mOrientation;
	private String[] values;

	private int textSize = 0;

	public LabelAdapter(Context context, LabelOrientation orientation) {
		mContext = context;
		mOrientation = orientation;
		textSize = (int) mContext.getResources().getDimension(R.dimen.chart_label_text_size);
		textSize = textSize <= 14 ? textSize : 14;
	}

	public void setLabelValues(String[] values) {
		this.values = values;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return values.length;
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return values[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView labelTextView;
		if (convertView == null) {
			convertView = new TextView(mContext);
		}

		labelTextView = (TextView) convertView;

		int gravity = Gravity.CENTER;
		if (mOrientation == LabelOrientation.VERTICAL) {
			if (position == 0) {
				gravity = Gravity.BOTTOM | Gravity.RIGHT;
			} else if (position == getCount() - 1) {
				gravity = Gravity.TOP | Gravity.RIGHT;
			} else {
				gravity = Gravity.CENTER | Gravity.RIGHT;
			}
		} else if (mOrientation == LabelOrientation.HORIZONTAL) {
			if (position == 0) {
				gravity = Gravity.CENTER | Gravity.LEFT;
			} else if (position == getCount() - 1) {
				gravity = Gravity.CENTER | Gravity.RIGHT;
			}
		}

		labelTextView.setGravity(gravity);
		if (values.length >= position) {
			labelTextView.setText(getItem(position));
			labelTextView.setTextSize(textSize);
		}
		return convertView;
	}
}
