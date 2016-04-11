package com.keyking.admin.frame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.keyking.admin.R;
import com.keyking.admin.net.DataBuffer;
import com.keyking.admin.net.resp.ResultCallBack;

public class AdminUser extends BaseActiivity implements ResultCallBack {
	static final String[] TITLE_NAME_BUY = {"买家会员","高级买家","其他"};
	static final String[] TITLE_NAME_SELLER = {"普通卖家","知名邮商","金牌经纪人","其他"};
	static final String[] PERMISSION_NAMES = {"买家","卖家"};
    Spinner spinner_title;
    Spinner spinner_permission;
    ArrayAdapter<String> adapter_title;
    ArrayAdapter<String> adapter_permission;
    EditText edit_title;
    EditText editText_nike;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_user);
		spinner_title = (Spinner)findViewById(R.id.spinner_title);
		edit_title  = (EditText)findViewById(R.id.editText_title);
		editText_nike = (EditText)findViewById(R.id.editText_nike);
        //设置默认值
		initBuy();
		spinner_permission = (Spinner)findViewById(R.id.spinner_permission);
		adapter_permission = new ArrayAdapter<String>(this,android.R.layout.simple_gallery_item,PERMISSION_NAMES);
		//设置下拉列表的风格
		adapter_permission.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
		spinner_permission.setAdapter(adapter_permission);
        //添加事件Spinner事件监听  
		spinner_permission.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				if (position == 0){
					initBuy();
				}else{
					initSeller();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		Button button = (Button)findViewById(R.id.button_deal);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AdminUser.this,AdminDeal.class);
				startActivity(intent);
			}
		});
		button = (Button)findViewById(R.id.button_agency);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AdminUser.this,AdminAgency.class);
				startActivity(intent);
			}
		});
	}

	private void initBuy(){
		edit_title.setVisibility(View.INVISIBLE);
		adapter_title = new ArrayAdapter<String>(this,android.R.layout.simple_gallery_item,TITLE_NAME_BUY);
		//设置下拉列表的风格
		adapter_title.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
		spinner_title.setAdapter(adapter_title);
        //添加事件Spinner事件监听  
		spinner_title.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				edit_title.setVisibility(position == 2 ? View.VISIBLE : View.INVISIBLE);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	private void initSeller(){
		edit_title.setVisibility(View.INVISIBLE);
		adapter_title = new ArrayAdapter<String>(this,android.R.layout.simple_gallery_item,TITLE_NAME_SELLER);
		//设置下拉列表的风格
		adapter_title.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
		spinner_title.setAdapter(adapter_title);
        //添加事件Spinner事件监听  
		spinner_title.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				edit_title.setVisibility(position == 3 ? View.VISIBLE : View.INVISIBLE);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	@Override
	public void succ(DataBuffer data) {
		
	}

	@Override
	public void fail(DataBuffer data) {
		
	}
}
