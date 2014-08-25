package com.tyt.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.dxj.tyt.R;
import com.tyt.background.TytService;
import com.tyt.common.CommonDefine;
import com.tyt.common.JsonTag;
import com.tyt.net.HttpManager;

public class LoginActivity extends BaseActivity implements TextWatcher ,OnClickListener, OnCheckedChangeListener{
	private EditText mAccountInput;
	private EditText mPasswordInput;
	private Button mLogin;
	private Button mRegister;
	private TextView mErrTip;
	private CheckBox mIsSaveCheck;
	private SharedPreferences mSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		mAccountInput = (EditText)findViewById(R.id.account);
		mAccountInput.addTextChangedListener(this);
		mPasswordInput = (EditText)findViewById(R.id.password);
		mPasswordInput.addTextChangedListener(this);
		mIsSaveCheck = (CheckBox)findViewById(R.id.is_save_check);
		mIsSaveCheck.setOnCheckedChangeListener(this);
		mLogin = (Button)findViewById(R.id.login);
		mLogin.setOnClickListener(this);
		mRegister = (Button)findViewById(R.id.register);
		mRegister.setOnClickListener(this);
		mErrTip = (TextView)findViewById(R.id.err_tip);

		mSharedPreferences = getSharedPreferences(CommonDefine.SETTING, Context.MODE_PRIVATE);

		boolean isSave = mSharedPreferences.getBoolean(CommonDefine.IS_SAVE_ACCOUNT, false);
		if (isSave) {
			String account = mSharedPreferences.getString(CommonDefine.ACCOUNT, "");
			String password = mSharedPreferences.getString(CommonDefine.PASSWORD, "");
			mAccountInput.setText(account);
			mPasswordInput.setText(password);
			mIsSaveCheck.setChecked(true);
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent intent) {
		super.onActivityResult(arg0, arg1, intent);
		if (intent != null) {
			String phone = intent.getStringExtra(RegisterActivity.FLAG_PHONE);
			mAccountInput.setText(phone);
		}
	}

	@Override
	public void onClick(View v) {
		mErrTip.setText("");
		int id = v.getId();
		switch (id) {
		case R.id.login:
			doInThread(new LoginRunnable());
			break;
		case R.id.register:
			Intent registerIntent = new Intent(this, RegisterActivity.class);
			startActivityForResult(registerIntent, CommonDefine.REQUESTCODE_REGISTER);
			break;
		default:
			break;
		}
	}

	class LoginRunnable implements Runnable {
		@Override
		public void run() {
			String account = mAccountInput.getText().toString();
			if (account.length() != 11) {
				mErrTip.setText(R.string.err_phone_length);
				return;
			}
			String password = mPasswordInput.getText().toString();
			if (password.length() < 6 || password.length() > 8) {
				mErrTip.setText(R.string.err_password_length);
				return;
			}
			HttpManager httpHandler = HttpManager.getInstance(mHandler);
			httpHandler.login(account, password);
		}
	}

	@Override
	public void handleNetErr(String err) {
		mErrTip.setText(R.string.err_net);
	}

	@Override
	public void handleNomal(String msg) {
		boolean isSave = mSharedPreferences.getBoolean(CommonDefine.IS_SAVE_ACCOUNT, false);
		if (isSave) {
			String account = mAccountInput.getText().toString();
			String password = mPasswordInput.getText().toString();
			Editor editor = mSharedPreferences.edit();
			editor.putString(CommonDefine.ACCOUNT, account);
			editor.putString(CommonDefine.PASSWORD, password);
			try {
				JSONObject response = new JSONObject(msg);
				editor.putInt(CommonDefine.SERVE_DAYS, response.getInt(JsonTag.SERVE_DAYS));
				editor.putString(CommonDefine.TICKET, response.getString(JsonTag.TICKET));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			editor.commit();
		}

		Intent serviceIntent = new Intent(this, TytService.class);
		startService(serviceIntent);

		Intent allIntent = new Intent(this, AllInfoActivity.class);
		startActivity(allIntent);
	}

	@Override
	public void handleServerErr(String err) {
		mErrTip.setText(getString(CommonDefine.Login_err.get(Integer.parseInt(err))));
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mErrTip.setText("");
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void handleOtherMsg(Message msg) {

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(CommonDefine.IS_SAVE_ACCOUNT, isChecked);
		editor.commit();
	}
}
