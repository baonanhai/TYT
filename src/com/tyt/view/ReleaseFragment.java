package com.tyt.view;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dxj.tyt.R;
import com.tyt.common.CommonDefine;
import com.tyt.common.JsonTag;
import com.tyt.common.TYTApplication;
import com.tyt.net.HttpManager;

public class ReleaseFragment extends Fragment implements OnClickListener {
	private EditText mStart;
	private EditText mEnd;
	private EditText mGoods;
	private EditText mPhone;
	private Button mRelease;
	private TextView mTip;
	private TYTApplication mApplication;

	protected Handler mHandler;

	private static class MyHandler extends Handler {  
		private WeakReference<ReleaseFragment> mReleaseFragment;

		public MyHandler(ReleaseFragment releaseFragment) {  
			mReleaseFragment = new WeakReference<ReleaseFragment>(releaseFragment);  
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ReleaseFragment relReleaseFragment = mReleaseFragment.get();
			if (relReleaseFragment != null) {
				switch (msg.what) {
				case CommonDefine.ERR_NONE:
					try {
						JSONObject msgJson = new JSONObject((String)msg.obj);
						int code = msgJson.getInt(JsonTag.CODE);
						if (code == CommonDefine.ERR_SERVER_NONE) {
							if (msgJson.has(JsonTag.DATA)) {
								relReleaseFragment.handleNomal(msgJson.getString(JsonTag.DATA));
							} else {
								relReleaseFragment.handleNomal(msgJson.getString(JsonTag.MSG));
							}
						} else if (code == CommonDefine.ERR_SERVER) {
							relReleaseFragment.handleServerErr(msgJson.getString(JsonTag.MSG));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case CommonDefine.ERR_NET:
					relReleaseFragment.handleNetErr(relReleaseFragment.getString(R.string.err_net));
					break;
				}
			}
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mHandler = new MyHandler(this);
		
		View releaseView = inflater.inflate(R.layout.release, null);
		mStart = (EditText)releaseView.findViewById(R.id.start);
		mEnd = (EditText)releaseView.findViewById(R.id.end);
		mGoods = (EditText)releaseView.findViewById(R.id.goods);
		mPhone = (EditText)releaseView.findViewById(R.id.phone);
		mRelease = (Button)releaseView.findViewById(R.id.release);
		mRelease.setOnClickListener(this);
		mTip = (TextView)releaseView.findViewById(R.id.tip);
		mApplication = (TYTApplication)(getActivity().getApplication());
		return releaseView;
	}

	@Override
	public void onClick(View v) {
		mTip.setText("");
		String start = mStart.getText().toString();
		if (start.equals("")) {
			mTip.setText(R.string.err_no_start);
			return;
		}
		String end = mEnd.getText().toString();
		String goods = mGoods.getText().toString();
		if (goods.equals("")) {
			mTip.setText(R.string.err_no_goods);
			return;
		}

		String phone = mPhone.getText().toString();
		if (phone.equals("")) {
			mTip.setText(R.string.err_no_phone);
			return;
		}

		mApplication.doInThread(new Release(start, end, goods, phone));
	}

	class Release implements Runnable {
		private String mStart;
		private String mEnd;
		private String mGoods;
		private String mPhone;
		private String mUploadCellPhone;
		private String mNickName;
		private String mQq;

		public Release(String start, String end, String goods, String phone) {
			mStart = start;
			mEnd = end;
			mGoods = goods;
			mPhone = phone;
			mNickName = mApplication.getPersonInfo().nickname;
			mQq = mApplication.getPersonInfo().qq;
			mUploadCellPhone = mApplication.getPersonInfo().cellPhone;
		}

		@Override
		public void run() {
			HttpManager httpHandler = HttpManager.getInstance(mHandler);
			httpHandler.releaseOrder(mStart, mEnd, mGoods, mPhone, mNickName, mQq, mUploadCellPhone);
		}
	}

	public void handleNetErr(String err) {

	}

	public void handleServerErr(String err) {
		Toast.makeText(mApplication, R.string.release_fail, Toast.LENGTH_LONG).show();
	}

	public void handleNomal(String msg) {
		Toast.makeText(mApplication, R.string.release_succ, Toast.LENGTH_LONG).show();
	}
}
