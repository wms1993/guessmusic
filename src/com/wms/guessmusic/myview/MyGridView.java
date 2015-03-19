package com.wms.guessmusic.myview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.wms.guessmusic.R;
import com.wms.guessmusic.domain.WordButton;
import com.wms.guessmusic.utils.Utils;

public class MyGridView extends GridView {
	private List<WordButton> mWordButtons = new ArrayList<WordButton>();

	private Context mContext;

	private MyAdapter mAdapter;

	private Animation mScaleAnimation;

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		mAdapter = new MyAdapter();
		setAdapter(mAdapter);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mWordButtons.size();
		}

		@Override
		public Object getItem(int position) {
			return mWordButtons.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final WordButton wordButton;
			if (convertView == null) {
				convertView = Utils.getLayoutInflater(mContext,
						R.layout.name_select_item);
				wordButton = mWordButtons.get(position);
				wordButton.setmButton((Button) convertView
						.findViewById(R.id.name_but));
				// wordButton.setmIndex(position);
				wordButton.getmButton().setOnClickListener(
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								wordButtonClickListener.click(wordButton);
							}
						});

				wordButton.setmIndex(position);
				wordButton.getmButton().setTextSize(24);
				wordButton.getmButton().setText(
						mWordButtons.get(position).getmWordText());
				convertView.setTag(wordButton);
				// 动画延时
				mScaleAnimation = AnimationUtils.loadAnimation(mContext,
						R.anim.scale);
				mScaleAnimation.setStartTime(position * 100);
				convertView.startAnimation(mScaleAnimation);
			} else {
				wordButton = (WordButton) convertView.getTag();
			}
			return convertView;
		}
	}

	public void setWordButtons(List<WordButton> wordButtons) {
		this.mWordButtons = wordButtons;
		this.setAdapter(mAdapter);
	}

	public interface OnWordButtonClick {
		void click(WordButton button);
	}

	private OnWordButtonClick wordButtonClickListener;

	public void setWordButtonClickListener(
			OnWordButtonClick wordButtonClickListener) {
		this.wordButtonClickListener = wordButtonClickListener;
	}
}
