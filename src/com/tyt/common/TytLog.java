package com.tyt.common;

import android.util.Log;

public class TytLog {
	public static void i(String flag, String content) {
		if (CommonDefine.IS_DEBUG) {
			Log.i(flag, content);
		}
	}
	
	public static void i(String content) {
		i("sssss", content);
	}
}
