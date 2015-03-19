package com.wms.guessmusic.domain;

import android.widget.Button;

/**
 * 用于填字的按钮
 * 
 * @author love敏
 * 
 */
public class WordButton {
	// 按钮的索引
	private int mIndex;
	private String mWordText;
	private Button mButton;
	private boolean isVisible = true;

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public int getmIndex() {
		return mIndex;
	}

	public void setmIndex(int mIndex) {
		this.mIndex = mIndex;
	}

	public String getmWordText() {
		return mWordText;
	}

	public void setmWordText(String mWordText) {
		this.mWordText = mWordText;
	}

	public Button getmButton() {
		return mButton;
	}

	public void setmButton(Button mButton) {
		this.mButton = mButton;
	}

}
