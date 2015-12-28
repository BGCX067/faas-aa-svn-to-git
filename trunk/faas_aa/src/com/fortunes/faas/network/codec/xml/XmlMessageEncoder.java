package com.fortunes.faas.network.codec.xml;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.fortunes.faas.network.model.xml.XmlMessage;


public class XmlMessageEncoder implements ProtocolEncoder {
	
	public void dispose(IoSession session) throws Exception {
		
	}

	@Override
	public void encode(IoSession session, Object o, ProtocolEncoderOutput out)
			throws Exception {
		XmlMessage message = (XmlMessage)o;
		IoBuffer buffer = IoBuffer.allocate(message.getTotalLength());
		
		buffer.putInt(message.getLength());
		buffer.put((byte)message.getFormatInt());
		buffer.put(new String(message.getTypeString()).getBytes());
		buffer.put(new String("abcdefghijklmnopqrstuvwx").getBytes());
		buffer.putInt(message.getOriginalLength());
		buffer.put(message.getContentsBytes());
		buffer.flip();
		out.write(buffer);
		out.flush();
	}
	


	

}
