package com.wms.guessmusic.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.wms.guessmusic.R;

public class AllPassActivity extends Activity {
	private FrameLayout fl_coins;
	private ImageButton mBufBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.allpass);

		fl_coins = (FrameLayout) findViewById(R.id.fl_coins);
		fl_coins.setVisibility(View.GONE);
		mBufBack = (ImageButton) findViewById(R.id.but_back);
		mBufBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AllPassActivity.this,
						MainActivity.class);
				startActivity(intent);
			}
		});
	}
}
