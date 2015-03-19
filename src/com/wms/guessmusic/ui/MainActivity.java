package com.wms.guessmusic.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wms.guessmusic.R;
import com.wms.guessmusic.data.Const;
import com.wms.guessmusic.domain.Song;
import com.wms.guessmusic.domain.WordButton;
import com.wms.guessmusic.myview.MyGridView;
import com.wms.guessmusic.myview.MyGridView.OnWordButtonClick;
import com.wms.guessmusic.utils.MyMediaPlayer;
import com.wms.guessmusic.utils.Utils;

public class MainActivity extends Activity implements OnWordButtonClick {

	private static final int WORD_COUNT = 24;
	// 回答正确的状态值
	private static final int ANSWER_SUCCESS = 0;
	// 回答错误的状态值
	private static final int ANSWER_FAIL = 1;
	// 没有填写完成的状态值
	private static final int ANSWER_LACK = 2;
	private ImageView mViewDisc;
	private ImageView mViewPin;
	private Animation mDiscAnimation, mPinAnimation, mPinFanAnimation;
	private MyGridView mGridView;
	private List<WordButton> mWordButtons = new ArrayList<WordButton>();
	private LinearLayout mWordSelect;
	// 已经选择的选择框
	private List<WordButton> mWordSelectButtons = new ArrayList<WordButton>();
	private Song mCurrentSong;
	private int mStageIndex = -1;
	private ImageButton delete_word;
	private ImageButton tip_answer;
	private int mCurrentStage = 1;

	// 当前金币数量
	private int mCurrentCoins = Const.TOTAL_COINS;
	private TextView tv_coins;
	private LinearLayout pass_view;
	private ImageButton next_question;
	private TextView current_stage;
	private TextView current_song;
	private ImageView mImageView;
	private ImageButton but_back;
	private TextView tv_current_stage;
	private SharedPreferences mPreferences;
	private ImageButton share;
	private long exitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		mPreferences = getSharedPreferences("config", MODE_PRIVATE);
		mCurrentCoins = mPreferences.getInt("coins", 1000);
		if (mCurrentCoins < 0) {
			mCurrentCoins = 0;
		}

		initView();
		initData();

		mGridView.setWordButtonClickListener(this);

		mDiscAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mPinAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		mPinFanAnimation = AnimationUtils.loadAnimation(this,
				R.anim.rotate_45_fan);
		mDiscAnimation.setFillAfter(true);
		mPinAnimation.setFillAfter(true);
		LinearInterpolator linearInterpolator = new LinearInterpolator();
		mDiscAnimation.setInterpolator(linearInterpolator);
		mPinAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mViewDisc.setAnimation(mDiscAnimation);
			}
		});

		mDiscAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mViewPin.setAnimation(mPinFanAnimation);
				mImageView.setVisibility(View.VISIBLE);
			}
		});
	}

	private void initView() {
		mViewDisc = (ImageView) findViewById(R.id.iv_disc);
		mViewPin = (ImageView) findViewById(R.id.iv_pin);
		mGridView = (MyGridView) findViewById(R.id.gridview);
		mWordSelect = (LinearLayout) findViewById(R.id.word_select_container);
		delete_word = (ImageButton) findViewById(R.id.delete_word);
		tip_answer = (ImageButton) findViewById(R.id.tip_answer);
		tv_coins = (TextView) findViewById(R.id.tv_coins);
		mImageView = (ImageView) findViewById(R.id.play_but);
		but_back = (ImageButton) findViewById(R.id.but_back);
		tv_current_stage = (TextView) findViewById(R.id.tv_current_stage);
		share = (ImageButton) findViewById(R.id.share);

		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				share();
			}

		});

		but_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("---->mCurrentStage" + mCurrentStage);
				if (mCurrentStage > 1) {
					mCurrentStage = mCurrentStage - 1;
					tv_current_stage.setText("" + mCurrentStage);
					MyMediaPlayer.stopSong();
					mViewDisc.clearAnimation();
					mViewPin.clearAnimation();
					mImageView.setVisibility(View.VISIBLE);
					initData();
				}
			}
		});

		tv_coins.setText(mCurrentCoins + "");

		delete_word.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				View dialogView = Utils.getLayoutInflater(MainActivity.this,
						R.layout.dialog);
				builder.setView(dialogView);
				final AlertDialog dialog = builder.create();
				dialog.show();
				TextView tv_message = (TextView) dialogView
						.findViewById(R.id.message);
				ImageButton buy_ok = (ImageButton) dialogView
						.findViewById(R.id.buy_ok);
				ImageButton buy_cancle = (ImageButton) dialogView
						.findViewById(R.id.buy_cancle);

				tv_message.setText("你确定要花掉30个金币去掉一个错误答案？");

				buy_ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (dialog != null) {
							dialog.dismiss();
						}
						doDeleteWord();
					}
				});

				buy_cancle.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (dialog != null) {
							dialog.dismiss();
						}
					}
				});

			}

		});

		tip_answer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				View dialogView = Utils.getLayoutInflater(MainActivity.this,
						R.layout.dialog);
				builder.setView(dialogView);
				final AlertDialog dialog = builder.create();
				dialog.show();
				TextView tv_message = (TextView) dialogView
						.findViewById(R.id.message);
				ImageButton buy_ok = (ImageButton) dialogView
						.findViewById(R.id.buy_ok);
				ImageButton buy_cancle = (ImageButton) dialogView
						.findViewById(R.id.buy_cancle);

				tv_message.setText("你确定要花掉90个金币得到一个答案提示么？");

				buy_ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (dialog != null) {
							dialog.dismiss();
						}
						doTipAnswer();
					}
				});

				buy_cancle.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (dialog != null) {
							dialog.dismiss();
						}
					}
				});
			}
		});
	}

	/**
	 * 获得配置文件中的删除一个文字所需的金币
	 * 
	 * @return
	 */
	private int getDeleteCoins() {
		return getResources().getInteger(R.integer.pay_delete_word);
	}

	/**
	 * 获得配置文件中的提示一个文字所需的金币
	 * 
	 * @return
	 */
	private int getTipAnswerCoins() {
		return getResources().getInteger(R.integer.pay_tip_answer);
	}

	/**
	 * 处理提示答案
	 */
	protected void doTipAnswer() {
		if (checkCoins(getTipAnswerCoins())) {
			TipAnswer();
		} else {
			Toast.makeText(MainActivity.this, "金币不足，不能购买", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * 处理提示文字
	 */
	private void TipAnswer() {
		Button button = null;
		for (int i = 0; i < mWordSelectButtons.size(); i++) {
			button = mWordSelectButtons.get(i).getmButton();
			if (TextUtils.isEmpty(button.getText())) {
				click(findCorrectAnswer(i));
				break;
			}
		}
	}

	/**
	 * 查找一个正确答案
	 * 
	 * @return
	 */
	private WordButton findCorrectAnswer(int index) {
		WordButton buf = null;

		for (int i = 0; i < WORD_COUNT; i++) {
			buf = mWordButtons.get(i);

			if (buf.getmButton().getText()
					.equals("" + mCurrentSong.getSongCharater()[index])) {
				return buf;
			}
		}

		return null;
	}

	/**
	 * 处理删除选择文字
	 */
	private void doDeleteWord() {
		System.out.println("---->" + mCurrentCoins);
		if (checkCoins(getDeleteCoins())) {
			deleteOneWord();
		} else {
			Toast.makeText(MainActivity.this, "金币不足，不能购买", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * 删除一个文字
	 */
	private void deleteOneWord() {
		// 不能删除正确答案，也不能删除隐藏的文字
		Random random = new Random();
		WordButton button = null;
		while (true) {
			button = mWordButtons.get(random.nextInt(WORD_COUNT));
			System.out.println("visible===" + button.isVisible());
			System.out.println("----" + button.getmButton().getText());
			if (button.isVisible()
					&& !mCurrentSong.getSongName().contains(
							button.getmButton().getText())) {
				button.getmButton().setVisibility(View.GONE);
				button.setVisible(false);
				return;
			}
		}
	}

	/**
	 * 检查金币是否足够
	 * 
	 * @param data
	 * @return true 足够 false 不足
	 */
	public boolean checkCoins(int data) {
		if (mCurrentCoins - data >= 0) {
			mCurrentCoins = mCurrentCoins - data;
			tv_coins.setText(mCurrentCoins + "");
			return true;
		} else {
			if (mCurrentCoins < 0) {
				mCurrentCoins = 0;
				tv_coins.setText(mCurrentCoins + "");
			}
			return false;
		}
	}

	private void initData() {
		mImageView.setVisibility(View.VISIBLE);
		// 设置当前的关数
		mCurrentSong = new Song();
		mStageIndex = mCurrentStage - 1;
		String[] data = Const.SONG_INFO[mStageIndex];
		mCurrentSong.setSongName(data[Const.INDEX_SONG_NAME]);
		mCurrentSong.setSongFilePath(data[Const.INDEX_FILE_NAME]);

		// 清空
		mWordButtons.clear();
		String[] words = generateWords();
		for (int i = 0; i < 24; i++) {
			WordButton button = new WordButton();
			button.setmWordText(words[i]);
			mWordButtons.add(button);
		}

		mGridView.setWordButtons(mWordButtons);
		// 添加选择按钮
		addSelectButton();
	}

	private void addSelectButton() {
		// 移除所有子元素
		mWordSelect.removeAllViews();
		mWordSelectButtons.clear();
		// 初始化数据
		for (int i = 0; i < mCurrentSong.getSongLength(); i++) {
			View view = Utils
					.getLayoutInflater(this, R.layout.name_select_item);
			final WordButton wordButton = new WordButton();
			wordButton.setmButton((Button) view.findViewById(R.id.name_but));
			wordButton.getmButton().setTextSize(24);
			wordButton.getmButton().setTextColor(Color.WHITE);
			wordButton.getmButton().setBackgroundResource(
					R.drawable.game_wordblank);
			wordButton.getmButton().setOnClickListener(
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							wordButton.getmButton().setText("");
							mWordButtons.get(wordButton.getmIndex())
									.getmButton().setVisibility(View.VISIBLE);
						}
					});
			mWordSelectButtons.add(wordButton);
		}
		// 添加选择框
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < mCurrentSong.getSongLength(); i++) {
			mWordSelect.addView(mWordSelectButtons.get(i).getmButton(), params);
		}
	}

	public void start(View view) {
		mImageView.setVisibility(View.GONE);
		mViewPin.startAnimation(mPinAnimation);

		// 播放音乐
		MyMediaPlayer.playSong(MainActivity.this,
				mCurrentSong.getSongFilePath());
	}

	@Override
	public void click(WordButton button) {
		// 将文字选择可见性置为false
		button.setVisible(false);
		setSelectWords(button);

		// 检查答案是否正确
		int status = checkAnswer();

		if (status == ANSWER_SUCCESS) {
			handleSuccess();
		} else if (status == ANSWER_FAIL) {
			// 回答错误
		} else if (status == ANSWER_LACK) {
			// 没有回答完
		}
	}

	/**
	 * 处理回答正确
	 */
	private void handleSuccess() {
		pass_view = (LinearLayout) findViewById(R.id.pass_view);
		pass_view.setVisibility(View.VISIBLE);
		next_question = (ImageButton) findViewById(R.id.next_question);
		current_stage = (TextView) findViewById(R.id.current_stage);
		current_stage.setText(mCurrentStage + "");

		current_song = (TextView) findViewById(R.id.current_song);
		current_song.setText(mCurrentSong.getSongName());

		// 答题正确，停止正在播放的音乐
		MyMediaPlayer.stopSong();
		// 停止动画
		mViewDisc.clearAnimation();
		mViewPin.clearAnimation();
		mImageView.setVisibility(View.VISIBLE);
		// 播放过关得到金币的音乐
		MyMediaPlayer.playSong(MainActivity.this, "coin.mp3");
		// 在原有的金币的基础上加上50个金币
		mCurrentCoins = mCurrentCoins + 50;
		tv_coins.setText(mCurrentCoins + "");
		next_question.setOnClickListener(new View.OnClickListener() {

			private TextView tv_current_stage;

			@Override
			public void onClick(View view) {
				if (!isLastSong()) {
					tv_current_stage = (TextView) findViewById(R.id.tv_current_stage);
					tv_current_stage.setText((++mCurrentStage) + "");
					pass_view.setVisibility(View.GONE);
					initData();
				} else {
					Intent intent = new Intent(MainActivity.this,
							AllPassActivity.class);
					finish();
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * 判断是否是最后一首歌
	 * 
	 * @return
	 */
	private boolean isLastSong() {
		return mCurrentStage == Const.SONG_INFO.length ? true : false;
	}

	private void setSelectWords(WordButton button) {
		Button selectButton = null;
		for (int i = 0; i < mCurrentSong.getSongLength(); i++) {
			selectButton = mWordSelectButtons.get(i).getmButton();
			if (TextUtils.isEmpty(selectButton.getText())) {
				selectButton.setText(button.getmWordText());
				// 隐藏文字选择区的选中文字
				button.getmButton().setVisibility(View.GONE);
				mWordSelectButtons.get(i).setmIndex(button.getmIndex());
				break;
			}
		}
	}

	/**
	 * 检查用户输入歌曲是否正确
	 */
	private int checkAnswer() {
		// 检查是否填写完成
		for (int j = 0; j < mWordSelectButtons.size(); j++) {
			// 如果有空的，说明答案还不完整
			if (TextUtils.isEmpty(mWordSelectButtons.get(j).getmButton()
					.getText())) {
				return ANSWER_LACK;
			}
		}

		StringBuilder answer = new StringBuilder();
		for (int i = 0; i < mWordSelectButtons.size(); i++) {
			answer.append(mWordSelectButtons.get(i).getmButton().getText());
		}

		if (mCurrentSong.getSongName().equals(answer + "")) {
			return ANSWER_SUCCESS;
		} else {
			// 闪烁文字
			sparkWords();
			return ANSWER_FAIL;
		}

	}

	/**
	 * 闪烁文字
	 */
	private void sparkWords() {
		TimerTask task = new TimerTask() {
			private boolean mColorChanged;
			private int mSparkCount;

			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (mSparkCount >= 5) {
							for (int i = 0; i < mWordSelectButtons.size(); i++) {
								mWordSelectButtons.get(i).getmButton()
										.setTextColor(Color.RED);
							}
							return;
						}
						// 交替的改变文字的颜色
						for (int i = 0; i < mWordSelectButtons.size(); i++) {
							mWordSelectButtons
									.get(i)
									.getmButton()
									.setTextColor(
											mColorChanged ? Color.RED
													: Color.WHITE);
						}
						mSparkCount++;
						mColorChanged = !mColorChanged;
					}
				});
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, 1, 150);
	}

	/**
	 * 生成所有待选文字
	 * 
	 * @return
	 */
	private String[] generateWords() {
		// // 初始化游戏的数据
		// initData();

		String[] words = new String[WORD_COUNT];
		// 将歌曲名称存入待选文字
		for (int i = 0; i < mCurrentSong.getSongLength(); i++) {
			words[i] = mCurrentSong.getSongCharater()[i] + "";
		}

		// 存入随机汉字
		for (int i = mCurrentSong.getSongLength(); i < WORD_COUNT; i++) {
			words[i] = getRandomChar() + "";
		}

		// 打乱文字顺序：首先从所有元素中随机选取一个与第一个元素进行交换，
		// 然后在第二个之后选择一个元素与第二个交换，知道最后一个元素。
		// 这样能够确保每个元素在每个位置的概率都是1/n。
		Random random = new Random();
		for (int i = WORD_COUNT - 1; i >= 0; i--) {
			int index = random.nextInt(i + 1);

			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;
		}
		return words;
	}

	/**
	 * 生成随机汉字 http://www.cnblogs.com/skyivben/archive/2012/10/20/2732484.html
	 * 
	 * @return
	 */
	private char getRandomChar() {
		String str = "";
		int hightPos;
		int lowPos;

		Random random = new Random();

		hightPos = (176 + Math.abs(random.nextInt(39)));
		lowPos = (161 + Math.abs(random.nextInt(93)));

		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(hightPos)).byteValue();
		b[1] = (Integer.valueOf(lowPos)).byteValue();

		try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return str.charAt(0);
	}

	@Override
	protected void onStop() {
		MyMediaPlayer.stopSong();
		mViewDisc.clearAnimation();
		mViewPin.clearAnimation();
		mImageView.setVisibility(View.VISIBLE);
		mPreferences
				.edit()
				.putInt("coins",
						Integer.parseInt(tv_coins.getText().toString()))
				.commit();
		super.onStop();
	}

	private void share() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, getTitle()));
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
