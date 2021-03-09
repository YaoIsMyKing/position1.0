package com.example.position.enumvo;

public enum ApiResultStatus {
	SUCCESS(0, "success"),
	ERROR(-1, "error")
	;
	
	private final int code;
	private final String msg;
	
	ApiResultStatus(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public int getCode() {
		return code;
	}
}
