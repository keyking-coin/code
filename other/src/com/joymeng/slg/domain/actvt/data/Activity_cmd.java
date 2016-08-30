package com.joymeng.slg.domain.actvt.data;

import com.joymeng.slg.domain.actvt.DTManager.DataKey;

public class Activity_cmd implements DataKey
{
	String id;
	String activity;
	String events;
	String cmd;
	String args;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getEvents() {
		return events;
	}

	public void setEvents(String events) {
		this.events = events;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	@Override
	public Object key() {
		return id;
	}
}
