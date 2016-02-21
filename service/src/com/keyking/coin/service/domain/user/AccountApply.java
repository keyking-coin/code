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
	boolean completed;
	
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

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}
