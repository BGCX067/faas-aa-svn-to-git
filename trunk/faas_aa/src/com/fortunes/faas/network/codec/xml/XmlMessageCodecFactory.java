package com.fortunes.faas.network.codec.xml;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class XmlMessageCodecFactory implements ProtocolCodecFactory {

	private ProtocolEncoder encoder;
    private ProtocolDecoder decoder;
    
    public XmlMessageCodecFactory() {
    	encoder = new XmlMessageEncoder();
        decoder = new XmlMessageDecoder();
	}
	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

}
