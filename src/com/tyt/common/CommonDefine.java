package com.tyt.common;

import android.util.SparseIntArray;

import com.dxj.tyt.R;

public class CommonDefine {
	public static final boolean IS_DEBUG = true;
	
	public static final String SETTING = "setting";
	public static final String IS_SAVE_ACCOUNT = "is_save_account";
	public static final String ACCOUNT = "account";
	public static final String PASSWORD = "password";
	
	private static final String URL_BASE_TEST = "http://182.92.186.31:8080/";
	private static final String URL_BASE_REAL = "http://www.teyuntong.cn/";
	
	private static final String URL_BASE;
	static {
		if (IS_DEBUG) {
			URL_BASE = URL_BASE_TEST;
		} else {
			URL_BASE = URL_BASE_REAL;
		}
	}
	
	public static final String URL_LOGIN = URL_BASE + "user/login";
	public static final String URL_QUERY = URL_BASE + "transport/query";
	public static final String URL_REGISTER = URL_BASE + "user/save";

	public static final String VERSION = "2000";
	public static final String PRIVATEKEY = "1345~opo-4%";
	public static final String USERSIGN = "0";
	public static final String PCSIGN = "";
	
	public static final SparseIntArray Login_err = new SparseIntArray();
	static {
		Login_err.put(-1, R.string.err_login_phone_not_exit);
		Login_err.put(-2, R.string.err_login_password_err);
		Login_err.put(-3, R.string.err_login_account_not_for_this_pc);
		Login_err.put(-4, R.string.err_login_account_out_date);
		Login_err.put(-5, R.string.err_login_soft_need_update);
		Login_err.put(-6, R.string.err_login_account_not_actived);
	}
	
	public static final int ERR_NONE = 0;
	public static final int ERR_NET = ERR_NONE + 1;
	public static final int ERR_NONE_VERIFYCODE_OK = ERR_NET + 1;
	public static final int ERR_VERIFYCODE = ERR_NONE_VERIFYCODE_OK + 1;
	public static final int TIME_VERIFYCODE = ERR_VERIFYCODE + 1;
	public static final int TIME_VERIFYCODE_END = TIME_VERIFYCODE + 1;
	
	public static final int ERR_SERVER_NONE = 200;
	public static final int ERR_SERVER = 500;
	
	public static final String URL_FOR_VERIFYCODE = "http://utf8.sms.webchinese.cn/";
	public static final String VERIFYCODE_UID = "teyuntong";
	public static final String VERIFYCODE_KEY = "f631e6e38de4549d1688";
	
	public static final int REQUESTCODE_REGISTER = 1000;
	public static final int RESULTCODE_REGISTER = REQUESTCODE_REGISTER + 1;
}
