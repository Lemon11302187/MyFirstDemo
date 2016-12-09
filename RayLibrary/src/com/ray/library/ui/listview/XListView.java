package com.ray.library.ui.listview;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.ray.library.R;

public class XListView extends ListView implements OnScrollListener {

	private Context mContext;

	public float mLastY = -1;
	public Scroller mScroller;
	public OnScrollListener mScrollListener;

	public IXListViewListener mListViewListener;

	public XListViewHeader mHeaderView;

	public RelativeLayout mExtraHeaderView;
	public RelativeLayout mHeaderViewContent;
	public TextView mHeaderTimeView;
	public TextView topSpace;
	public int mHeaderViewHeight;
	public boolean mEnablePullRefresh = true;
	public boolean mPullRefreshing = false;

	public XListViewFooter mFooterView;
	public RelativeLayout mFooterViewContent;
	public boolean mEnablePullLoad;
	public boolean mPullLoading;
	public boolean mIsFooterReady = false;

	private boolean isShow = false;
	private boolean isTouched = false;

	public int mTotalItemCount;

	public int mScrollBack;
	public final static int SCROLLBACK_HEADER = 0;
	public final static int SCROLLBACK_FOOTER = 1;

	public final static int PULL_LOAD_MORE_DELTA = 50;

	public final static float OFFSET_RADIO = 1.8f;

	public String refresh_time;

	private int topMargin;
	private int bottomMargin;
	private int topExtraMargin = 0;

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@SuppressLint("NewApi")
	public void init(int topMargin, int bottomMargin) {
		this.topMargin = topMargin;
		this.bottomMargin = bottomMargin;

		mScroller = new Scroller(mContext, new DecelerateInterpolator());
		super.setOnScrollListener(this);

		topSpace = new TextView(mContext);
		topSpace.setHeight(topMargin);
		addHeaderView(topSpace);

		//
		mHeaderView = new XListViewHeader(mContext);
		mExtraHeaderView = (RelativeLayout) mHeaderView
				.findViewById(R.id.xlistview_header_extra);
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.xlistview_header_content);
		mHeaderTimeView = (TextView) mHeaderView
				.findViewById(R.id.xlistview_header_time);
		addHeaderView(mHeaderView);

