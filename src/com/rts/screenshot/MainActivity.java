package com.rts.screenshot;

import java.io.IOException;

import com.rts.transfer.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint({ "NewApi", "SimpleDateFormat" })
public class MainActivity extends Activity implements OnClickListener {

	Button bShoot;
	ImageView img;
	WebView mWebview;
	protected Bitmap screen_bitmap;
	private final int port = 8719;
	RaspberryPi pi = new RaspberryPi(port);
	AndroidMe me = new AndroidMe(port, "screen_shot");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		new StartReciever().execute(null, null, null);

		bShoot = (Button) findViewById(R.id.bTakeShot);
		bShoot.setOnClickListener(this);
		img = (ImageView) findViewById(R.id.imageView1);
		mWebview = (WebView) findViewById(R.id.webView1);

		mWebview.getSettings().setJavaScriptEnabled(true);
		mWebview.getSettings().setSupportMultipleWindows(true);

		mWebview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		mWebview.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onCreateWindow(WebView view, boolean dialog,
					boolean userGesture, Message resultMsg) {
				Toast.makeText(getApplicationContext(), "OnCreateWindow",
						Toast.LENGTH_LONG).show();
				return true;
			}
		});
		mWebview.loadUrl("http://www.google.com");

		/*
		 * mWebview.setWebViewClient(new WebViewClient() { public void
		 * onReceivedError(WebView view, int errorCode, String description,
		 * String failingUrl) { Toast.makeText(getApplicationContext(),
		 * description, Toast.LENGTH_SHORT).show(); } });
		 */
		/*
		 * topLevelView.setDrawingCacheEnabled(true); Bitmap fullScreenBitmap =
		 * Bitmap.createBitmap(topLevelView .getDrawingCache());
		 * topLevelView.setDrawingCacheEnabled(false);
		 */}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public Bitmap getScreenShot() {
		return screen_bitmap;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bTakeShot:

			Toast.makeText(getApplicationContext(), "TakeShot",
					Toast.LENGTH_SHORT).show();
			View v1 = getCurrentFocus();// .findViewById(R.id.webView1);
			v1.setDrawingCacheEnabled(true);
			screen_bitmap = Bitmap.createBitmap(v1.getDrawingCache());
			v1.setDrawingCacheEnabled(false);
			Drawable d = new BitmapDrawable(getResources(), screen_bitmap);
			img.setBackgroundColor(Color.WHITE);
			img.setBackground(null);
			img.setBackground(d);

			RaspberryPi server = new RaspberryPi(port);
			server.run();
			new StartSender().execute(null, null, null);

			// Calendar c = Calendar.getInstance();
			// SimpleDateFormat df = new
			// SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			// String formattedDate = df.format(c.getTime());
			// Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
			// Process sh;
			// try {
			// sh = Runtime.getRuntime().exec("su");
			// OutputStream os = sh.getOutputStream();
			// os.write(("/system/bin/screencap -p " + "/sdcard/img.png")
			// .getBytes("ASCII"));
			// os.flush();
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
			//
			// }
			break;
		}
	}

	class StartReciever extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			pi.start();
			me.start();
			try {
				if (me.getReader() == null)
					return null;
				else
					me.getReader().start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

	class StartSender extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				if (me.getWriter() == null)
					return null;
				else
					me.getWriter().start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

}
