package com.tyt.view;

import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
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

public class RegisterActivity extends BaseActivity implements OnClickListener, TextWatcher{
	public static final String FLAG_PHONE = "phone";
	private EditText mAccountInput;
	private EditText mPasswordInput;
	private EditText mRePasswordInput;
	private EditText mQqInput;
	private EditText mNameInput;
	private EditText mIdcardInput;
	private EditText mVerifyCodeInput;
	private Button mGetVerifyCode;
	private Button mOkCode;
	private TextView mTip;
	private int mSmsText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		mAccountInput = (EditText)findViewById(R.id.account);
		mAccountInput.addTextChangedListener(this);
		mPasswordInput = (EditText)findViewById(R.id.password);
		mPasswordInput.addTextChangedListener(this);
		mRePasswordInput = (EditText)findViewById(R.id.re_password);
		mRePasswordInput.addTextChangedListener(this);
		mQqInput = (EditText)findViewById(R.id.qq);
		mQqInput.addTextChangedListener(this);
		mNameInput = (EditText)findViewById(R.id.name);
		mNameInput.addTextChangedListener(this);
		mIdcardInput = (EditText)findViewById(R.id.idcard);
		mIdcardInput.addTextChangedListener(this);
		mVerifyCodeInput = (EditText)findViewById(R.id.id_entifying);
		mVerifyCodeInput.addTextChangedListener(this);
		mGetVerifyCode = (Button)findViewById(R.id.re_get);
		mGetVerifyCode.setOnClickListener(this);
		mOkCode = (Button)findViewById(R.id.ok);
		mOkCode.setOnClickListener(this);
		mTip = (TextView)findViewById(R.id.tip);
	}

	@Override
	public void handleNetErr(String err) {
		mTip.setText(R.string.err_net);
	}

	@Override
	public void handleServerErr(String err) {
		mTip.setText(err);
	}

	@Override
	public void handleNomal(String msg) {
		Intent result = new Intent();
		String phone = mAccountInput.getText().toString();
		result.putExtra(FLAG_PHONE, phone);
		setResult(CommonDefine.RESULTCODE_REGISTER, result);
	}

	@Override
	public void handleOtherMsg(Message msg) {
		switch (msg.what) {
		case CommonDefine.ERR_NONE_VERIFYCODE_OK:
			mGetVerifyCode.setClickable(false);
			new Thread(){
				public void run() {
					for (int i = 60; i > 0; i --) {
						mHandler.obtainMessage(CommonDefine.TIME_VERIFYCODE, "" + i).sendToTarget();
						try {
							sleep(1 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					mHandler.obtainMessage(CommonDefine.TIME_VERIFYCODE_END).sendToTarget();
				}
			}.start();
			break;
		case CommonDefine.ERR_VERIFYCODE:
			mTip.setText(R.string.err_verifycode_get_fail);
			break;
		case CommonDefine.TIME_VERIFYCODE:
			mGetVerifyCode.setText((String)msg.obj);
			break;
		case CommonDefine.TIME_VERIFYCODE_END:
			mGetVerifyCode.setText(R.string.re_get);
			break;
		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.re_get:
			String smsMob = mAccountInput.getText().toString();
			if (smsMob.length() != 11) {
				mTip.setText(R.string.err_phone_length);
			} else {
				doInThread(new VerifycodeRunnable(smsMob));
			}
			break;
		case R.id.ok:
			String phone = mAccountInput.getText().toString();
			if (phone.length() != 11) {
				mTip.setText(R.string.err_phone_length);
				return;
			}
			String password = mPasswordInput.getText().toString();
			if (password.length() < 6 || password.length() > 8) {
				mTip.setText(R.string.err_password_length);
				return;
			} else {
				String password_re = mRePasswordInput.getText().toString();
				if (!password_re.equals(password)) {
					mTip.setText(R.string.err_password_no_same);
					return;
				}
			}

			String qq = mQqInput.getText().toString();
			if (qq.length() == 0) {
				mTip.setText(R.string.err_no_qq);
				return;
			} 

			String name = mNameInput.getText().toString();
			if (name.length() == 0) {
				mTip.setText(R.string.err_no_name);
				return;
			} 

			String idcard = mIdcardInput.getText().toString();
			if (idcard.length() != 18) {
				mTip.setText(R.string.err_id_card_err);
				return;
			} 

			String verifyCode = mVerifyCodeInput.getText().toString();
			if (verifyCode.length() == 0) {
				mTip.setText(R.string.err_no_verifycode);
				return;
			} else {
				if (!(mSmsText + "").equals(verifyCode)) {
					mTip.setText(R.string.err_verifycode);
					return;
				}
			}
			doInThread(new RegisterRunnable(phone, password, qq, name, idcard));
			break;
		default:
			break;
		}
	}

	class VerifycodeRunnable implements Runnable {
		private String mSmsMob; 

		public VerifycodeRunnable(String smsMob) {
			mSmsMob = smsMob;
			Random random = new Random();
			mSmsText = random.nextInt(100000) + 100000;
		}

		@Override
		public void run() {
			HttpHandler httpHandler = HttpHandler.getInstance(mHandler);
			httpHandler.getVerifyCode(mSmsMob, mSmsText);
		}
	}

	class RegisterRunnable implements Runnable {
		private String mPhone; 
		private String mPassword; 
		private String mQq;
		private String mName;
		private String mIdcard;

		public RegisterRunnable(String phone, String password, String qq, String name, String idcard) {
			mPhone = phone;
			mPassword = password; 
			mQq = qq;
			mName = name;
			mIdcard = idcard;
		}

		@Override
		public void run() {
			HttpHandler httpHandler = HttpHandler.getInstance(mHandler);
			httpHandler.register(mPhone, mPassword, mQq, mName, mIdcard);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mTip.setText("");
	}

	@Override
	public void afterTextChanged(Editable s) {

	}
}
