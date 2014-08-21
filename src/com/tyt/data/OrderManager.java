package com.tyt.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;

import com.tyt.common.JsonTag;

public class OrderManager {
	private Context mContext;
	public OrderManager(Context context) {
		mContext = context;
	}

	public void addOrderInfo(String response) {
		try {
			JSONArray data = new JSONObject(response).getJSONArray(JsonTag.DATA);
			if (data.length() > 0) {
				File xx = new File(Environment.getExternalStorageDirectory(), "json.txt");
				FileWriter fw = new FileWriter(xx);
				fw.write(data.toString());
				fw.flush();
				fw.close();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
