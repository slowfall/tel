package com.tranway.Oband_Fitnessband.widget.chartview;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tranway.Oband_Fitnessband.R;

public class BarAdapter extends BaseAdapter {
	private LayoutInflater layoutInflater;
	private List<BarData> dataList;
	private int itemWidth = 10;
	private int itemHeight = 256;

	public BarAdapter(Context context, int barWidth, int barHeight, int itemCount) {
		this.itemWidth = barWidth / itemCount;
		this.itemHeight = barHeight;
		layoutInflater = LayoutInflater.from(context);
	}

	public void setData(List<BarData> data) {
		this.dataList = data;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.item_barchart, null);
			holder.bottomTxt = (TextView) convertView.findViewById(R.id.text_bottom);
			holder.valuesView = (View) convertView.findViewById(R.id.view_values);
			holder.lineView = (View) convertView.findViewById(R.id.view_line);
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(itemWidth, itemHeight);
			Log.d("-------", "item width = " + itemWidth + " ; height = " + itemHeight);
			convertView.setLayoutParams(params);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String bottom = "";
		int remain = position % 12;
		int time = (position + 1) / 12;
		if ((position - 1) % 12 == 0) {
			bottom = "h";
		} else if (remain == 0) {
			bottom = String.valueOf(time % 10);
		} else if ((position + 1) % 12 == 0) {
			int decade = time / 10;
			if (decade != 0) {
				bottom = String.valueOf(decade);
			}
		}
		BarData data = dataList.get(position);
		holder.bottomTxt.setText(bottom);
		if (data.getPencent() > 0.85) {
			holder.valuesView.setBackgroundResource(R.color.blue);
		} else {
			holder.valuesView.setBackgroundResource(R.color.yellow);
		}
		setHeight(holder.valuesView, (int) (data.getPencent() * (itemHeight - 60))); // 60 = lineHeight + textHeight
		setWidth(holder.bottomTxt);
		setWidth(holder.lineView);
		return convertView;
	}

	public class ViewHolder {
		TextView bottomTxt;
		View valuesView;
		View lineView;
	}

	public void setHeight(View values, int heightvalues) {
		RelativeLayout.LayoutParams p = (android.widget.RelativeLayout.LayoutParams) values.getLayoutParams();
		p.height = heightvalues;
		p.width = itemWidth;
		values.setLayoutParams(p);
	}

	public void setWidth(View view) {
		RelativeLayout.LayoutParams p = (android.widget.RelativeLayout.LayoutParams) view.getLayoutParams();
		p.width = itemWidth;
		view.setLayoutParams(p);
	}

}
