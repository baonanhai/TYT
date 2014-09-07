package com.tyt.common;

import android.util.SparseIntArray;

import com.dxj.tyt.R;

public class CommonDefine {
	public static final boolean IS_DEBUG = true;
	
	public static final int NUMBER_FOR_BACKGROUND_THREAD = 6;

	public static final String SETTING = "Setting";
	public static final String SERVICE_TIME = "ServiceTime";
	public static final String ORDER_SAVE = "OrderSave";
	public static final String KEEP_ORDER_SAVE = "KeepOrderSave";
	public static final String BLACK_ORDER_SAVE = "BlackOrderSave";
	public static final String IS_SAVE_ACCOUNT = "IsSaveAccount";
	public static final String IS_AUTO_LOGIN = "IsAutoLogin";
	public static final String ACCOUNT = "Account";
	public static final String SERVE_DAYS = "ServeDays";
	public static final String PASSWORD = "Password";
	public static final String TICKET = "Ticket";
	
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
	public static final String URL_REGISTER = URL_BASE + "user/save";
	public static final String URL_QUERY = URL_BASE + "transport/query";
	public static final String URL_CHECK_TICKET = URL_BASE + "user/checkTicket";
	public static final String URL_MAP_1 = "http://map.sogou.com/#s=m==nav!!from==";
	public static final String URL_MAP_2 = "|||uid!!to==";
	public static final String URL_MAP_3 = "|||uid!!fromidx==!!toidx==!!tactic==1!!mode==1!!exactroute==1";
	
	public static final String URL_RELEASE = URL_BASE + "transport/save";
	public static final String URL_INFO_QUERY = URL_BASE + "user/get";
	public static final String URL_INFO_UPDATE = URL_BASE + "transport/update";

	public static final int DELAY_FOR_GET_DELAY = 10 * 1000;
	public static final int DELAY_FOR_CHECK_TICKET = 5 * 60 * 1000;
	public static final String VERSION = "2000";
	public static final String PLAT_ID = "2";
	public static final String PRIVATEKEY = "1345~opo-4%";
	public static final String USERSIGN = "2";   //用户身份标识 0 车主(注册不通过，服务器端问题) 1配货站 2货主 3 销售 4 管理员

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
	public static final int LOCATION_INIT_END = TIME_VERIFYCODE_END + 1;

	public static final int ERR_SERVER_NONE = 200;
	public static final int ERR_SERVER = 500;

	public static final String URL_FOR_VERIFYCODE = "http://utf8.sms.webchinese.cn/";
	public static final String VERIFYCODE_UID = "teyuntong";
	public static final String VERIFYCODE_KEY = "f631e6e38de4549d1688";

	public static final int REQUESTCODE_REGISTER = 1000;
	public static final int RESULTCODE_REGISTER = REQUESTCODE_REGISTER + 1;
	
	public static final int ORDER_STATE_USEFULL = 1;
	public static final int ORDER_STATE_NO_USEFULL = 0;
	public static final int ORDER_STATE_COMPLETE = 4;
}
