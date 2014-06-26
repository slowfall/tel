package com.tranway.Oband_Fitnessband;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class DocumentActivity extends Activity {
	public static final String DOCUMENT_TYPE = "documentType";
	public static final int ABOUT_FITNESS_BAND = 0;
	public static final int FAQ = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_document);
		TextView textView = (TextView) findViewById(R.id.tv_document);
		int type = getIntent().getIntExtra(DOCUMENT_TYPE, ABOUT_FITNESS_BAND);
		int titleTextID = R.string.about_fitness_band;
		String filename = "";
		switch (type) {
		case ABOUT_FITNESS_BAND:
			filename = "about_fitness_band.txt";
			titleTextID = R.string.about_fitness_band;
			break;
		case FAQ:
			titleTextID = R.string.faq;
			filename = "faq.txt";
		default:
			break;
		}

		initTitleView(titleTextID);
		try {
			InputStream inputStream = getAssets().open(filename);
			String text = readTextFile(inputStream);
			textView.setText(text);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initTitleView(int titleTextID) {
		ImageButton mPreBtn = (ImageButton) findViewById(R.id.btn_title_icon_left);
		mPreBtn.setVisibility(View.VISIBLE);
		mPreBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		TextView mTitleTxt = (TextView) findViewById(R.id.txt_title);
		mTitleTxt.setText(titleTextID);
	}

	private String readTextFile(InputStream inputStream) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
		}
		return outputStream.toString();
	}
}
