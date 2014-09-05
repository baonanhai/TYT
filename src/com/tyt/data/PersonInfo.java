package com.tyt.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.tyt.common.JsonTag;

public class PersonInfo {
	public String cellPhone;
	private String contactNum;
	private String ctime;
	private int id;
	private String idCard;
	private int infoUploadFlag;
	private long mtime;
	private String password;
	private String pcSign;
	public String qq;
	private String qqModTimes;
	private int serveDays;
	private String ticket;
	private String trueName;
	private String userName;
	private int userSign;
	private int userType;
	public String nickname;

	public PersonInfo(String info) {
		try {
			JSONObject infoJson = new JSONObject(info);
			JSONObject dataJson = infoJson.getJSONObject(JsonTag.DATA);
			cellPhone = dataJson.getString(JsonTag.CELLPHONE);
			qq = dataJson.getString(JsonTag.QQ_MINI);
			trueName = dataJson.getString(JsonTag.TRUE_NAME);
			nickname = trueName + "(" + qq + ")";
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
