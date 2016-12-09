package com.ray.library.util;

import android.content.Context;

/**
 * 反射资源文件的工具类（比如s"app_name"--->"R.string.app_name"）
 * 
 * @author WANGYA
 * 
 */
public class ReflectResourceUtil {
	/**
	 * 通过layout名字，获得layout的id
	 * 
	 * @param paramContext
	 * @param paramString
	 * @return
	 */
	public static int getLayoutId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "layout",
				paramContext.getPackageName());
	}

	/**
	 * 通过String名字，获得String的id
	 * 
	 * @param paramContext
	 * @param paramString
	 * @return
	 */
	public static int getStringId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "string",
				paramContext.getPackageName());
	}

	/**
	 * 通过Drawable名字，获得Drawable的id
	 * 
	 * @param paramContext
	 * @param paramString
	 * @return
	 */
	public static int getDrawableId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString,
				"drawable", paramContext.getPackageName());
	}

	/**
	 * 通过Style名字，获得Style的id
	 * 
	 * @param paramContext
	 * @param paramString
	 * @return
	 */
	public static int getStyleId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "style",
				paramContext.getPackageName());
	}

	/**
	 * 通过Id名字，获得Id的id
	 * 
	 * @param paramContext
	 * @param paramString
	 * @return
	 */
	public static int getId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "id",
				paramContext.getPackageName());
	}

	/**
	 * 通过Color名字，获得Color的id
	 * 
	 * @param paramContext
	 * @param paramString
	 * @return
	 */

	public static int getColorId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "color",
				paramContext.getPackageName());
	}

	/**
	 * 通过Array名字，获得Array的id
	 * 
	 * @param paramContext
	 * @param paramString
	 * @return
	 */
	public static int getArrayId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "array",
				paramContext.getPackageName());
	}
}
