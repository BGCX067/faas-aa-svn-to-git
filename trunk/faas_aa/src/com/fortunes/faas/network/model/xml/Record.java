package com.fortunes.faas.network.model.xml;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;

public class Record extends LinkedHashMap<String, String>{
	
	private XmlEntity xmlEntity;
	
	public Record() {
	}
	
	public Record(XmlEntity xmlEntity) {
		this.xmlEntity = xmlEntity;
	}

	public static Record create(XmlEntity xmlEntity){
		Record r = new Record(xmlEntity);
		return r;
	}
	
	public Record setRecordItem(String key,String value){
		this.put(key, value);
		return this;
	}
	
	public Record setRecordItem(String key,int value){
		this.put(key, ""+value);
		return this;
	}
	
	public Record setRecordItem(String key,Integer value){
		this.put(key, ""+value);
		return this;
	}
	
	public Record setRecordItem(String key,double value){
		this.put(key, ""+new DecimalFormat("0.00").format(value));
		return this;
	}
	
	public Record setRecordItem(String key,long value){
		this.put(key, ""+new DecimalFormat("0").format(value));
		return this;
	}
	
	public Record setRecordItem(String key,Long value){
		this.put(key, ""+new DecimalFormat("0").format(value));
		return this;
	}
	
	public Record setRecordItem(String key,Double value){
		this.put(key, ""+value);
		return this;
	}
	
	public long getLong(String key){
		return Long.parseLong(this.get(key));
	}
	
	public long getInt(String key){
		return Integer.parseInt(this.get(key));
	}
	
	public double getDouble(String key){
		return Double.parseDouble(this.get(key));
	}
	
	public XmlEntity buildRecord() {
		this.getXmlEntity().getRecordList().add(this);
		return this.getXmlEntity();
		
	}

	public void setXmlEntity(XmlEntity xmlEntity) {
		this.xmlEntity = xmlEntity;
	}

	public XmlEntity getXmlEntity() {
		return xmlEntity;
	}
}
