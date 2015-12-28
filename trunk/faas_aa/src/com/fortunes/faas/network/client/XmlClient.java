package com.fortunes.faas.network.client;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import com.fortunes.faas.network.codec.xml.XmlMessageCodecFactory;

public class XmlClient extends Client{
	
	
	@Override
	protected ProtocolCodecFactory getCodecFactory() {
		return new XmlMessageCodecFactory();
	}
	
}
