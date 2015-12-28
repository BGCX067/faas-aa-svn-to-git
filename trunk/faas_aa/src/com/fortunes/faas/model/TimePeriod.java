package com.fortunes.faas.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fortunes.fjdp.admin.model.Dict;
import net.fortunes.core.Model;

@Entity
public class TimePeriod extends Model{
	
	@Id @GeneratedValue
	private long id;
	
	@Temporal(TemporalType.TIME)
	private Date startTime;//开始日期
	
	@Temporal(TemporalType.TIME)
	private Date endTime;//结束日期
	
	private int toleranceStartMinute;//允许偏差checkIn时间
	
	private int toleranceEndMinute;//允许偏差checkOut时间
	
	
    public TimePeriod() {
    }
    
    public TimePeriod(long id) {
    	this.id = id;
    }
    
    @Override
	public String toString() {
		return "";
	}
    
    /*=============== setter and getter =================*/
    
    public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return startTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getEndTime() {
		return endTime;
	}
	public void setToleranceStartMinute(int toleranceStartMinute) {
		this.toleranceStartMinute = toleranceStartMinute;
	}

	public int getToleranceStartMinute() {
		return toleranceStartMinute;
	}
	public void setToleranceEndMinute(int toleranceEndMinute) {
		this.toleranceEndMinute = toleranceEndMinute;
	}

	public int getToleranceEndMinute() {
		return toleranceEndMinute;
	}

}
