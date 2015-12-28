package com.fortunes.faas.network.server;

import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fortunes.faas.network.model.xml.XmlEntity;
import com.fortunes.faas.network.model.xml.XmlMessage;


public class XmlMessageHandler extends IoHandlerAdapter {
	
	final Logger logger = LoggerFactory.getLogger("ROOT");
	
	private BusinessService businessService;
	
	

	@Override
	public void messageReceived(IoSession session, Object o){
		XmlMessage requestMessage = (XmlMessage)o;
		
		XmlEntity requestXml = XmlEntity.parse(requestMessage.getContents());
		logger.info(session.getRemoteAddress().toString());
		requestXml.setRemoteAddress((InetSocketAddress)session.getRemoteAddress());
		
		int tranCode = requestXml.getTransCode();
		
		XmlEntity responseXml = null;
		
		try {
			switch (tranCode) {

			}
		} catch (Exception e) {
			logger.info("程序执行异常", e);
			responseXml = XmlEntity.create().createDefaultResponse(requestXml);
			responseXml.setResponseCodeAndMsg("000001", "请检查输入数据，稍后再试");
		}
		session.write(XmlMessage.createDefaultMessage(responseXml.buildAsBytes()));
	}
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("sessionCreated！");
	}
	
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("sessionOpened！");
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.info("{}通信出现异常{}",session.getRemoteAddress(),cause);
		session.close(true);
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("连接已关闭！{}", session.getRemoteAddress());
		session.close(false);
	}

	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}

	public BusinessService getBusinessService() {
		return businessService;
	}
}
