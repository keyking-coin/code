package com.keyking.admin.frame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.keyking.admin.R;

public class AdminDeal extends BaseActiivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_deal);
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
	}
}
