package com.wms.guessmusic.utils;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * 音乐控制类
 * 
 * @author love敏
 * 
 */
public class MyMediaPlayer {

	private static MediaPlayer mMediaPlayer;

	/**
	 * 音乐播放
	 * 
	 * @param context
	 * @param songName
	 */
	public static void playSong(Context context, String songName) {
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
		}

		// 充值mMediaPlayer
		mMediaPlayer.reset();

		// 获取要播放的资源
		AssetManager assetManager = context.getAssets();
		try {
			AssetFileDescriptor fd = assetManager.openFd(songName);
			mMediaPlayer.setDataSource(fd.getFileDescriptor(),
					fd.getStartOffset(), fd.getLength());
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 停止音乐
	 */
	public static void stopSong (){
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}
	} 
}
