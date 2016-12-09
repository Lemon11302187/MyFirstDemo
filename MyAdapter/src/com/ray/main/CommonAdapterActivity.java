package com.ray.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ray.library.R;
import com.ray.library.ui.listview.IXListViewListener;
import com.ray.library.ui.listview.XListView;
import com.ray.main.bean.Bean;

/**
 * @author Ray 南京厚建 2015-8-24 TODO
 * 自定义的MyAdapter 继承了  BaseAdapter
 * 自定义ListView  集成了 下拉刷新和上拉加载
 */
public class CommonAdapterActivity extends Activity {

	private XListView listView;
	private MyAdapter myAdapter;
	private List<Bean> mDatas;
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInflater = LayoutInflater.from(getApplicationContext());
		setContentView(R.layout.activity_common);
		listView = (XListView) findViewById(R.id.mylist);
		listView.init(50, 0);
		initDatas("onCreate");
		myAdapter = new MyAdapter(mDatas);
		listView.setAdapter(myAdapter);

		listView.setXListViewListener(new IXListViewListener() {

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

	}

	protected void notifyMyAdapter(String dosth) {
		Toast.makeText(getApplicationContext(), dosth, Toast.LENGTH_SHORT)
				.show();
		if (null == myAdapter) {
			myAdapter = new MyAdapter(mDatas);
		}
		if (TextUtils.equals(dosth, "add")) {

			listView.stopLoadMore();
			initDatas("onLoadMore");
			myAdapter.appendData(mDatas);
			listView.setPullLoadEnable(true);

		} else if (TextUtils.equals(dosth, "refresh")) {
			
			listView.stopRefresh();
			listView.setRefreshTime(System.currentTimeMillis()+"");
			initDatas("refresh");
			myAdapter.clearAll();
			myAdapter.appendData(mDatas);
			
		}
	}

	class MyAdapter extends BaseAdapter {

		private List<Bean> myDatas;

		public MyAdapter(List<Bean> myDatas) {
			this.myDatas = myDatas;
		}

		@Override
		public int getCount() {
			return myDatas.size();
		}

		@Override
		public Object getItem(int paramInt) {
			return myDatas.get(paramInt);
		}

		@Override
		public long getItemId(int paramInt) {
			return 0;
		}

		public void appendData(List<Bean> myDatas) {
			this.myDatas.addAll(myDatas);
			notifyDataSetChanged();
		}

		public void clearAll() {
			myDatas.clear();
			notifyDataSetChanged();
		}

		@Override
		public View getView(int paramInt, View convertView,
				ViewGroup paramViewGroup) {
			ViewHolder holder;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_list, null);
				holder.myImageView = (ImageView) convertView
						.findViewById(R.id.img_pic);
				holder.myTextView1 = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.myTextView2 = (TextView) convertView
						.findViewById(R.id.tv_describe);
				holder.myTextView3 = (TextView) convertView
						.findViewById(R.id.tv_time);
				holder.myTextView4 = (TextView) convertView
						.findViewById(R.id.tv_phone);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Bean myBean = myDatas.get(paramInt);
			holder.myImageView.setImageResource(R.drawable.ic_launcher);
			holder.myTextView1.setText(myBean.getTitle());
			holder.myTextView2.setText(myBean.getDesc());
			holder.myTextView3.setText(myBean.getTime());
			holder.myTextView4.setText(myBean.getPhone());

			return convertView;
		}

	}

	static class ViewHolder {

		ImageView myImageView;
		TextView myTextView1;
		TextView myTextView2;
		TextView myTextView3;
		TextView myTextView4;

	}

	private void initDatas(String tag) {
		mDatas = new ArrayList<Bean>();
		Bean bean = null;
		for (int i = 0; i < 10; i++) {
			bean = new Bean(tag + "	   世上本没有路" + i, "走的人多了，也便成了路" + i,
					"号码：10086", "时间：20130240122");
			mDatas.add(bean);
		}
	}

}
