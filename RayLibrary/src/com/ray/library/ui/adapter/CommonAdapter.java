package com.ray.library.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author Ray 南京厚建 2015-8-20
 */

public abstract class CommonAdapter<T> extends BaseAdapter {
	protected LayoutInflater myInflater;
	protected Context myContext;
	protected List<T> myDatas;
	protected final int myItemLayoutId;

	public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId) {
		this.myContext = context;
		this.myInflater = LayoutInflater.from(myContext);
		this.myDatas = mDatas;
		this.myItemLayoutId = itemLayoutId;
	}

	@Override
	public int getCount() {
		return myDatas.size();
	}

	@Override
	public T getItem(int position) {
		return myDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 清空当前 adapter 数据
	 */
	public void clearData() {
		myDatas.clear();
		notifyDataSetChanged();
	}

	/**
	 * 向 adapter 动态添加数据
	 * 
	 * @param data
	 */
	public void appendData(List<T> data) {
		myDatas.addAll(data);
		notifyDataSetChanged();
	}

	public abstract void convert(ViewHolder helper, T item);

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder = getViewHolder(position, convertView,
				parent);
		convert(viewHolder, getItem(position));
		return viewHolder.getConvertView();

	}

	private ViewHolder getViewHolder(int position, View convertView,
			ViewGroup parent) {
		return ViewHolder.get(myContext, convertView, parent, myItemLayoutId,
				position);
	}
}
