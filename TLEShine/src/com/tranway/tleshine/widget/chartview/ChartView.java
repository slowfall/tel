package com.tranway.tleshine.widget.chartview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tranway.tleshine.R;

public class ChartView extends RelativeLayout {

	// View

	private Paint mPaint = new Paint();

	// Series

	private List<AbstractSeries> mSeries = new ArrayList<AbstractSeries>();

	// Labels
	private LabelAdapter mBottomLabelAdapter;
	private LinearLayout mBottomLabelLayout;

	private int mLeftLabelWidth;
	private int mTopLabelHeight;
	private int mRightLabelWidth;
	private int mBottomLabelHeight;

	// Range
	private RectD mValueBounds = new RectD();
	private double mMinX = Double.MAX_VALUE;
	private double mMaxX = Double.MIN_VALUE;
	private double mMinY = Double.MAX_VALUE;
	private double mMaxY = Double.MIN_VALUE;

	// Grid
	private Rect mGridBounds = new Rect();
	private int mGridLineColor;
	private int mGridLineWidth;
	private int mGridLinesHorizontal;

	// ////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// ////////////////////////////////////////////////////////////////////////////////////

	public ChartView(Context context) {
		this(context, null, 0);
	}

	public ChartView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setWillNotDraw(false);
		setBackgroundColor(Color.TRANSPARENT);

