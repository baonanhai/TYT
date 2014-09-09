package com.tyt.data;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tyt.common.JsonTag;

@DatabaseTable
public class OrderInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	@DatabaseField(id = true)
	private int id;
	@DatabaseField
	private String pubTime;
	@DatabaseField
	private String	pubQQ;
	@DatabaseField
	private int status;
	@DatabaseField
	private String tel;
	@DatabaseField
	private long mtime;
	@DatabaseField
	private String resend;
	@DatabaseField
	private String uploadCellPhone;
	@DatabaseField
	private String taskContent;
	@DatabaseField
	private long ctime;
	@DatabaseField
	private String source;
	@DatabaseField
	private String nickName;
	@DatabaseField
	private String destPoint;
	@DatabaseField
	private String startPoint;
	@DatabaseField
	private long mKeepTime;
	@DatabaseField
	private boolean mIsKeep = false;
	@DatabaseField
	private boolean mIsBlack = false;

	public OrderInfo() {
		super();
	}

	public OrderInfo(JSONObject order) {
		try {
			pubTime = order.getString(JsonTag.PUB_TIME);
			pubQQ = order.getString(JsonTag.PUB_QQ);
			status = order.getInt(JsonTag.STATUS);
			tel =  order.getString(JsonTag.TEL);
			mtime =  order.getLong(JsonTag.TIME);
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
	
	public boolean isBlack() {
		return mIsBlack;
	}

	public void setIsBlack(boolean isBlack) {
		mIsBlack = isBlack;
	}
	
	public boolean isKeep() {
		return mIsKeep;
	}

	public void setIsKeep(boolean isKeep) {
		mIsKeep = isKeep;
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

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTel() {
		return tel;
	}

	public long getMtime() {
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

	public long getKeepTime() {
		return mKeepTime;
	}

	public void setKeepTime(long keepTime) {
		mKeepTime = keepTime;
	}

	@Override
	public String toString() {
		return "OrderInfo [pubTime=" + pubTime + ", pubQQ=" + pubQQ + ", status=" + status + ", tel=" + tel + ", mtime=" + mtime + ", resend="
				+ resend + ", uploadCellPhone=" + uploadCellPhone + ", taskContent=" + taskContent + ", ctime=" + ctime + ", id=" + id + ", source="
				+ source + ", nickName=" + nickName + ", destPoint=" + destPoint + ", startPoint=" + startPoint + "]";
	}
}
