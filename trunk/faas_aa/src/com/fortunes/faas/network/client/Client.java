package com.fortunes.faas.network.client; 

import java.net.InetSocketAddress;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fortunes.faas.network.model.Message;

public abstract class Client extends IoHandlerAdapter{
	
	final Logger logger = LoggerFactory.getLogger(Client.class);
	
	public static final int CONNECT_TIMEOUT = 3000;
	public static final int READ_TIMEOUT = 5000;
	
	private InetSocketAddress serverAddress;
	private SocketConnector connector;
	
	protected abstract ProtocolCodecFactory getCodecFactory();
	
	public Client() {
		connector = new NioSocketConnector();
		connector.getSessionConfig().setTcpNoDelay(true);
		getConnector().getFilterChain().addLast("codec",
				new ProtocolCodecFilter(getCodecFactory()));
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.setHandler(this);
	}
	
	public Object sendRequest(IoSession session,Message message){
		WriteFuture writeFuture = session.write(message);
		writeFuture.awaitUninterruptibly();
		
		ReadFuture readFuture = session.read();
		readFuture.awaitUninterruptibly(READ_TIMEOUT);
		
		return readFuture.getMessage();
	}
	
	public IoSession connect(InetSocketAddress serverAddress){
		setServerAddress(serverAddress);
		return connect();
	}

	public IoSession connect() {
		ConnectFuture connectFuture = getConnector().connect(getServerAddress());
		connectFuture.awaitUninterruptibly(CONNECT_TIMEOUT);
		try {
			IoSession session = connectFuture.getSession();
			session.getConfig().setUseReadOperation(true);
			logger.info("成功连接至{},本地地址:{}",session.getRemoteAddress(),session.getLocalAddress());
			return session;
		} catch (RuntimeIoException e) {
			logger.info("连接失败",e);
			
			//TODO 重新初始化
			//logger.info("正在尝试进立新的连接",e);
			//new ClientService().init();
			return null;
		}
	}
	
	public void sendRequestNoResponse(IoSession session,Message message){
		session.write(message);
	}
	
	public void close(IoSession session){
		if(isConnected(session)){
			session.close(false);
			logger.info("客户端关闭了连接\n");
		}
	}
	
	public boolean isConnected(IoSession session) {
		return (session != null && session.isConnected());
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
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		//logger.info("收到来自"+session.getRemoteAddress()+"的消息:\n{} - 本地端口:{}",message,session.getLocalAddress());
	}
	
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		//logger.info("发送至"+session.getRemoteAddress()+"的消息:\n{}",message);
		logger.info("消息已发送！");
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("连接至{}的连接被关闭！- 本地端口:{}",session.getRemoteAddress(),session.getLocalAddress());
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		session.close(true);
		logger.info("{}通信出现异常{}",session.getRemoteAddress(),cause);
	}

	public void setConnector(SocketConnector connector) {
		this.connector = connector;
	}

	public SocketConnector getConnector() {
		return connector;
	}

	public void setServerAddress(InetSocketAddress serverAddress) {
		this.serverAddress = serverAddress;
	}

	public InetSocketAddress getServerAddress() {
		return serverAddress;
	}
}
