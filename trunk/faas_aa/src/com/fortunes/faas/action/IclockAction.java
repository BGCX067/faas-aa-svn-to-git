package com.fortunes.faas.action;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import net.fortunes.core.action.BaseAction;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.fortunes.faas.model.Device;
import com.fortunes.faas.model.OperateCmd;
import com.fortunes.faas.service.AreaService;
import com.fortunes.faas.service.AttendanceLogService;
import com.fortunes.faas.service.DeviceService;
import com.fortunes.faas.service.OperateCmdService;
import com.fortunes.fjdp.Constants;
import com.fortunes.fjdp.admin.service.EmployeeService;

@SuppressWarnings("serial")
@Component @Scope("prototype")
public class IclockAction extends BaseAction {
	
	@Resource EmployeeService employeeService;
	@Resource AttendanceLogService attendanceLogService;
	@Resource DeviceService deviceService;
	@Resource OperateCmdService operateCmdService;
	@Resource AreaService areaService;
	
	//设备有新记录时发送请求过来
	public String cdata() throws Exception{
		
		if(request.getMethod().equalsIgnoreCase("POST")){
			return uploadData();
		}else{
			return pushConfig(); 
		}
		
	}
//	public String fdata()throws Exception{
//		System.out.println("fdate来了");
//		return render("OK","text/plain");
//	}
	
	private String pushConfig() throws Exception {
		String sn = p("SN");
		Device device = deviceService.getDevice(sn);
		String ret = 
			"\r\n" +
			"GET OPTION FROM: "+sn+"\r\n"+
			"Stamp="+(null!=device?device.getLastStamp():0)+"\r\n"+
			"OpStamp="+(null!=device?device.getLastOpStamp():0)+"\r\n"+
			"PhotoStamp=0\r\n"+
			"ErrorDelay=60\r\n"+
			"Delay=200\r\n"+
			"TransTimes=00:00;08:49\r\n"+
			"TransInterval=1\r\n"+
			"TransFlag=1001000000\r\n"+
			"Realtime=1\r\n"+
			"Encrypt=0\r\n";
		
		return render(ret,"text/plain");
		
	}

	private String uploadData() throws Exception {
		String sn = p("SN");
		Device device = deviceService.getDevice(sn);
		//验证此考勤机是否关联有区域，没关联有区域则认为是不合法的考勤机不能上传记录
		if(null==device){
			return render("OK","text/plain");
		}
		//更新设备时间戳
		device.setLastStamp(StringUtils.isNotEmpty(p("Stamp"))?
				Integer.parseInt(p("Stamp")):device.getLastStamp());
		device.setLastOpStamp(StringUtils.isNotEmpty(p("OpStamp"))?
				Integer.parseInt(p("OpStamp")):device.getLastOpStamp());
		//上传考勤记录
		ServletInputStream in = request.getInputStream();
		System.out.println(in);
		byte[] bytes = new byte[request.getContentLength()];
		in.read(bytes);
		System.out.println(bytes);
		String raw = new String(bytes);
		System.out.print(raw);
		deviceService.uploadAttendanceLogs(raw,device);
		
		return render("OK","text/plain");
	}

	public String getrequest() throws Exception{
		System.out.println("----------------getrequest---------------- "+new Date());
		String sn = p("SN");
		String info = p("INFO");
		System.out.println(sn+"-----"+info);
		//新接设备，存储设备信息到数据库
		saveDeviceInfo(sn,info);
		Device device = deviceService.getDevice(sn);
		if(null!=device)
			deviceService.updateLastActiveTime(device);//更新设备最后活动时间
		if(null==device){
			return render("OK","text/plain");
		}
		//获取数据库表操作命令
		List<OperateCmd> list = operateCmdService.getOperateCmds(device);
		StringBuffer res = new StringBuffer();
		int i = 0;
		for(OperateCmd o:list){
			if(i==list.size()||i!=0){
				res.append("\n");
			}
			String content = new String(o.getContent().getBytes("gb2312"),"gb2312");
			res.append("C:").append(o.getId()).append(content);
			if(i==list.size()-1){
				res.append("\n");
			}
			i++;
		}
		String ret = list.size()>0?res.toString():"OK";
		logger.info(ret);
		return render(ret,"text/plain;charset=gb2312");
	}
	
	public String devicecmd() throws Exception{
		String id = p("ID");
		logger.info(id);
//		String sn = p("SN");
		String cmd = p("CMD");
		ServletInputStream in = request.getInputStream();
		byte[] bytes = new byte[request.getContentLength()];
		in.read(bytes);
		in.close();
		String raw = new String(bytes);		
		System.out.println(raw);
		String[] raws = raw.split("\n");
		logger.info(raw);
		for(String r : raws){
			String Return = "";
			try {
				String[] colmuns = r.split("&");
				id = colmuns[0].split("=")[1];
				Return = colmuns[1].split("=")[1];
				cmd = colmuns[2].split("=")[1];
			} catch (NullPointerException e) {
				return render("OK","text/plain");
			}catch(ArrayIndexOutOfBoundsException a){
				return render("OK","text/plain");
			}
//			System.out.println("-----设备编号为："+sn+"--执行命令号为："+id+"--操作类型："+cmd);
			if("DATA".equals(cmd)&&"0".equals(Return)){
				OperateCmd o = operateCmdService.get(id);
				if(null!=o){
					o.setUpload(true);
					operateCmdService.update(o);
				}
			}
		}		
		return render("OK","text/plain");
	} 
	
	/**
	 * 存储设备信息到数据库
	 * @param sn
	 * @param info
	 * @throws Exception
	 */
	private void saveDeviceInfo(String sn,String info) throws Exception {
		if(StringUtils.isNotEmpty(info)){
			Device device = deviceService.getDevice(sn);
			if(null==device){
				device = new Device();
			}
			device.setCode(sn);
			System.out.println(info);
			String[] infos = info.split(",");
			int len = infos.length;
			device.setIp(infos[len-5]);
//			device.setArea(areaService.getArea());
			device.setEmployeeCount(Integer.parseInt(infos[len-8]));
			device.setCheckCount(Integer.parseInt(infos[len-6]));
			device.setPort("4370");
			device.setModel("M880");
			device.setLastActiveTime(new Date());
			device.setStatus(Constants.DEVICE_STATUS_ONLINE);
			deviceService.addOrUpdate(device);
		}
	}
}