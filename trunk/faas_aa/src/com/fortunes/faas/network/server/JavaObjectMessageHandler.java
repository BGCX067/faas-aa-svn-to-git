package com.fortunes.faas.network.server;

import javax.annotation.Resource;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.fortunes.faas.network.model.MessageObject;

/**
 * 
 * @author Leo
 * @version 2011-4-12
 */
public class JavaObjectMessageHandler extends IoHandlerAdapter{
	
	public static final int SYNC_EMPLOYEE = 1001;//同步员工
	public static final int SYNC_ORGANIZATION = 1002;//同步员工
	public static final int SYNC_ATTSHIFT = 1003;//同步考勤信息
	public static final int SYNC_MEET = 2004;//上传移动考勤信息
	public static final int SYNC_ATT = 2005;
	
	@Resource BusinessService businessService;
	
	@SuppressWarnings("unchecked")
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		MessageObject reqMsg = (MessageObject)message;
		String code = reqMsg.getReqCode();
		int reqCode = Integer.parseInt(code);
		MessageObject res = null;
		try {
			switch (reqCode) {
				case SYNC_EMPLOYEE:{
					//获取服务器所有员工发送到移动客户端
					res = businessService.syncEmp(session,message);
					break;
				}case SYNC_ORGANIZATION:{
					//获取服务器所有组织机构发送到移动客户端
					res = businessService.syncOrganization(session,message);
					break;
				}case SYNC_ATTSHIFT:{
					//同步考勤信息
					res = businessService.syncAttShift(session,message);
					break;
				}case SYNC_MEET:{
					res = businessService.syncMeet(session, message);
					break;
				}case SYNC_ATT:{
					res=businessService.syncAtt(session, message);
				}
				default:{
					break;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.write(res);
	}
}
