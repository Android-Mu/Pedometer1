package com.click369.pedometer.data.javabean;

public class Step {

	private int id;
	private int number;
	private int mubiao;
	private String date;
	private String userId;
	
	

	public int getMubiao() {
		return mubiao;
	}

	public void setMubiao(int mubiao) {
		this.mubiao = mubiao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
