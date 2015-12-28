package com.fortunes.faas.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.springframework.stereotype.Component;

@Component
public class ServerToDeviceUDP {
	 public static void sendCmd(String ip,String cmd)throws SocketException,  
	    UnknownHostException{  
	        DatagramSocket ds = new DatagramSocket();// 创建用来发送数据报包的套接字  
	        DatagramPacket dp = new DatagramPacket(cmd.getBytes(),  
	                cmd.getBytes().length,  
	                InetAddress.getByName(ip), 4374);  
	        // 构造数据报包，用来将长度为 length 的包发送到指定主机上的指定端口号  
	        try {  
	            ds.send(dp);  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        ds.close();  
	          
	    }  
}
