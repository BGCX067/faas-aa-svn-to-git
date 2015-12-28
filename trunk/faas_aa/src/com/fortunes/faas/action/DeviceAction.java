package com.fortunes.faas.action;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.sf.json.JSONObject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.Device;
import com.fortunes.faas.service.AreaService;
import com.fortunes.faas.service.DeviceService;
import com.fortunes.faas.service.OperateCmdService;
import com.fortunes.faas.udp.ServerToDeviceUDP;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.service.EmployeeService;

@Component @Scope("prototype")
public class DeviceAction extends GenericAction<Device> {
	@Resource private DeviceService deviceService;
	@Resource private EmployeeService employeeService;
	@Resource private OperateCmdService operateCmdService;
	
	public String uploadEmployees() throws Exception {
		Device device = deviceService.get(id);
//		if(null==device.getArea()){
//			setJsonMessage(true, "该设备没有关联区域");
//			return render(jo);
//		}
		if(device.getStatus()==0){
			setJsonMessage(true, "该设备处于离线状态，请接入服务器后再上传");
			return render(jo);
		}
		List<Employee> employees = employeeService.getUnUploadEmployees();
		try {
			operateCmdService.createOperateCmd(employees,device,"NEW");
			ServerToDeviceUDP.sendCmd(device.getIp(), "INFO");
			setJsonMessage(true, "操作成功");
		} catch (Exception e) {
			setJsonMessage(true, e.getMessage());
			e.printStackTrace();
			setJsonMessage(true, "操作失败");
		}
		return render(jo);
	}
	
	protected void setEntity(Device e) throws ParseException{
		e.setIp(p("ip"));
		e.setModel(p("model"));
		e.setCode(p("code"));
		e.setLocation(p("location"));
//		e.setArea(areaService.getArea());
	}
	
	protected JSONObject toJsonObject(Device e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
//		record.put("area", e.getArea());
		record.put("ip", e.getIp());
		record.put("code", e.getCode());
		record.put("port", e.getPort());
		record.put("model", e.getModel());
		record.put("location", e.getLocation());
		record.put("employeeCount", e.getEmployeeCount());
		record.put("checkCount", e.getCheckCount());
		record.put("lastActiveTime", e.getLastActiveTime());
		record.put("status", e.getStatus());
		return record.getJsonObject();
	}
	
	
	/*=============== setter and getter =================*/
	
	@Override
	public GenericService<Device> getDefService() {
		return deviceService;
	}
}
