package com.joyplus;

import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;
import com.joyplus.faye.FayeClient;
import com.joyplus.faye.FayeClient.FayeListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;

public class Before_Binding extends Activity {
	FayeClient mClient;
	private String macAddress = null;
	private String tv_channel = null;

	private String user_id = null;
	App app;
	ProgressDialog pb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.before_binding);
		app = (App) getApplication();
		user_id = app.UserID;
		Intent intent = getIntent();
		macAddress = intent.getStringExtra("SaoMiao_result");
		tv_channel = "/screencast/CHANNEL_TV_" + macAddress;

		pb = new ProgressDialog(this);
		pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pb.setCanceledOnTouchOutside(false);
		pb.setCancelable(true);

		connect_TVChannel(tv_channel, user_id);

		// connect_userChannel(user_channel);

		ImageButton confirmBinding = (ImageButton) findViewById(R.id.confirm_binding);
		confirmBinding.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pb.show();
				if (!app.isNetworkAvailable()) {
					app.MyToast(Before_Binding.this, "您的网络有问题！");
					return;
				}
				if (tv_channel != null && user_id != null) {
					try {
						// connect_TVChannel(tv_channel, user_id);
						JSONObject et = new JSONObject();
						try {
							et.put("user_id", user_id);
							et.put("push_type", "31");
							mClient.sendMessage(et);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});

		ImageButton cancel_Binding = (ImageButton) findViewById(R.id.cancel_binding);
		cancel_Binding.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

	}

	private void connect_TVChannel(String channel, String user_id) {
		if (android.os.Build.VERSION.SDK_INT <= 8)
			return;
		try {

			URI uri = URI.create("http://comettest.joyplus.tv:8000/bindtv");

			JSONObject ext = new JSONObject();
			ext.put("push_type", "31");
			ext.put("user_id", user_id);
			mClient = new FayeClient(null, uri, channel);
			mClient.setFayeListener(TVChannleListener);
			mClient.connectToServer(ext);
			// mClient.sendMessage(ext);
		} catch (JSONException ex) {
		}
	}

	public void OnClickTab1TopLeft(View v) {
		finish();
	}

	FayeListener TVChannleListener = new FayeListener() {

		@Override
		public void subscriptionFailedWithError(String error) {
			Log.i("TVChannleListener", "subscriptionFailedWithError" + error);

		}

		@Override
		public void subscribedToChannel(String subscription) {
			Log.i("TVChannleListener", "subscribedToChannel" + subscription);

		}

		@Override
		public void messageReceived(JSONObject json) {
			String push_type = null;
			try {
				push_type = (String) json.get("push_type");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (push_type.equals("32")) {
				pb.dismiss();
				app.SaveServiceData("Binding_TV_Channal", macAddress);
				Log.i("TVChannleListener", "messageReceived" + json.toString());
				finish();

			}
		}

		@Override
		public void disconnectedFromServer() {
			Log.i("TVChannleListener", "disconnectedFromServer");

		}

		@Override
		public void connectedToServer() {
			Log.i("TVChannleListener", "connectedToServer");

		}
	};
}