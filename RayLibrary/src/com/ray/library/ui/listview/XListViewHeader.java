package com.ray.library.ui.listview;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ray.library.R;
import com.ray.library.util.UIUtil;

public class XListViewHeader extends LinearLayout {
	private LinearLayout mContainer;
	private ImageView mArrowImageView;
	private TextView mHintTextView;
	private int mState = STATE_NORMAL;

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;

	private Animation mRotateAnim;

	private float DESITY;
	// 荔枝list头部view的间距
	private int topExtraMargin = 0;

	// @f_off
	public static Integer[] loadingpics;

	// @f_on

	public XListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	public XListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		if (loadingpics == null) {
			List<Integer> list = UIUtil.getLoadingPicPath(context);
			loadingpics = list.toArray(new Integer[list.size()]);
		}

		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		DESITY = dm.density;

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.xlistview_header, null);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

		mArrowImageView = (ImageView) findViewById(R.id.xlistview_header_arrow);
		mHintTextView = (TextView) findViewById(R.id.xlistview_header_hint_textview);

		mRotateAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateAnim.setInterpolator(new LinearInterpolator());
		mRotateAnim.setDuration(750);
		mRotateAnim.setFillAfter(false);
		mRotateAnim.setRepeatCount(-1);

	}

	public void setState(int state) {
		if (state == mState)
			return;

		if (state == STATE_REFRESHING) {
			mArrowImageView
					.setImageResource(loadingpics[loadingpics.length - 1]);
			mArrowImageView.startAnimation(mRotateAnim);
		} else {
			mArrowImageView.clearAnimation();
		}

		switch (state) {
		case STATE_NORMAL:
			mHintTextView.setText(R.string.xlistview_header_hint_normal);
			break;
		case STATE_READY:
			if (mState != STATE_READY) {
				mHintTextView.setText(R.string.xlistview_header_hint_ready);
			}
			break;
		case STATE_REFRESHING:
			mHintTextView.setText(R.string.xlistview_header_hint_loading);
			break;
		default:
		}

		mState = state;
	}

	public void setTopExtraMargin(int topExtraMargin) {
		this.topExtraMargin = topExtraMargin;

	}

	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer
				.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
		if (mState == STATE_NORMAL || mState == STATE_READY) {
			if (height < 45 * DESITY + topExtraMargin) {
				mArrowImageView.setImageResource(loadingpics[0]);
				return;
			}
			int t = (int) (height - (45 * DESITY + topExtraMargin));
			int d = (int) (t / (loadingpics.length * 3));
			// Log.d("appplant", ">>>>>" + d);
			if (d >= loadingpics.length - 1) {
				d = loadingpics.length - 1;
			}
			mArrowImageView.setImageResource(loadingpics[d]);
		}
	}

	public int getVisiableHeight() {
		return mContainer.getHeight();
	}

}
