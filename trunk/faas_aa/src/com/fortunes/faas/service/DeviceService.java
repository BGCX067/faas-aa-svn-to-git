package com.fortunes.faas.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.fortunes.faas.model.AttendanceLog;
import com.fortunes.faas.model.Device;
import com.fortunes.fjdp.Constants;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.service.EmployeeService;

import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;

import org.hibernate.criterion.Order;
import org.springframework.stereotype.Component;

@Component
public class DeviceService extends GenericService<Device> {
	
	@Resource private EmployeeService employeeService;
	@Resource private AttendanceLogService attendanceLogService;
	
	public void updateLastActiveTime(Device entity) throws Exception {
		this.getDefDao().getHibernateTemplate().refresh(entity);
		if(null!=entity){
			entity.setLastActiveTime(new Date(System.currentTimeMillis()));
			entity.setStatus(Constants.DEVICE_STATUS_ONLINE);
			this.update(entity);
		}
	}
	

	/**
	 * 查询未分配区域的设备
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Device> getDevicesUnAssign() {
		return getDefDao().findByQueryString(
				"select e from Device as d left join d.area as a where a.id is null");
	}
	@SuppressWarnings("unchecked")
	public Device getDevice(String code){
		List<Device> list = getDefDao().findByQueryString(
			"select d from Device as d where d.code = '"+code+"'");
		return list.size()>0?list.get(0):null;
	}
	
	public void refressDeviceStatus() throws Exception {
		List<Device> devices = this.getAll();
		for(Device device : devices){
			Date lastActiveTime = device.getLastActiveTime();
			long last = lastActiveTime.getTime()/1000;
			long current = System.currentTimeMillis()/1000;
			if(last+200<current){
				device.setStatus(Constants.DEVICE_STATUS_OFFLINE);
			}else{
				device.setStatus(Constants.DEVICE_STATUS_ONLINE);
			}
			this.update(device);
		}
	}

	public void uploadAttendanceLogs(String raw, Device device) throws Exception {
		String[] raws = raw.split("\n");
		for(int i = 0;i<raws.length;i++){
			String r = raws[i];
			if(r.contains("USER")||r.contains("OPLOG")){
				continue;
			}
			AttendanceLog a = new AttendanceLog();
			String[] record = r.split("\t");
			Employee e = employeeService.getEmployeeEmpty(record[0]);
			if(null==e){
				continue;
			}
			Date date = Tools.string2Date(record[1]);
			a.setCheckInTime(date);
			a.setAttDate(date);
			a.setEmployee(e);
			a.setPushSite(device.getLocation());
			attendanceLogService.add(a);
		}
		
		device.setLastActiveTime(new Date());
		device.setCheckCount(device.getCheckCount()+raws.length);
		device.setStatus(Constants.DEVICE_STATUS_ONLINE);
		this.update(device);//更新设备信息
		
	}
}
