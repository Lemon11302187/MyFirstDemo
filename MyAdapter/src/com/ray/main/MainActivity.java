package com.ray.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ray.library.R;
import com.ray.library.ui.adapter.CommonAdapter;
import com.ray.library.ui.adapter.ViewHolder;
import com.ray.library.ui.listview.IXListViewListener;
import com.ray.library.ui.listview.XListView;
import com.ray.main.bean.Bean;

/**
 * @author Ray 自定义的MyAdapter 继承了 封装的 Adapter 自定义ListView 集成了 下拉刷新和上拉加载
 */
public class MainActivity extends Activity {

	private Context mContext;
	private XListView xListView;
	private TextView myTitle;
	private MyAdapter myAdapter;
	private List<Bean> mDatas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_common);
		initDatas("onCreate");
		initView();
		setAdapter();
		setLisener();
	}

	private void setAdapter() {
		myAdapter = new MyAdapter(mContext, mDatas, R.layout.item_list);
		xListView.setAdapter(myAdapter);
	}

	private void initView() {
		xListView = (XListView) findViewById(R.id.mylist);
		xListView.init(0, 0);
		myTitle = (TextView) findViewById(R.id.mytitle);
		myTitle.setText("Ray's ListView");
	}

	class MyAdapter extends CommonAdapter<Bean> {

		public MyAdapter(Context context, List<Bean> mDatas, int itemLayoutId) {
			super(context, mDatas, itemLayoutId);
		}

		@Override
		public void convert(ViewHolder helper, Bean item) {
			helper.setImageResource(R.id.img_pic, R.drawable.ic_launcher);
			helper.setText(R.id.tv_title, item.getTitle());
			helper.setText(R.id.tv_describe, item.getDesc());
			helper.setText(R.id.tv_phone, item.getPhone());
			helper.setText(R.id.tv_time, item.getTime());
		}

		@Override
		public void appendData(List<Bean> data) {
			this.myDatas.addAll(data);
			notifyDataSetChanged();
		}
	}

	private void setLisener() {
		xListView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						notifyMyAdapter("refresh");
					}
				}, 1000);
			}

			@Override
			public void onLoadMore() {

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						notifyMyAdapter("add");
					}
				}, 1000);
			}
		});

		xListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				Toast.makeText(mContext, "click >>> " + paramLong,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	protected void notifyMyAdapter(String dosth) {
		Toast.makeText(getApplicationContext(), dosth, Toast.LENGTH_SHORT)
				.show();
		if (null == myAdapter) {
			myAdapter = new MyAdapter(mContext, mDatas, R.layout.item_list);
		}
		if (TextUtils.equals(dosth, "add")) {

			xListView.stopLoadMore();
			initDatas("onLoadMore");
			myAdapter.appendData(mDatas);
			xListView.setPullLoadEnable(true);

		} else if (TextUtils.equals(dosth, "refresh")) {

			xListView.stopRefresh();
			xListView.setRefreshTime(System.currentTimeMillis() + "");
			initDatas("refresh");
			myAdapter.clearData();
			myAdapter.appendData(mDatas);

		}
	}

	private void initDatas(String tag) {
		mDatas = new ArrayList<Bean>();
		Bean bean = null;
		for (int i = 0; i < 10; i++) {
			bean = new Bean(tag + "    世上本没有路" + i, "走的人多了，也便成了路" + i,
					"号码：10086", "时间：20130240122");
			mDatas.add(bean);
		}
	}
}
