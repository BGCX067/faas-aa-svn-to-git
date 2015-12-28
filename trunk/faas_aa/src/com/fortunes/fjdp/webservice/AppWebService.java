package com.fortunes.fjdp.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import org.springframework.stereotype.Service;

@WebService
@Service
public class AppWebService {
	
	@WebMethod
	public String hello(String world){
		return "hello, "+world;
	}
	
	public static void main(String[] args) {
		AppWebService app = new AppWebService();
		Endpoint endpoint = Endpoint.publish("http://locaohost:8080/AppWebService", "陈本明");
	}
	
}


