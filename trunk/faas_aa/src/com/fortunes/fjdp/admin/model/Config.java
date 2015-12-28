package com.fortunes.fjdp.admin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import net.fortunes.core.Model;

@Entity
public class Config extends Model {
	
	public static enum ConfigKey {
		APP_ROOT_DIR("应用根目录"),
		ADMIN_EMAIL("管理员邮箱"),
		TEMP_UPLOAD_DIR("临时上传文件夹(相对目录)"),
		LATE_GREATER_THAN_TIME("迟到超过（分钟）"),
		LATE_GREATER_THAN_TIME_FOR_PARAM("旷工（天）"),
		EARLY_GREATER_THAN_TIME("早退超过（分钟）"),
		EARLY_GREATER_THAN_TIME_FOR_PARAM("旷工（天）"),
		ATTSHIFT_PUSH_CARD_ONLYONE("打卡一次算旷工（天）"),
		ALERT_PERCENT("续缴预警百分比"),
		PHOTO_DIR("人员照片文件夹"),
		FULL_PAY_DEVIATION_PERCENT("缴齐偏差百分比"),
		LATEOREARLY_DEDUCT_BASIC_SALARY_STANDARD("迟到早退扣基本工资标准"),
		ABSENT_DEDUCT_BASIC_SALARY_STANDARD("旷工扣基本工资标准"),
		WORK_TO_MORROW_EARLYEST_AHEAD("工作到次日最早打卡提前(小时)"),
		WORK_TO_MORROW_LATEST_REMIT("工作到次日最迟打卡推迟到(小时)"),
		START_WORK_IN_MORNING_TIME("早上上班时间"),
		OVER_WORK_IN_AFTERNOON_TIME("下午下班时间");
		
		private String label;
		
		private ConfigKey(String label) {
			this.label = label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		
	}
	@Id 
	@GeneratedValue
	private long id;
	
	@Enumerated(EnumType.STRING) @Column(nullable = false)
	private ConfigKey configKey;
	
	@Column(nullable = false)
	private String configValue;
	
	@Column(nullable = false)
	private String configLabel;
	
	@Column(nullable = false)
	private String lastValue;
	
	@Column(nullable = false)
	private String defaultValue;
	
	public Config() {
	}
	
	public Config(ConfigKey key,String value,String lastValue,String defaultValue) {
		this.setConfigKey(key);
		this.configLabel = key.getLabel();
		this.configValue = value;
		this.lastValue = lastValue;
		this.defaultValue = defaultValue;
		
	}

	@Override
	public String toString() {
		return "系统参数:"+getConfigKey();
	}
	
	//================================ setter and getter ====================================
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}


	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

	public void setConfigKey(ConfigKey configKey) {
		this.configKey = configKey;
	}

	public ConfigKey getConfigKey() {
		return configKey;
	}

	public void setLastValue(String lastValue) {
		this.lastValue = lastValue;
	}

	public String getLastValue() {
		return lastValue;
	}

	public void setConfigLabel(String configLabel) {
		this.configLabel = configLabel;
	}

	public String getConfigLabel() {
		return configLabel;
	}
	
}
