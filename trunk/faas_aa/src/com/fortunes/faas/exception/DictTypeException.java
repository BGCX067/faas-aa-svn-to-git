package com.fortunes.faas.exception;

@SuppressWarnings("serial")
public class DictTypeException extends Exception {
	
	private String msg = "";
	
	public DictTypeException(String msg){
		this.msg = msg;
	}

	@Override
	public String toString() {
		return msg;
	}
}
