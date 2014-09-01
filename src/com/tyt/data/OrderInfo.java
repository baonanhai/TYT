package com.tyt.data;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.tyt.common.JsonTag;

public class OrderInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String pubTime;
	private String	pubQQ;
	private int status;
	private String tel;
	private String mtime;
	private String resend;
	private String uploadCellPhone;
	private String taskContent;
	private long ctime;
	private int id;
	private String source;
	private String nickName;
	private String destPoint;
	private String startPoint;

	public OrderInfo(JSONObject order) {
		try {
			pubTime = order.getString(JsonTag.PUB_TIME);
			pubQQ = order.getString(JsonTag.PUB_QQ);
			status = order.getInt(JsonTag.STATUS);
			tel =  order.getString(JsonTag.TEL);
			mtime =  order.getString(JsonTag.TIME);
			resend =  order.getString(JsonTag.RESEND);
			taskContent =  order.getString(JsonTag.TASK_CONTENT);
			ctime =  order.getLong(JsonTag.CTIME);
			id =  order.getInt(JsonTag.ID);
			source =  order.getString(JsonTag.SOURCE);
			nickName =  order.getString(JsonTag.NICK_NAME);
			startPoint =  order.getString(JsonTag.STARTPOINT);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			destPoint =  order.getString(JsonTag.DESTPOINT);
		} catch (JSONException e) {
			//
		}
		
		try {
			uploadCellPhone =  order.getString(JsonTag.UPLOAD_CELL_PHONE);
		} catch (JSONException e) {
			//
		}
	}

	public String getPubTime() {
		return pubTime;
	}

	public String getPubQQ() {
		return pubQQ;
	}

	public int getStatus() {
		return status;
	}

	public String getTel() {
		return tel;
	}

	public String getMtime() {
		return mtime;
	}

	public String getResend() {
		return resend;
	}

	public String getUploadCellPhone() {
		return uploadCellPhone;
	}

	public String getTaskContent() {
		return taskContent;
	}

	public long getCtime() {
		return ctime;
	}

	public int getId() {
		return id;
	}

	public String getSource() {
		return source;
	}

	public String getNickName() {
		return nickName;
	}

	public String getDestPoint() {
		return destPoint;
	}

	public String getStartPoint() {
		return startPoint;
	}

	@Override
	public String toString() {
		return "OrderInfo [pubTime=" + pubTime + ", pubQQ=" + pubQQ + ", status=" + status + ", tel=" + tel + ", mtime=" + mtime + ", resend="
				+ resend + ", uploadCellPhone=" + uploadCellPhone + ", taskContent=" + taskContent + ", ctime=" + ctime + ", id=" + id + ", source="
				+ source + ", nickName=" + nickName + ", destPoint=" + destPoint + ", startPoint=" + startPoint + "]";
	}
}
