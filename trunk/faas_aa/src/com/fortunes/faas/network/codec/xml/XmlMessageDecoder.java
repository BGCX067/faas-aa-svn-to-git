package com.fortunes.faas.network.codec.xml;

import java.io.DataInputStream;
import java.util.zip.InflaterInputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fortunes.faas.network.model.xml.XmlMessage;
import com.fortunes.faas.network.model.xml.XmlMessage.MessageFormat;


public class XmlMessageDecoder extends CumulativeProtocolDecoder {
	
	final Logger logger = LoggerFactory.getLogger(XmlMessageDecoder.class);
	
	private boolean dataAvailable(IoBuffer in){

        int dataLength = in.getInt(in.position());
		
		return in.remaining() >= dataLength+
						XmlMessage.CONTENTSLENGTH_LENGTH +
						XmlMessage.TYPE_LENGTH + 
						XmlMessage.FORMAT_LENGTH + 
						XmlMessage.MSG_ID_LENGTH +
						XmlMessage.ORIGINALLENGTH_LENGTH;
	}
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer dataInput,
			ProtocolDecoderOutput out) throws Exception {
		if(this.dataAvailable(dataInput)){
			
			XmlMessage message = new XmlMessage();
			int contentsLength = dataInput.getInt();
			logger.debug("contentsLength {}",contentsLength);
			message.setLength(contentsLength);
			
			//format
			byte messageFormat = dataInput.get();
			logger.debug("messageFormat {}",messageFormat);
			message.setFormat(messageFormat);
			
			//type
			byte[] typeBytes = new byte[XmlMessage.TYPE_LENGTH];
			dataInput.get(typeBytes);
			String typeString = new String(typeBytes,XmlMessage.ENCODING);
			logger.debug("typeString {}",typeString);
			message.setType(typeString);
			
			//messageId
			byte[] messageIdBytes = new byte[XmlMessage.MSG_ID_LENGTH];
			dataInput.get(messageIdBytes);
			String messageId = new String(messageIdBytes,XmlMessage.ENCODING);
			logger.debug("messageId {}",messageId);
			message.setMessageId(messageId);
			
			//originalLength
			int originalLength = dataInput.getInt();
			logger.debug("originalLength {}",originalLength);
			message.setOriginalLength(originalLength);
			
			//contents
			byte[] contentsBytes = new byte[originalLength];
			if(message.getFormat() == MessageFormat.compress){
				new DataInputStream(new InflaterInputStream(dataInput.asInputStream())).readFully(contentsBytes);
			}else{
				dataInput.get(contentsBytes);
			}
			message.setContents(contentsBytes);
			
			out.write(message);
			
			return true;
		}else{
			return false;
		}
		
	}
	
}