		final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ChartView);

		mGridLineColor = attributes.getInt(R.styleable.ChartView_gridLineColor, Color.GRAY);
		mGridLineWidth = attributes.getDimensionPixelSize(R.styleable.ChartView_gridLineWidth, 1);
		mGridLinesHorizontal = attributes.getInt(R.styleable.ChartView_gridLinesHorizontal, 5);
		attributes.getInt(R.styleable.ChartView_gridLinesVertical, 5);
		mLeftLabelWidth = attributes.getDimensionPixelSize(R.styleable.ChartView_leftLabelWidth, 0);
		mTopLabelHeight = attributes.getDimensionPixelSize(R.styleable.ChartView_topLabelHeight, 0);
		mRightLabelWidth = attributes.getDimensionPixelSize(R.styleable.ChartView_rightLabelWidth, 0);

		float scale = context.getResources().getDisplayMetrics().scaledDensity;
		int size = (int) context.getResources().getDimension(R.dimen.chart_label_text_size);
		size = (size <= 14) ? size : 14;
		mBottomLabelHeight = (int) (size * scale + 5);
		Log.d(VIEW_LOG_TAG, "botton label height: " + mBottomLabelHeight);

		// bottom label layout
		LayoutParams bottomLabelParams = new LayoutParams(LayoutParams.MATCH_PARENT, mBottomLabelHeight);
		bottomLabelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mBottomLabelLayout = new LinearLayout(context);
		mBottomLabelLayout.setLayoutParams(bottomLabelParams);
		mBottomLabelLayout.setOrientation(LinearLayout.HORIZONTAL);

		// Add label views
		addView(mBottomLabelLayout);
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// ////////////////////////////////////////////////////////////////////////////////////

	public void clearSeries() {
		mSeries = new ArrayList<AbstractSeries>();
		resetRange();
		invalidate();
	}

	public void addSeries(AbstractSeries series) {
		if (mSeries == null) {
			mSeries = new ArrayList<AbstractSeries>();
		}

		extendRange(series.getMinX(), series.getMinY());
		extendRange(series.getMaxX(), series.getMaxY());

		mSeries.add(series);

		invalidate();
	}

	// Label adapters
	public void setBottomLabelAdapter(LabelAdapter adapter) {
		mBottomLabelAdapter = adapter;
	}

	// Grid properties
	public void setGridLineColor(int color) {
		mGridLineColor = color;
	}

	public void setGridLineWidth(int width) {
		mGridLineWidth = width;
	}

	public void setGridLinesHorizontal(int count) {
		mGridLinesHorizontal = count;
	}

	public void setGridLinesVertical(int count) {
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// OVERRIDEN METHODS
	// ////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		final int gridLeft = mLeftLabelWidth + mGridLineWidth - 1;
		final int gridTop = mTopLabelHeight + mGridLineWidth - 1;
		final int gridRight = getWidth() - mRightLabelWidth - mGridLineWidth;
		final int gridBottom = getHeight() - mBottomLabelHeight - mGridLineWidth;

		mGridBounds.set(gridLeft, gridTop, gridRight, gridBottom);

		LayoutParams bottomParams = (LayoutParams) mBottomLabelLayout.getLayoutParams();
		bottomParams.width = mGridBounds.width();
		mBottomLabelLayout.setLayoutParams(bottomParams);
		mBottomLabelLayout.layout(gridLeft, gridBottom, gridRight, getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Start drawing
		drawGrid(canvas);
		drawLabels(canvas);

		for (AbstractSeries series : mSeries) {
			series.draw(canvas, mGridBounds, mValueBounds);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// ////////////////////////////////////////////////////////////////////////////////////

	private void resetRange() {
		mMinX = Double.MAX_VALUE;
		mMaxX = Double.MIN_VALUE;
		mMinY = Double.MAX_VALUE;
		mMaxY = Double.MIN_VALUE;

		mValueBounds.set(mMinX, mMinY, mMaxX, mMaxY);
	}

	private void extendRange(double x, double y) {
		if (x < mMinX) {
			mMinX = x;
		}

		if (x > mMaxX) {
			mMaxX = x;
		}

		if (y < mMinY) {
			mMinY = y;
		}

		if (y > mMaxY) {
			mMaxY = y;
		}

		mValueBounds.set(mMinX, mMinY, mMaxX, mMaxY);
	}

	// Drawing methods

	private void drawGrid(Canvas canvas) {
		mPaint.setColor(mGridLineColor);
		mPaint.setStrokeWidth(mGridLineWidth);

		final float stepX = mGridBounds.width() / (float) (mGridLinesHorizontal + 1);
		// final float stepY = mGridBounds.height() / (float)
		// (mGridLinesVertical + 1);

		final float left = mGridBounds.left;
		final float top = mGridBounds.top;
		final float bottom = mGridBounds.bottom;
		// final float right = mGridBounds.right;

		for (int i = 0; i < mGridLinesHorizontal + 2; i++) {
			canvas.drawLine(left + (stepX * i), top, left + (stepX * i), bottom, mPaint);
		}

		// for (int i = 0; i < mGridLinesVertical + 2; i++) {
		// canvas.drawLine(left, top + (stepY * i), right, top + (stepY * i),
		// mPaint);
		// }
	}

	private void drawLabels(Canvas canvas) {
		if (mBottomLabelAdapter != null) {
			drawBottomLabels(canvas);
		}
	}

	// private void drawRightLabels(Canvas canvas) {
	// // Add views from adapter
	// final int labelCount = mRightLabelAdapter.getCount();
	// for (int i = 0; i < labelCount; i++) {
	// View view = mRightLabelLayout.getChildAt(i);
	//
	// if (view == null) {
	// LinearLayout.LayoutParams params = new
	// LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
	// if (i == 0 || i == labelCount - 1) {
	// params.weight = 0.5f;
	// } else {
	// params.weight = 1;
	// }
	//
	// view = mRightLabelAdapter.getView((labelCount - 1) - i, view,
	// mRightLabelLayout);
	// view.setLayoutParams(params);
	//
	// mRightLabelLayout.addView(view);
	// } else {
	// mRightLabelAdapter.getView((labelCount - 1) - i, view,
	// mRightLabelLayout);
	// }
	// }
	//
	// // Remove extra views
	// final int childCount = mRightLabelLayout.getChildCount();
	// for (int i = labelCount; i < childCount; i++) {
	// mRightLabelLayout.removeViewAt(i);
	// }
	// }

	private void drawBottomLabels(final Canvas canvas) {
		// Add views from adapter
		final int labelCount = mBottomLabelAdapter.getCount();
		for (int i = 0; i < labelCount; i++) {
			View view = mBottomLabelLayout.getChildAt(i);

			if (view == null) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
				if (i == 0 || i == labelCount - 1) {
					params.weight = 0.5f;
				} else {
					params.weight = 1;
				}

				view = mBottomLabelAdapter.getView(i, view, mBottomLabelLayout);
				view.setLayoutParams(params);

				mBottomLabelLayout.addView(view);
			} else {
				mBottomLabelAdapter.getView(i, view, mBottomLabelLayout);
			}
		}

		// Remove extra views
		final int childCount = mBottomLabelLayout.getChildCount();
		for (int i = labelCount; i < childCount; i++) {
			mBottomLabelLayout.removeViewAt(i);
		}
	}
}