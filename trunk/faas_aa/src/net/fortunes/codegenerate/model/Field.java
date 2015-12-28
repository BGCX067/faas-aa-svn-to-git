package net.fortunes.codegenerate.model;

import net.fortunes.core.Model;

public class Field extends Model{

	private String type;//类型
	private String name;//变量名
	private String label;//标签
	private String extend;//kind(dict),(length)textArea,(dateType)date
	private boolean allowEmpty;
	
	public Field() {
		// TODO Auto-generated constructor stub
	}
	
	public Field(String type, String name, String label,boolean allowEmpty, String extend) {
		this.type = type;
		this.name = name;
		this.label = label;
		this.allowEmpty = allowEmpty;
		this.extend = extend;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	public String getExtend() {
		return extend;
	}

	public void setAllowEmpty(boolean allowEmpty) {
		this.allowEmpty = allowEmpty;
	}

	public boolean isAllowEmpty() {
		return allowEmpty;
	}
	
}
