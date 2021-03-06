package com.tranway.Oband_Fitnessband.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.tranway.Oband_Fitnessband.R;

@SuppressLint("DrawAllocation")
public class MultiRoundProgressBar extends View {
	/**
	 * 画笔对象的引用
	 */
	private Paint paint;

	/**
	 * 圆环的颜色
	 */
	private int roundColor;

	/**
	 * 圆环进度的颜色
	 */
	private int roundProgressColor;
	private int roundProgressNextColor;

	/**
	 * 中间进度百分比的字符串的颜色
	 */
	private int textCurrentColor;
	private int textTotalColor;

	/**
	 * 中间进度百分比的字符串的字体
	 */
	private float textCurrentSize;
	private float textTotalSize;

	/**
	 * 圆环的宽度
	 */
	private float roundWidth;

	/**
	 * 最大进度
	 */
	private int max;

	/**
	 * 当前进度
	 */
	private int progress;
	private int progressNext;

	/**
	 * 开始的角度
	 */
	private int startAngle;
	private int startAngleNext;
	/**
	 * 是否显示中间的进度
	 */
	private boolean textIsDisplayable = false;

	/**
	 * 进度的风格，实心或者空心
	 */
	private int style;

	/**
	 * 当前进度文本
	 */
	private String textCurrent;

	/**
	 * 总大小文本
	 */
	private String textTotal;

	public static final int STROKE = 0;
	public static final int FILL = 1;

	public MultiRoundProgressBar(Context context) {
		this(context, null);
	}

	public MultiRoundProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MultiRoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		paint = new Paint();

		TypedArray mTypedArray = context
				.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);

		// 获取自定义属性和默认值
		roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
		roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor,
				Color.GREEN);
		roundProgressNextColor = mTypedArray.getColor(
				R.styleable.RoundProgressBar_roundProgressNextColor, Color.GREEN);
		startAngle = mTypedArray.getInteger(R.styleable.RoundProgressBar_roundProgressStartAngle,
				-90);
		startAngleNext = mTypedArray.getInteger(
				R.styleable.RoundProgressBar_roundProgressNextStartAngle, -90);
		textCurrentColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textCurrentColor,
				Color.GREEN);
		textTotalColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textTotalColor,
				Color.GRAY);
		textCurrentSize = mTypedArray
				.getDimension(R.styleable.RoundProgressBar_textCurrentSize, 15);
		textTotalSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textTotalSize, 15);
		roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
		max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
		textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable,
				false);
		style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);

		mTypedArray.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		/**
		 * 画最外层的大圆环
		 */
		int center = getWidth() / 2; // 获取圆心的x坐标
		int radius = (int) (center - roundWidth / 2); // 圆环的半径
		paint.setColor(roundColor); // 设置圆环的颜色
		paint.setStyle(Paint.Style.STROKE); // 设置空心
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setAntiAlias(true); // 消除锯齿
		canvas.drawCircle(center, center, radius, paint); // 画出圆环

		/**
		 * 画圆弧 ，画圆环的进度
		 */

		// 设置进度是实心还是空心
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setColor(roundProgressColor); // 设置进度的颜色
		RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius); // 用于定义的圆弧的形状和大小的界限

		switch (style) {
		case STROKE:
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawArc(oval, startAngle, max == 0 ? 0 : 360 * progress / max, false, paint); // 根据进度画圆弧
			paint.setColor(roundProgressNextColor);
			canvas.drawArc(oval, startAngleNext, max == 0 ? 0 : 360 * progressNext / max, false,
					paint); // 画第二个进度
			break;
		case FILL:
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			if (progress != 0) {
				canvas.drawArc(oval, startAngle, max == 0 ? 0 : 360 * progress / max, true, paint); // 根据进度画圆弧
			}
			if (progressNext != 0) {
				paint.setColor(roundProgressNextColor);
				canvas.drawArc(oval, startAngleNext, max == 0 ? 0 : 360 * progressNext / max, true,
						paint); // 画第二个进度
			}
			break;
		}

		/**
		 * 画进度百分比
		 */
		if (textIsDisplayable && style == STROKE) {
			int yOffset = 10;
			paint.setStrokeWidth(0);

			paint.setTextSize(textCurrentSize);
			float curWidth = paint.measureText(textCurrent);
			paint.setTextSize(textTotalSize);
			float totalWidth = paint.measureText(textTotal);

			paint.setTextSize(textCurrentSize);
			paint.setColor(textCurrentColor);
			canvas.drawText(textCurrent, center - curWidth / 2, center + yOffset, paint);
			paint.setColor(textTotalColor);
			paint.setTextSize(textTotalSize);
			canvas.drawText(textTotal, center - totalWidth / 2, center + textTotalSize + yOffset
					* 2, paint);
		}
	}

	public synchronized int getMax() {
		return max;
	}

	/**
	 * 设置进度的最大值
	 * 
	 * @param max
	 */
	public synchronized void setMax(int max) {
		if (max < 0) {
			throw new IllegalArgumentException("max not less than 0");
		}
		this.max = max;
	}

	/**
	 * 获取进度.需要同步
	 * 
	 * @return
	 */
	public synchronized int getProgress() {
		return progress;
	}

	/**
	 * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
	 * 
	 * @param progress
	 */
	public synchronized void setProgress(int progress) {
		if (progress < 0) {
			throw new IllegalArgumentException("progress not less than 0");
		}
		if (progress > max) {
			progress = max;
		}
		if (progress <= max) {
			this.progress = progress;
			postInvalidate();
		}
	}

	public synchronized void setProgressNext(int progressNext) {
		if (progressNext < 0) {
			throw new IllegalArgumentException("progress not less than 0");
		}
		if (progressNext > max) {
			progressNext = max;
		}
		if (progressNext <= max) {
			this.progressNext = progressNext;
			postInvalidate();
		}
	}

	public int getCricleColor() {
		return roundColor;
	}

	public void setCricleColor(int cricleColor) {
		this.roundColor = cricleColor;
	}

	public int getCricleProgressColor() {
		return roundProgressColor;
	}

	public void setCricleProgressColor(int cricleProgressColor) {
		this.roundProgressColor = cricleProgressColor;
	}

	public int getTextColor() {
		return textCurrentColor;
	}

	public void setTextColor(int textColor) {
		this.textCurrentColor = textColor;
	}

	public float getTextSize() {
		return textCurrentSize;
	}

	public void setTextSize(float textSize) {
		this.textCurrentSize = textSize;
	}

	public float getRoundWidth() {
		return roundWidth;
	}

	public void setRoundWidth(float roundWidth) {
		this.roundWidth = roundWidth;
	}

}