		//
		mFooterView = new XListViewFooter(mContext);
		mFooterViewContent = (RelativeLayout) mFooterView
				.findViewById(R.id.xlistview_footer_content);

		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
		setOverScrollMode(OVER_SCROLL_NEVER);
	}

	/**
	 * 另外增加头部空余空间高度
	 * 
	 * @param addHight
	 */
	public void addTopMargin(int addHight) {
		topSpace.setHeight(topMargin + addHight);
	}

	/**
	 * 在刷新view下面再在加一行view 类似澎湃网的头部
	 * 
	 * @param view
	 * @param topExtraMargin
	 */
	@SuppressLint("InlinedApi")
	public void setExtraHeaderView(View view, int topExtraMargin) {
		this.topExtraMargin = topExtraMargin;
		mHeaderView.setTopExtraMargin(topExtraMargin);
		mExtraHeaderView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, topExtraMargin));
		mExtraHeaderView.addView(view);
		mHeaderView.setVisiableHeight(topExtraMargin);
		mHeaderViewHeight = mHeaderViewContent.getHeight();
	}

	/**
	 * 移除刷新view下面的一行view
	 * 
	 * @param view
	 */
	@SuppressLint("InlinedApi")
	public void removeExtraHeaderView(View view) {
		this.topExtraMargin = 0;
		mExtraHeaderView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 0));
		mExtraHeaderView.removeView(view);
		mHeaderViewHeight = mHeaderViewContent.getHeight();
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);

			TextView bottomSpace = new TextView(mContext);
			bottomSpace.setHeight(bottomMargin);
			addFooterView(bottomSpace);
		}
		super.setAdapter(adapter);
		setPullLoadEnable(true);
		resetFooterHeight();
	}

	public void setFooterReady(boolean enable) {
		this.mIsFooterReady = enable;
	}

	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) {
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mFooterViewContent.setVisibility(View.VISIBLE);
		}
	}

	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterViewContent.setVisibility(View.GONE);
			mFooterView.setOnClickListener(null);
			// 默认底部高度8dip,也可通过setFooterHeight(int height)设置
			// tv.setHeight(Util.dip2px(context, height));
		} else {
			mPullLoading = false;
			mFooterViewContent.setVisibility(View.VISIBLE);
			if (!isShow) {
				// tv.setHeight(Util.dip2px(context, 0));
			} else {
				// tv.setHeight(Util.dip2px(context, height));
			}
			mFooterView.setState(XListViewFooter.STATE_NORMAL);

			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight(500);
		}
	}

	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
		}
	}

	public void setRefreshTime(String time) {
		refresh_time = time;
		mHeaderTimeView.setText(getResources().getString(
				R.string.xlistview_header_last_time)
				+ convertRefrshTime(Long.parseLong(time)));
	}

	public String getRefreshTime() {
		return refresh_time;
	}

	public void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	public void updateHeaderHeight(float delta) {
		if (delta == 0)
			return;
		mHeaderView.setVisiableHeight((int) delta
				+ mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) {
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(XListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(XListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0);
	}

	public void resetHeaderHeight(int dur) {
		int height = mHeaderView.getVisiableHeight();
		// if (height == 0)
		// return;
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0;

		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height
				+ topExtraMargin, dur);

		invalidate();
	}

	public void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) {
				mFooterView.setState(XListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(XListViewFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);

		setSelection(mTotalItemCount - 1);
	}

	public void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, 0);
			invalidate();
		}
	}

	public void startLoadMore() {
		mFooterView.setState(XListViewFooter.STATE_LOADING);
		if (mListViewListener != null && !mPullLoading) {
			mListViewListener.onLoadMore();
		}
		mPullLoading = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isTouched = true;
			if (null != refresh_time) {
				mHeaderTimeView.setText(getResources().getString(
						R.string.xlistview_header_last_time)
						+ convertRefrshTime(Long.parseLong(refresh_time)));
			}
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			isTouched = true;
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0
					&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				// Log.e("------", mHeaderView.getVisiableHeight() + "   "
				// + deltaY);
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			// 判断是否向下且移动距离大于ListHeadViewForZJ.HEADVIEWHIGHT,且第一个可见大于0\
			removeColumnHeader();
			break;
		default:
			isTouched = false;
			mLastY = -1;
			if (getFirstVisiblePosition() == 0) {
				if (mEnablePullRefresh
						&& mHeaderView.getVisiableHeight() > mHeaderViewHeight
								+ topExtraMargin) {
					mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
					if (mListViewListener != null && !mPullRefreshing) {
						mListViewListener.onRefresh();
					}
					mPullRefreshing = true;
				}
				if (mHeaderView.getVisiableHeight() > topExtraMargin) {
					resetHeaderHeight(0);
				}
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				if (mEnablePullLoad
						&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
					startLoadMore();
				}
				resetFooterHeight();
			}
			break;
		}

		try {
			return super.onTouchEvent(ev);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	public interface ListStopListener {
		void stop();
	}

	private ListStopListener stopListener;

	public void setStopListener(ListStopListener stopListener) {
		this.stopListener = stopListener;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
		if (scrollState == SCROLL_STATE_IDLE) {
			if (null != stopListener) {
				stopListener.stop();
			}
		}
		if (scrollState == SCROLL_STATE_FLING) {
			try {
				Method method = getClass().getMethod("setOverScrollMode",
						int.class);
				Field field = getClass().getField("OVER_SCROLL_ALWAYS");
				if (method != null && field != null) {
					method.invoke(this, field.getInt(View.class));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				Method method = getClass().getMethod("setOverScrollMode",
						int.class);
				Field field = getClass().getField("OVER_SCROLL_NEVER");
				if (method != null && field != null) {
					method.invoke(this, field.getInt(View.class));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mTotalItemCount = totalItemCount;
		removeColumnHeader();
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}

		if (mHeaderView != null
				&& mHeaderView.getVisiableHeight() > topExtraMargin
				&& !isTouched) {
			resetHeaderHeight(0);
		}
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * 转化检查更新时间
	 * 
	 * @param time
	 * @return
	 */
	public String convertRefrshTime(long time) {
		if (time == 0) {
			return "从未";
		}
		long cur_time = System.currentTimeMillis();
		long del_time = cur_time - time;
		long sec = del_time / 1000;
		long min = sec / 60;
		if (min < 60) {
			if (min < 1) {
				return "刚刚";
			}
			return min + " 分钟前";
		} else {
			long h = min / 60;
			if (h < 24) {
				return h + " 小时前";
			}
			long day = h / 24;
			return day + " 天前";

		}
	}

	// 是否一直显示底部高度
	public void setIsShowBottom(boolean isShow) {
		this.isShow = isShow;
	}

	/**
	 * 手动刷新，显示刷新提示
	 */
	public void showRefreshProgress(int height) {
		mHeaderView.setVisiableHeight(height + topExtraMargin);
		mPullRefreshing = true;
		mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
	}

	/**
	 * 当快速滑动的时候去除头部 误差为2个item
	 */
	private void removeColumnHeader() {
		if (removeHeaderListener != null) {
			if (mHeaderView.getVisiableHeight() <= 0
					|| getFirstVisiblePosition() > 2) {
				removeHeaderListener.removeColumnHeader();
				mHeaderView.setTopExtraMargin(0);
			}
		}
	}

	/**
	 * 用来去除澎湃网那种头部显示的view
	 * 
	 * @author wangya
	 * 
	 */
	public interface RemoveColumnListener {
		void removeColumnHeader();
	}

	private RemoveColumnListener removeHeaderListener;

	public void setRemoveColumnHeaderListener(
			RemoveColumnListener removeHeaderListener) {
		this.removeHeaderListener = removeHeaderListener;
	}
}
