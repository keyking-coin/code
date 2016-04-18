package com.keyking.admin.frame;

import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.keyking.admin.JsonUtil;
import com.keyking.admin.R;
import com.keyking.admin.StringUtil;
import com.keyking.admin.data.deal.Deal;
import com.keyking.admin.data.deal.Order;
import com.keyking.admin.net.DataBuffer;
import com.keyking.admin.net.request.NetLogicName;
import com.keyking.admin.net.request.Request;
import com.keyking.admin.net.resp.ResultCallBack;

public class AdminDeal extends BaseActiivity implements ResultCallBack{
	View preSelect = null;
	View container_deal = null;
	View container_order = null;
	SearchView deal_search;
	Deal target;
	Button lockAction;
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_deal);
		deal_search = (SearchView)findViewById(R.id.deal_search);
		deal_search.setQueryHint("买卖盘编号");
		deal_search.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				String logicName = NetLogicName.deal_search.getKey();
				if (preSelect == findViewById(R.id.deal_button_cjp)){
					logicName = NetLogicName.order_search.getKey();
				}
				Request request = new Request(logicName);
				request.add(Long.parseLong(query));
				if (net.send(request,AdminDeal.this)){
					showLoading(AdminDeal.this,"查询数据");
					return true;
				}
				return false;
			}
			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		
		Button button = (Button)findViewById(R.id.deal_button_user);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AdminDeal.this,AdminUser.class);
				startActivity(intent);
			}
		});
		button = (Button)findViewById(R.id.deal_button_agency);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AdminDeal.this,AdminAgency.class);
				startActivity(intent);
			}
		});
		container_deal  = findViewById(R.id.deal_container);
		container_order = findViewById(R.id.order_container);
		container_deal.setVisibility(View.VISIBLE);
		container_order.setVisibility(View.GONE);
		button = (Button)findViewById(R.id.deal_button_mmp);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (preSelect != null){
					preSelect.setBackgroundColor(Color.DKGRAY);
				}
				v.setBackgroundColor(Color.GREEN);
				preSelect = v;
				target = null;
				refresh();
			}
		});
		preSelect = button;
		button = (Button)findViewById(R.id.deal_button_cjp);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (preSelect != null){
					preSelect.setBackgroundColor(Color.DKGRAY);
				}
				v.setBackgroundColor(Color.GREEN);
				preSelect = v;
				target = null;
				refresh();
			}
		});
		button.setBackgroundColor(Color.DKGRAY);
		lockAction = (Button)findViewById(R.id.deal_button_lock);
		lockAction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (target == null){
					showTips(AdminDeal.this,"请选搜索数据");
					return;
				}
				if (preSelect == findViewById(R.id.deal_button_cjp)){
					Order order = target.getOrders().get(0);
					if (order.getRevoke() == 3){
						showTips(AdminDeal.this,"已锁定，无需锁定");
						return;
					}
					Request request = new Request(NetLogicName.order_lock.getKey());
					request.add(target.getId());
					request.add(order.getId());
					if (net.send(request,AdminDeal.this)){
						showLoading(AdminDeal.this,"锁定数据");
					}
				}else{
					if (target.isRevoke()){
						showTips(AdminDeal.this,"已锁定，无需锁定");
						return;
					}
					Request request = new Request(NetLogicName.deal_lock.getKey());
					request.add(target.getId());
					if (net.send(request,AdminDeal.this)){
						showLoading(AdminDeal.this,"锁定数据");
					}
				}
			}
		});
		uiHandler = new Handler(){
			@Override
            public void handleMessage(Message msg) {
				dispearLoading();
				refresh();
				if (msg.what == 2){
					showTips(AdminDeal.this,"锁定成功");
				}
            }
		};
		refresh();
	}
	
	private void refresh(){
		if (target == null){
			container_deal.setVisibility(View.GONE);
			container_order.setVisibility(View.GONE);
			lockAction.setText("锁定");
		}else{
			if (preSelect == findViewById(R.id.deal_button_mmp)){
				container_deal.setVisibility(View.VISIBLE);
				container_order.setVisibility(View.GONE);
				TextView label = (TextView)findViewById(R.id.deal_role_name);
				label.setText(target.getIssueName());
				label = (TextView)findViewById(R.id.label_time);
				label.setText(target.getCreateTime());
				label = (TextView)findViewById(R.id.deal_value_id);
				label.setText(target.getId() + "");
				label = (TextView)findViewById(R.id.deal_value_type);
				label.setText(target.getSellFlag() == 0 ? "求购" : "出售");
				label = (TextView)findViewById(R.id.deal_value_help);
				label.setText(target.getHelpFlag() == 0 ? "买家先款" : "平台中介");
				label = (TextView)findViewById(R.id.deal_value_issue);
				label.setText(target.getType() == 0 ? "入库" : "现货");
				label = (TextView)findViewById(R.id.deal_value_bourse);
				label.setText(target.getBourse().split(",")[0]);
				label = (TextView)findViewById(R.id.deal_value_title);
				label.setText(target.getName());
				label = (TextView)findViewById(R.id.deal_value_price);
				label.setText(target.getPrice() + "");
				label = (TextView)findViewById(R.id.deal_value_monad);
				label.setText(target.getMonad());
				label = (TextView)findViewById(R.id.deal_value_num);
				label.setText(target.getNum() + "");
				label = (TextView)findViewById(R.id.deal_value_valid);
				label.setText(target.getValidTime());
				lockAction.setText(target.isRevoke() ? "已锁定" : "锁定");
			}else{
				container_deal.setVisibility(View.GONE);
				container_order.setVisibility(View.VISIBLE);
				TextView label = (TextView)findViewById(R.id.order_issue_name);
				label.setText(target.getIssueName());
				label = (TextView)findViewById(R.id.order_issue_time);
				label.setText(target.getCreateTime());
				label = (TextView)findViewById(R.id.order_issue_id_value);
				label.setText(target.getId() + "");
				label = (TextView)findViewById(R.id.order_issue_sell_value);
				label.setText(target.getSellFlag() == 0 ? "求购" : "出售");
				label = (TextView)findViewById(R.id.order_issue_help_value);
				label.setText(target.getHelpFlag() == 0 ? "买家先款" : "平台中介");
				label = (TextView)findViewById(R.id.order_issue_type_value);
				label.setText(target.getType() == 0 ? "入库" : "现货");
				label = (TextView)findViewById(R.id.order_issue_bourse_value);
				label.setText(target.getBourse().split(",")[0]);
				label = (TextView)findViewById(R.id.order_issue_title_value);
				label.setText(target.getName());
				label = (TextView)findViewById(R.id.order_issue_price_value);
				label.setText(target.getPrice() + "");
				label = (TextView)findViewById(R.id.order_issue_monad_value);
				label.setText(target.getMonad());
				label = (TextView)findViewById(R.id.order_issue_lfet_num_value);
				label.setText(target.getNum() + "");
				label = (TextView)findViewById(R.id.order_issue_valid_value);
				label.setText(target.getValidTime());
				label = (TextView)findViewById(R.id.order_issue_other);
				if (StringUtil.isNull(target.getOther())){
					label.setVisibility(View.GONE);
				}else{
					label.setVisibility(View.VISIBLE);
				}
				label.setText(target.getOther());
				label = (TextView)findViewById(R.id.order_grab_name_value);
				Order order = target.getOrders().get(0);
				label.setText(order.getBuyerName());
				label = (TextView)findViewById(R.id.order_grab_num_value);
				label.setText(order.getNum() + "");
				label = (TextView)findViewById(R.id.order_grab_id_value);
				label.setText(order.getId() + "");
				label = (TextView)findViewById(R.id.order_grab_time_value);
				label.setText(order.getTimes().get(0));
				lockAction.setText(order.getRevoke() == 3 ? "已锁定" : "锁定");
			}
			
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void succ(String logicName, DataBuffer data) {
		String str = data.getUTF();
		Map<String,Object> temp = (Map<String,Object>)JSON.parse(str);
		target = JsonUtil.JsonToObject(temp.get("deal").toString(),Deal.class);
		Message message = new Message();
		if (logicName.equals(NetLogicName.deal_search.getKey()) ||
			logicName.equals(NetLogicName.order_search.getKey()) ){
	        message.what = 1;
		}else{
	        message.what = 2;
		}
		uiHandler.sendMessage(message);
	}

	@Override
	public void fail(String logicName, DataBuffer data) {
		_fail(data);
	}
}
