package com.joy;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.joy.view.PullToRefreshView_foot;
import com.joy.view.PullToRefreshView_foot.OnFooterRefreshListener;

public class TongxunluList extends Activity implements OnFooterRefreshListener{
	Context context;
	ListView listView;
	EditText sousuo,phone;
	AutoCompleteTextView autoCompleteTextView;
	List<Map<String, String>> list= new ArrayList<Map<String,String>>();
	private int page_count = 2;
	private int current_page = 0;
    private int index =0;
	SimpleAdapter adapter;
	PullToRefreshView_foot mPullToRefreshView;
	GetThird_AccessToken GetThird_AccessToken;
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1500:
				getPhoneNum(++current_page, page_count);
				adapter=new SimpleAdapter(context,list,R.layout.mylistview,new String[] {"name"},new int[ ] {R.id.item_text});
				listView.setAdapter(adapter);
				listView.setSelection(listView.getCount()-1);
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tongxunlunlist);
		GetThird_AccessToken=(GetThird_AccessToken)getApplicationContext();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		context=this;
		mPullToRefreshView = (PullToRefreshView_foot)findViewById(R.id.tongxunlu_main_pull_refresh_view);
        mPullToRefreshView.setOnFooterRefreshListener(this);
		listView=(ListView)findViewById(R.id.tongxunlu_list);
		sousuo=(EditText)findViewById(R.id.tongxunlu_sousuo);
		phone=(EditText)findViewById(R.id.tongxunlu_phone);
		getPhoneNum(current_page, page_count);
		
		adapter=new SimpleAdapter(context,list,R.layout.mylistview,new String[] {"name"},new int[ ] {R.id.item_text});
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				GetThird_AccessToken.setphoneNum(list.get(arg2).get("phoneNumber"));
				GetThird_AccessToken.setphoneName(list.get(arg2).get("name"));
				Intent intent=new Intent();
				intent.setClass(context, Yaoqing.class);
				startActivity(intent);
			}
		});
		
	}
	public void Btnback(View v){
		finish();
	}
	public void Btnsousuo(View v){
		if (sousuo.getText().toString().trim().length()!=0) {
			
		}
		else {
			
		}
	}
	public void BtnSend(View v){
		if (phone.getText().toString().trim().length()!=0) {
			
		}
		else {
			
		}
	}
	public void getPhoneNum(int pageindex, int pagecount){//获取手机通讯录
		ContentResolver resolver = context.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,null, null, null, null);  //传入正确的uri
		if(phoneCursor!=null){
			for (int i = index; i < pagecount * (pageindex + 1) && i < phoneCursor.getCount(); i++) {
				phoneCursor.moveToPosition(i);
				int nameIndex = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME);   //获取联系人name
				String name = phoneCursor.getString(nameIndex);
				String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER)); //获取联系人number
				if(TextUtils.isEmpty(phoneNumber)){
					 continue;
				}
				index++;
				Map<String, String> map=new HashMap<String, String>();
				map.put("name", name);
				map.put("phoneNumber", phoneNumber);
				list.add(map);
			}
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onFooterRefresh(PullToRefreshView_foot view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Message msg = new Message(); 
                msg.what = 1500; 
                handler.sendMessage(msg); 
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 1000);
	}
}
