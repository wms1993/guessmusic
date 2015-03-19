package com.wms.guessmusic.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class Utils {
	public static View getLayoutInflater(Context context, int id) {
		return LayoutInflater.from(context).inflate(id, null);
	}
}
