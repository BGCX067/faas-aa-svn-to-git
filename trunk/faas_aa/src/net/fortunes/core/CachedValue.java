package net.fortunes.core;

import org.hibernate.tool.hbm2x.StringUtils;

public class CachedValue {
	
	private String relativeId;
	private String relativeName;
	private String code;
	private String pinyin;
	private String name;
	private String orgName;
	public CachedValue( String pinyin,
			String name, String code,String relativeId,String relativeName) {
		this.relativeId = relativeId;
		this.relativeName = relativeName;
		this.code = code;
		this.pinyin = pinyin;
		this.name = name;
	}
	
	public CachedValue(String pinyin,
			String name,String code) {
		this.pinyin = pinyin;
		this.name = name;
		this.code = code;
	}
	
	public CachedValue(String pinyin,
			String name,String code,String orgName) {
		this.pinyin = pinyin;
		this.name = name;
		this.code = code;
		this.orgName = orgName;
	}

	public boolean match(String keyword, boolean matchPinyin) {
		System.out.println(keyword+"        "+pinyin+":"+name+":"+code+":"+relativeId+":"+relativeName);
		if(StringUtils.isEmpty(keyword)){
			return false;
		}
		if(StringUtils.isNotEmpty(name)  && name.indexOf(keyword)  != -1) {
			return true;
		}
		if(StringUtils.isNotEmpty(code)  && code.startsWith(keyword.toUpperCase())){
			return true;
		}
		if(matchPinyin){
			if(StringUtils.isNotEmpty(pinyin)  && pinyin.startsWith(keyword.toUpperCase())){
				return true;
			}
		}
		return false;
	}

	public String getRelativeId() {
		return relativeId;
	}

	public void setRelativeId(String relativeId) {
		this.relativeId = relativeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRelativeName(String relativeName) {
		this.relativeName = relativeName;
	}

	public String getRelativeName() {
		return relativeName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	
	
}
