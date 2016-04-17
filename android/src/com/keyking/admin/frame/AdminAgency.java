package com.keyking.admin.frame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keyking.admin.R;
import com.keyking.admin.data.DataManager;
import com.keyking.admin.data.deal.Deal;
import com.keyking.admin.data.deal.Order;
import com.keyking.admin.net.DataBuffer;
import com.keyking.admin.net.request.NetLogicName;
import com.keyking.admin.net.request.Request;
import com.keyking.admin.net.resp.ResultCallBack;

@SuppressLint("HandlerLeak")
public class AdminAgency extends BaseActiivity implements ResultCallBack{
	
	int cursor;//光标
	LinearLayout container;
	TextView show_id;
	TextView show_page;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_agency);
		container =  (LinearLayout)findViewById(R.id.agency_container);
		show_id = (TextView)findViewById(R.id.agency_id);
		show_page = (TextView)findViewById(R.id.agency_page);
		Button button = (Button)findViewById(R.id.agency_button_user);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AdminAgency.this,AdminUser.class);
				startActivity(intent);
			}
		});
		button = (Button)findViewById(R.id.agency_button_deal);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AdminAgency.this,AdminDeal.class);
				startActivity(intent);
			}
		});
		button = (Button)findViewById(R.id.agency_button_left);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cursor > 0){
					cursor --;
					refresh();
				}
			}
		});
		button = (Button)findViewById(R.id.agency_button_right);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cursor < DataManager.getInstance().getDeals().size() -1){
					cursor ++;
					refresh();
				}
			}
		});
		button = (Button)findViewById(R.id.asvt2);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Request request = new Request(NetLogicName.agency_commit.getKey());
				Deal deal = DataManager.getInstance().getDeals().get(cursor);
				byte index   = 2;
				request.add(deal.getId());
				request.add(deal.getOrders().get(0).getId());
				request.add(index);
				if (net.send(request,AdminAgency.this)){
					showLoading(AdminAgency.this,"提交");
				}
			}
		});
		button = (Button)findViewById(R.id.asvt5);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Request request = new Request(NetLogicName.agency_commit.getKey());
				Deal deal = DataManager.getInstance().getDeals().get(cursor);
				byte index   = 5;
				request.add(deal.getId());
				request.add(deal.getOrders().get(0).getId());
				request.add(index);
				if (net.send(request,AdminAgency.this)){
					showLoading(AdminAgency.this,"提交");
				}
			}
		});
		uiHandler = new Handler(){
			@Override
            public void handleMessage(Message msg) {
				dispearLoading();
            	if (cursor > 0){
            		cursor --;
            	}
            	refresh();
            	showTips(AdminAgency.this,"修改成功");
            }
		};
		refresh();
	}
	
	private void refresh(){
		if (DataManager.getInstance().getDeals().size() == 0){
			container.setVisibility(View.INVISIBLE);
		}else{
			container.setVisibility(View.VISIBLE);
			Deal deal = null;
			try {
				deal = DataManager.getInstance().getDeals().get(cursor);
			} catch (Exception e) {
				
			}
			Order order = deal.getOrders().get(0);
			show_id.setText("订单编号:" + order.getId());
			show_page.setText((cursor + 1) + "/" + DataManager.getInstance().getDeals().size());
			if (order.getState() == 1){
				findViewById(R.id.asvt1).setVisibility(View.VISIBLE);
				findViewById(R.id.asvt2).setVisibility(View.VISIBLE);
				findViewById(R.id.agency_s_line2).setVisibility(View.INVISIBLE);
				findViewById(R.id.asvt3).setVisibility(View.INVISIBLE);
				findViewById(R.id.agency_s_line3).setVisibility(View.INVISIBLE);
				findViewById(R.id.asvt4).setVisibility(View.INVISIBLE);
				findViewById(R.id.agency_s_line4).setVisibility(View.INVISIBLE);
				findViewById(R.id.asvt5).setVisibility(View.INVISIBLE);
			}else{
				findViewById(R.id.asvt1).setVisibility(View.VISIBLE);
				findViewById(R.id.asvt2).setVisibility(View.VISIBLE);
				findViewById(R.id.agency_s_line2).setVisibility(View.VISIBLE);
				findViewById(R.id.asvt3).setVisibility(View.VISIBLE);
				findViewById(R.id.agency_s_line3).setVisibility(View.VISIBLE);
				findViewById(R.id.asvt4).setVisibility(View.VISIBLE);
				findViewById(R.id.agency_s_line4).setVisibility(View.VISIBLE);
				findViewById(R.id.asvt5).setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void succ(String logicName, DataBuffer data) {
		Message message = new Message();
        uiHandler.sendMessage(message);
	}

	@Override
	public void fail(String logicName, DataBuffer data) {
		_fail(data);
	}
}
