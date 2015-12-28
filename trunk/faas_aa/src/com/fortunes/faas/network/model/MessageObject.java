package com.fortunes.faas.network.model;

import java.util.List;

import net.fortunes.core.Model;

/**
 * 
 * @author Leo
 * @version 2011-4-12
 */
public class MessageObject<E> extends Model {
	
	private String reqCode;
	private List<E> list;
	public MessageObject(String reqCode,List<E> list){
		this.reqCode = reqCode;
		this.list = list;
	}
	public String getReqCode() {
		return reqCode;
	}
	public void setReqCode(String reqCode) {
		this.reqCode = reqCode;
	}
	public List<E> getList() {
		return list;
	}
	public void setList(List<E> list) {
		this.list = list;
	}
	
}
