package com.tyt.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dxj.tyt.R;
import com.tyt.common.CommonDefine;
import com.tyt.net.HttpHandler;

public class LoginActivity extends BaseActivity implements TextWatcher ,OnClickListener{
	private EditText mAccountInput;
	private EditText mPasswordInput;
	private Button mLogin;
	private Button mRegister;
	private TextView mErrTip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		mAccountInput = (EditText)findViewById(R.id.account);
		mAccountInput.addTextChangedListener(this);
		mPasswordInput = (EditText)findViewById(R.id.password);
		mPasswordInput.addTextChangedListener(this);
		mLogin = (Button)findViewById(R.id.login);
		mLogin.setOnClickListener(this);
		mRegister = (Button)findViewById(R.id.register);
		mRegister.setOnClickListener(this);
		mErrTip = (TextView)findViewById(R.id.err_tip);
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
			startActivity(registerIntent);
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
			HttpHandler httpHandler = HttpHandler.getInstance(mHandler);
			httpHandler.login(account, password);
		}
	}

	@Override
	public void handleNetErr(String err) {
		mErrTip.setText(R.string.err_net);
	}

	@Override
	public void handleNomal(String msg) {
		mErrTip.setText(msg);
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
}
