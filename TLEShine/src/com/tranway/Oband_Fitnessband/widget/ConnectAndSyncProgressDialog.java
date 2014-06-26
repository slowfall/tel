package com.tranway.Oband_Fitnessband.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.tranway.Oband_Fitnessband.R;

public class ConnectAndSyncProgressDialog extends ProgressDialog {
	private Context context = null;
	private int msgId = R.string.is_loading;

	public ConnectAndSyncProgressDialog(Context context, int msgId) {
		super(context);
		this.context = context;
		this.msgId = msgId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_progress_dialog);
		setScreenBrightness();
		this.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				ImageView image = (ImageView) ConnectAndSyncProgressDialog.this.findViewById(R.id.loading_imgeview);
				Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_progress);
				image.startAnimation(animation);
				TextView tipsTxt = (TextView) ConnectAndSyncProgressDialog.this.findViewById(R.id.txt_tips);
				if (tipsTxt != null) {
					tipsTxt.setText(context.getResources().getString(msgId));
				}
			}
		});
//		this.setCancelable(false);
	}

	private void setScreenBrightness() {
		Window window = getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.5f;
		window.setAttributes(lp);
	}
}
