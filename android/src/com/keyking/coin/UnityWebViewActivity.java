package com.keyking.coin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.unity3d.player.UnityPlayer;

public class UnityWebViewActivity extends Activity {
	
	private WebView webView;
	
	//private Button close;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		webView = (WebView) findViewById(R.id.webView);
		String url = getIntent().getStringExtra("OPEN_URL");
		String gameObject = getIntent().getStringExtra("GAME_OBJECT");
		WebSettings webSettings =   webView .getSettings();       
		webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setSupportZoom(true);
		webView.loadUrl(url);
		webView.setWebViewClient(new WebViewClientSun(gameObject));
	    webView.getSettings().setJavaScriptEnabled(true);
		/*close = (Button) findViewById(R.id.button);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});*/
	}
 
	private class WebViewClientSun extends WebViewClient {
		
		String gameObject;

		public WebViewClientSun(String gameObject){
			this.gameObject = gameObject;
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			//这里实现的目标是在网页中继续点开一个新链接，还是停留在当前程序中
			view.loadUrl(url);
			return super.shouldOverrideUrlLoading(view, url);
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			UnityPlayer.UnitySendMessage(gameObject,"onLoadStart",url);
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			UnityPlayer.UnitySendMessage(gameObject,"onLoadFinish",url);
		}
		
		@Override
		public void onReceivedError(WebView view, int errorCode,String desc,String url) {
			UnityPlayer.UnitySendMessage(gameObject,"onLoadFail",url);
		}
	}
}
