package com.keyking.admin.frame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.keyking.admin.R;

public class AdminAgency extends BaseActiivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_agency);
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
	}
}
