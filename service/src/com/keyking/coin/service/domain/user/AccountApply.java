package com.keyking.coin.service.domain.user;

public class AccountApply {
	long id ;
	String bourse;
	String bankName;
	String tel;
	String email;
	String indentFront;
	String indentBack;
	String bankFront;
	byte state;//0未处理;1完成;2未通过
	String reason;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getBourse() {
		return bourse;
	}
	
	public void setBourse(String bourse) {
		this.bourse = bourse;
	}
	
	public String getBankName() {
		return bankName;
	}
	
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	public String getTel() {
		return tel;
	}
	
	public void setTel(String tel) {
		this.tel = tel;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	

	public String getIndentFront() {
		return indentFront;
	}

	public void setIndentFront(String indentFront) {
		this.indentFront = indentFront;
	}

	public String getIndentBack() {
		return indentBack;
	}

	public void setIndentBack(String indentBack) {
		this.indentBack = indentBack;
	}

	public String getBankFront() {
		return bankFront;
	}
	
	public void setBankFront(String bankFront) {
		this.bankFront = bankFront;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
}
