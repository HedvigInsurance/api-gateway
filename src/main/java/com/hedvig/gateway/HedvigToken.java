package com.hedvig.gateway;

public class HedvigToken {

	private String token;
	
	public HedvigToken(){
		int n = (int) (100000d + Math.random() * 900000d);
		this.token = new Integer(n).toString();
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public String toString(){
		return this.token;
	}

}
