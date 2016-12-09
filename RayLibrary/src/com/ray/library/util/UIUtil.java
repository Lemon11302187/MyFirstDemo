package com.ray.library.util;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;

public class UIUtil {
	/**
	 * 获得loadingPic
	 * 
	 * @param context
	 * @return
	 */
	public static List<Integer> getLoadingPicPath(Context context) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 1;; i++) {
			Integer drawableId = 0;
			drawableId = ReflectResourceUtil.getDrawableId(context, "refresh"
					+ i);
			if (drawableId != 0) {
				list.add(drawableId);
			} else {
				break;
			}
		}
		return list;
	}

}
