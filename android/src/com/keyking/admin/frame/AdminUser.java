package com.keyking.admin.frame;

import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.keyking.admin.JsonUtil;
import com.keyking.admin.R;
import com.keyking.admin.StringUtil;
import com.keyking.admin.data.user.PermissionType;
import com.keyking.admin.data.user.UserData;
import com.keyking.admin.net.DataBuffer;
import com.keyking.admin.net.request.NetLogicName;
import com.keyking.admin.net.request.Request;
import com.keyking.admin.net.resp.ResultCallBack;

public class AdminUser extends BaseActiivity implements ResultCallBack {
	static final String[] TITLE_NAME_BUY = {"买家会员","高级买家","其他头衔"};
	static final String[] TITLE_NAME_SELLER = {"普通卖家","知名邮商","金牌经纪人","其他头衔"};
	static final String[] PERMISSION_NAMES = {"买家","卖家"};
    Spinner spinner_title;
    Spinner spinner_permission;
    EditText edit_title;
    EditText editText_nike;
    SearchView user_search;
    UserData target;
    LinearLayout other_title;
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_user);
		user_search  = (SearchView)findViewById(R.id.user_search);
		spinner_title = (Spinner)findViewById(R.id.spinner_title);
		edit_title  = (EditText)findViewById(R.id.editText_title);
		editText_nike = (EditText)findViewById(R.id.editText_nike);
		other_title =  (LinearLayout)findViewById(R.id.userLayout6);
		//设置搜索逻辑
		user_search.setQueryHint("用户手机号");
		user_search.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Request request = new Request(NetLogicName.user_search.getKey());
				request.add(query);
				if (net.send(request,AdminUser.this)){
					showLoading(AdminUser.this,"登录");
					return true;
				}
				return false;
			}
			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
        //设置默认值
		initBuy();
		spinner_permission = (Spinner)findViewById(R.id.spinner_permission);
		ArrayAdapter<String> adapter_permission = new ArrayAdapter<String>(this,android.R.layout.simple_gallery_item,PERMISSION_NAMES);
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
		button = (Button)findViewById(R.id.button_user_update);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String title = spinner_title.getSelectedItem().toString();
				if (title.equals("其他头衔")){
					title = edit_title.getText().toString();
				}
				String nikeName = editText_nike.getText().toString();
				if (StringUtil.isNull(nikeName)){
					showTips(AdminUser.this,"请输入新的昵称");
					return;
				}
				Request request = new Request(NetLogicName.user_commit.getKey());
				UserData temp = new UserData();
				temp.copy(target);
				temp.getPerission().setType(spinner_permission.getSelectedItemPosition() == 0 ? PermissionType.buyer:PermissionType.seller);
				temp.setTitle(title);
				temp.setNikeName(nikeName);
				String str = JsonUtil.ObjectToJsonString(temp);
				request.add(str);
				if (net.send(request,AdminUser.this)){
					showLoading(AdminUser.this,"修改");
				}
			}
		});
		
		uiHandler = new Handler(){
			@Override
            public void handleMessage(Message msg) {
                switch(msg.what){
	                case 1:
	                	updateData();
	                	dispearLoading();
	                	break;
	                case 2:
	                	dispearLoading();
	                	showTips(AdminUser.this,"修改成功");
	                	break;
                }
            }
		};
	}

	private void initBuy(){
		other_title.setVisibility(View.INVISIBLE);
		ArrayAdapter<String> adapter_title = new ArrayAdapter<String>(this,android.R.layout.simple_gallery_item,TITLE_NAME_BUY);
		//设置下拉列表的风格
		adapter_title.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
		spinner_title.setAdapter(adapter_title);
        //添加事件Spinner事件监听  
		spinner_title.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				other_title.setVisibility(position == 2 ? View.VISIBLE : View.INVISIBLE);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		_updateData();
	}
	
	private void initSeller(){
		other_title.setVisibility(View.INVISIBLE);
		ArrayAdapter<String> adapter_title = new ArrayAdapter<String>(this,android.R.layout.simple_gallery_item,TITLE_NAME_SELLER);
		//设置下拉列表的风格
		adapter_title.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
		spinner_title.setAdapter(adapter_title);
        //添加事件Spinner事件监听  
		spinner_title.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				other_title.setVisibility(position == 3 ? View.VISIBLE : View.INVISIBLE);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		_updateData();
	}
	
	private void _updateData(){
		if (target == null){
			return;
		}
		String preStr = spinner_permission.getSelectedItem().toString();
		String[] items = null;
		if (preStr.equals(PERMISSION_NAMES[0])){
			items = TITLE_NAME_BUY;
		}else{
			items = TITLE_NAME_SELLER;
		}
		int select = -1;
		for (int i = 0 ; i < items.length -1 ; i++){
			if (target.getTitle().equals(items[i])){
				select = i;
				break;
			}
		}
		if (select >= 0){
			spinner_title.setSelection(select);
		}else{
			spinner_title.setSelection(items.length-1);
			edit_title.setText(target.getTitle());
		}
		editText_nike.setText(target.getNikeName());
	}
	
	private void updateData(){
		if (target == null){
			return;
		}
		if (target.getPerission().getType() == PermissionType.buyer){
			spinner_permission.setSelection(0);
			initBuy();
		}else if (target.getPerission().getType() == PermissionType.seller){
			spinner_permission.setSelection(1);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void succ(String logicName,DataBuffer data) {
		String str = data.getUTF();
		Map<String,Object> temp = (Map<String,Object>)JSON.parse(str);
    	target = JsonUtil.JsonToObject(temp.get("user").toString(),UserData.class);
		if (logicName.equals(NetLogicName.user_search.getKey())){
			Message message = new Message();
            message.what = 1;
            uiHandler.sendMessage(message);
		}else{
			Message message = new Message();
            message.what = 2;
            uiHandler.sendMessage(message);
		}
	}

	@Override
	public void fail(String logicName,DataBuffer data) {
		_fail(data);
	}
}
