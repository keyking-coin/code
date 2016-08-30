package com.joymeng.slg.world;

public class GameResult {
	public static final GameResult RESULT_SUCC = createSuccResult();
	public static final GameResult RESULT_FAIL = createFailResult();
	protected boolean isSucc;
	protected String msg = "";
	protected Object obj;
	public boolean isSucc() {
		return isSucc;
	}
	public boolean isFail() {
		return !isSucc;
	}
	public void setSucc(boolean isSucc) {
		this.isSucc = isSucc;
	}
	public String getMsg() {
		return msg == null ? "" : msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public static GameResult createSuccResult() {
		GameResult result = new GameResult();
		result.isSucc = true;
		return result;
	}
	
	public static GameResult createSuccResult(String msg,Object... objs) {
		GameResult result = new GameResult();
		result.isSucc = true;
		result.msg = msg;
		if (objs != null && objs.length > 0) {
			result.obj = objs[0];
		}
		return result;
	}
	
	public static GameResult createFailResult() {
		GameResult result = new GameResult();
		result.isSucc = false;
		return result;
	}
	
	public static GameResult createFailResult(String msg) {
		GameResult result = new GameResult();
		result.isSucc = false;
		result.msg = msg;
		return result;
	}
	
	
	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	
}

