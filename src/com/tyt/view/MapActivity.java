package com.tyt.view;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dxj.tyt.R;
import com.tyt.common.CommonDefine;

public class MapActivity extends Activity {
	public static final String START = "Start";
	public static final String STOP = "Stop";
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		mWebView = (WebView)findViewById(R.id.webView);
		mWebView.getSettings().setJavaScriptEnabled(true);  
		//mWebView.getSettings().setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");
		String start = getIntent().getStringExtra(START);
		String stop = getIntent().getStringExtra(STOP);
		String url = CommonDefine.URL_MAP_1 + Uri.encode(start) + CommonDefine.URL_MAP_2 + Uri.encode(stop) + CommonDefine.URL_MAP_3;
		Log.i("sssss", url);
		mWebView.loadUrl(url); 
		mWebView.setWebViewClient(new WebViewClient(){  
			@Override  
			public boolean shouldOverrideUrlLoading(WebView view, String url) {  
				view.loadUrl(url); 
				return true;
			}  
		});  
	}
}
