package com.fortunes.faas.model;
/**
 * 
 * @author Leo
 * @version 2011-4-6
 */
public enum LeaveType {
	LEAVE("事假"),//事假
	SICK_LEAVE("病假"),//病假
	ANNUAL_LEAVE("年休假"),//年休假
	HOME_LEAVE("探亲假"),//探亲假
	NURSE_LEAVE("福利假_看护假"),//看护假
	MARRIAGE_LEAVE("婚假"),//看护假
	FUNERAL_LEAVE("丧假"),//看护假
	MATERNITY_LEAVE("产假"),//产假
	FEED_LEAVE("哺育假"),//哺育假
	LOOKAFTER_LEAVE("生育假_看护假"),
	FAMILYPLANNING_LEAVE("计划生育假"),//计划生育假
	INJURY_LEAVE("工伤假"),
	/////2012-09-24//////////////////////////
	TAKECARE_LEAVE("看护假");//看护假
	
	private String label;
	
	private LeaveType(String label) {
		this.label = label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
