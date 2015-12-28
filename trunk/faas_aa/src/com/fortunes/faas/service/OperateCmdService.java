package com.fortunes.faas.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.fortunes.core.service.GenericService;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.fortunes.faas.model.Area;
import com.fortunes.faas.model.Device;
import com.fortunes.faas.model.OperateCmd;
import com.fortunes.faas.util.CardHelper;
import com.fortunes.fjdp.admin.model.Employee;
@Component
public class OperateCmdService extends GenericService<OperateCmd> {
	@Resource private DeviceService deviceService;
	/**
	 * 获取未上传的人员信息
	 * @return List<Employee> 可以多个或者一个
	 * @author Leo
	 */
	@SuppressWarnings("unchecked")
	public List<OperateCmd> getOperateCmds(Device device){
		return getDefDao().findByQueryString(
		"select new OperateCmd(o.id,o.content,o.operateType) from OperateCmd as o where o.isUpload = 0 and o.device = ? ",0,30,device);
	}
	
	public void addOperateCmd(String employeeId,Employee employee,String operateType) throws Exception{
		Map<String , String> map = new HashMap<String, String>();
		if(null!=employee&&StringUtils.isNotEmpty(employee.getCard())){
			List<Device> devices = deviceService.getAll();
			for(Device device : devices){
				OperateCmd o = new OperateCmd();
				o.setDevice(device);
				o.setContent(converToContent(operateType,employee));
				o.setOperateType(operateType);
				this.add(o);
			}
		}
	}
	public void createOperateCmd(List<Employee> employees,Device device,String operateType) throws Exception {
		for(Employee employee : employees){
			OperateCmd o = new OperateCmd();
			o.setDevice(device);
			if(employee.getCode().equals("442002011120003")){
				System.out.println(employee.getCode()+"-----------"+employee.getName());
			}
			o.setContent(converToContent(operateType,employee));
			o.setOperateType(operateType);
			this.add(o);
		}
	}
	private String converToContent(String opType,Employee e) throws Exception{
		StringBuffer res = new StringBuffer();
		if("DEL".equals(opType)){
			res.append(":DATA DEL_USER").append(" PIN=").append(e.getCode());
		}
		if("NEW".equals(opType)||"UPDATE".equals(opType)){
			res.append(":DATA USER")
				.append(" PIN=").append(e.getCode()).append("\tPri=0")
				.append("\tName=").append(e.getName())
				.append("\tCard=[").append(CardHelper.String2RevHex(e.getCard())).append("]")
				.append("\tTZ=0000000000000000\tGrp=1");
		}
		return res.toString();
	}
	public void create(Device device,String operateType) throws Exception{
		OperateCmd o = new OperateCmd();
		if("delAll".equals(operateType)){
			o.setContent("CLEAR DATA");
		}
		o.setDevice(device);o.setOperateType(operateType);
		o.setUpload(false);
		this.add(o);
	}
	
	public void updateAllStatus(){
		JdbcTemplate jdbcTemplate = this.getDefDao().getJdbcTemplate();
		String sql = "update OperateCmd set isUpload = 1 where isUpload = 0";
		jdbcTemplate.execute(sql);
	}
	
	public static void main(String[] args) {
		System.out.println(CardHelper.cardNo2Hex("1468381"));
	}
}
