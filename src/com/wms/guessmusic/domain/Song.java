package com.wms.guessmusic.domain;

public class Song {
	private String songName;
	private int songLength;
	private String songFilePath;
	private char[] songCharater;

	public char[] getSongCharater() {
		// 将歌曲名称转换为字符数组
		return songName.toCharArray();
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public int getSongLength() {
		return songName.length();
	}

	public String getSongFilePath() {
		return songFilePath;
	}

	public void setSongFilePath(String songFilePath) {
		this.songFilePath = songFilePath;
	}

}
