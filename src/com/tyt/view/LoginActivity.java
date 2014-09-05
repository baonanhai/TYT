package com.tyt.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dxj.tyt.R;
import com.tyt.background.TytService;
import com.tyt.common.CommonDefine;
import com.tyt.common.JsonTag;
import com.tyt.net.HttpManager;

public class LoginActivity extends BaseActivity implements TextWatcher ,OnClickListener {
	private EditText mAccountInput;
	private EditText mPasswordInput;
	private Button mLogin;
	private Button mRegister;
	private TextView mErrTip;
	private TextView mIsSaveCheck;
	private TextView mIsAutoLoginCheck;
	private SharedPreferences mSharedPreferences;
	private boolean mIsSavePassword = true;
	private boolean mIsAutoLogin = true;
	private boolean mIsLoginSuc = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		mAccountInput = (EditText)findViewById(R.id.account);
		mAccountInput.addTextChangedListener(this);
		mPasswordInput = (EditText)findViewById(R.id.password);
		mPasswordInput.addTextChangedListener(this);
		mIsSaveCheck = (TextView)findViewById(R.id.is_save_check);
		mIsSaveCheck.setOnClickListener(this);
		mIsAutoLoginCheck = (TextView)findViewById(R.id.is_auto_login);
		mIsAutoLoginCheck.setOnClickListener(this);

		mLogin = (Button)findViewById(R.id.login);
		mLogin.setOnClickListener(this);
		mRegister = (Button)findViewById(R.id.register);
		mRegister.setOnClickListener(this);
		mErrTip = (TextView)findViewById(R.id.err_tip);

		mSharedPreferences = getSharedPreferences(CommonDefine.SETTING, Context.MODE_PRIVATE);

		mIsSavePassword = mSharedPreferences.getBoolean(CommonDefine.IS_SAVE_ACCOUNT, true);
		if (mIsSavePassword) {
			String account = mSharedPreferences.getString(CommonDefine.ACCOUNT, "");
			String password = mSharedPreferences.getString(CommonDefine.PASSWORD, "");
			mAccountInput.setText(account);
			mPasswordInput.setText(password);
			setLeftDrawable(mIsSaveCheck, R.drawable.select_yes);
		} else {
			setLeftDrawable(mIsSaveCheck, R.drawable.select_no);
		}

		mIsAutoLogin = mSharedPreferences.getBoolean(CommonDefine.IS_AUTO_LOGIN, true);
		if (mIsAutoLogin) {
			setLeftDrawable(mIsAutoLoginCheck, R.drawable.select_yes);
		} else {
			setLeftDrawable(mIsAutoLoginCheck, R.drawable.select_no);
		}

		setTitle(getString(R.string.login));
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
			mIsLoginSuc = false;
			doInThread(new LoginRunnable());
			break;
		case R.id.register:
			Intent registerIntent = new Intent(this, RegisterActivity.class);
			startActivityForResult(registerIntent, CommonDefine.REQUESTCODE_REGISTER);
			break;
		case R.id.is_save_check:
			if (mIsSavePassword) {
				setLeftDrawable(mIsSaveCheck, R.drawable.select_no);
			} else {
				setLeftDrawable(mIsSaveCheck, R.drawable.select_yes);
			}
			mIsSavePassword = !mIsSavePassword;
			Editor editor = mSharedPreferences.edit();
			editor.putBoolean(CommonDefine.IS_SAVE_ACCOUNT, mIsSavePassword);
			editor.commit();
			break;
		case R.id.is_auto_login:
			if (mIsAutoLogin) {
				setLeftDrawable(mIsAutoLoginCheck, R.drawable.select_no);
			} else {
				setLeftDrawable(mIsAutoLoginCheck, R.drawable.select_yes);
			}

			mIsAutoLogin = !mIsAutoLogin;
			Editor editor1 = mSharedPreferences.edit();
			editor1.putBoolean(CommonDefine.IS_AUTO_LOGIN, mIsAutoLogin);
			editor1.commit();
			break;
		default:
			break;
		}
	}

	private void setLeftDrawable(TextView view, int drawableId) {
		Drawable drawable = getResources().getDrawable(drawableId);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		view.setCompoundDrawables(drawable, null, null, null);
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

	class PersonDataInit implements Runnable {
		String account;
		String password;

		public PersonDataInit(String account, String password) {
			this.account = account;
			this.password = password;
		}

		@Override
		public void run() {
			HttpManager httpHandler = HttpManager.getInstance(mHandler);
			httpHandler.getPersonInfo(account, password);
		}
	}

	@Override
	public void handleNetErr(String err) {
		mErrTip.setText(R.string.err_net);
	}

	@Override
	public void handleNomal(String msg) {
		if (!mIsLoginSuc) {
			boolean isSave = mSharedPreferences.getBoolean(CommonDefine.IS_SAVE_ACCOUNT, false);
			String account = mAccountInput.getText().toString();
			String password = mPasswordInput.getText().toString();

			Editor editor = mSharedPreferences.edit();
			if (isSave) {
				editor.putString(CommonDefine.ACCOUNT, account);
				editor.putString(CommonDefine.PASSWORD, password);
			}
			try {
				JSONObject response = new JSONObject(msg);
				editor.putInt(CommonDefine.SERVE_DAYS, response.getInt(JsonTag.SERVE_DAYS));
				editor.putString(CommonDefine.TICKET, response.getString(JsonTag.TICKET));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			editor.commit();
			
			mIsLoginSuc = true;
			
			doInThread(new PersonDataInit(account, password));
		} else {
			Intent serviceIntent = new Intent(this, TytService.class);
			serviceIntent.putExtra(TytService.COMMAND, TytService.COMMAND_INIT);
			startService(serviceIntent);

			Intent allIntent = new Intent(this, AllInfoActivity.class);
			startActivity(allIntent);
		}
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
	protected void loginOther() {
		Toast.makeText(this, R.string.login_other, Toast.LENGTH_LONG).show();
	}
}
