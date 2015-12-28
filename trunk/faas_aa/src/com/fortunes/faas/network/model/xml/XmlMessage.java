package com.fortunes.faas.network.model.xml;

import java.io.UnsupportedEncodingException;
import java.util.zip.Deflater;
import com.fortunes.faas.network.model.Message;

public class XmlMessage implements Message{
	
	public static final String ENCODING = "GBK";
	
	private static final String BIN = "BIN";
	private static final String XML = "XML";
	public static final int CONTENTSLENGTH_LENGTH = 4;
	public static final int TYPE_LENGTH = 3;
	public static final int FORMAT_LENGTH = 1;
	public static final int MSG_ID_LENGTH = 24;
	public static final int ORIGINALLENGTH_LENGTH = 4;
	
	public static final String DEFAULT_MSGID = "abcdefghijklmnopqrstuvwx";
	
	public enum MessageFormat {
		normal,compress,encrypt
	}
	
	public enum MessageType {
		xml,binary
	}
	
	private int length;
	private MessageFormat format;
	private MessageType type;
	private String messageId;
	private int originalLength;
	private String contents;
	
	
	@Override
	public String toString() {
		return "ContentsLength : "+length+"\n"+
				"Format : "+format+"("+this.getFormatInt()+")\n"+
				"Type : "+type+"("+this.getTypeString()+")\n"+
				"MessageId : "+messageId+"\n"+
				"OriginalLength : "+originalLength+"\n"+
				"Contents :\n\n"+contents;
	}
	
	public static XmlMessage createDefaultMessage(byte[] contents){
		XmlMessage message = new XmlMessage();
		message.setOriginalLength(contents.length);
		message.setFormat(MessageFormat.normal);
		message.setType(XmlMessage.MessageType.xml);
		message.setMessageId(DEFAULT_MSGID);
		//当内容大于1M,压缩内容
		if(contents.length > 1024*1024){
			 // Compress the contents
			 Deflater compresser = new Deflater();
			 compresser.setInput(contents);
			 compresser.finish();
			 byte[] compressedData = new byte[compresser.getTotalOut()];
			 
			 message.setLength(compresser.deflate(compressedData));
			 message.setContents(compressedData);

		}else{
			message.setLength(contents.length);
			message.setContents(contents);
		}
		
		return message;
	}
		
	public int getTotalLength(){
		return CONTENTSLENGTH_LENGTH +
				TYPE_LENGTH + 
				FORMAT_LENGTH + 
				MSG_ID_LENGTH +
				ORIGINALLENGTH_LENGTH +
				this.getLength();
	}
	
	public void setType(String typeString){
		if(typeString.equals(XML)){
			this.setType(MessageType.xml);
		}else if(typeString.equals(BIN)){
			this.setType(MessageType.binary);
		}
	}
	
	public String getTypeString(){
		return this.type == MessageType.xml ? XML : BIN;
	}
	
	public int getFormatInt(){
		if(this.format == MessageFormat.compress)
			return 1;
		else if(this.format == MessageFormat.encrypt)
			return 2;
		else 
			return 0;
	}
	
	public void setFormat(int formatInt){
		switch(formatInt){
			case 0:
				this.setFormat(MessageFormat.normal);
				break;
			case 1:
				this.setFormat(MessageFormat.compress);
				break;
			case 2:
				this.setFormat(MessageFormat.encrypt);
				break;				
		}
	}
	
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public MessageFormat getFormat() {
		return format;
	}
	public void setFormat(MessageFormat format) {
		this.format = format;
	}
	public MessageType getType() {
		return type;
	}
	public void setType(MessageType type) {
		this.type = type;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public int getOriginalLength() {
		return originalLength;
	}
	public void setOriginalLength(int originalLength) {
		this.originalLength = originalLength;
	}
	public String getContents() {
		return contents;
	}
	
	public byte[] getContentsBytes() {
		try {
			return this.contents.getBytes(XmlMessage.ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setContents(byte[] contents) {
		try {
			this.contents = new String(contents,XmlMessage.ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void setContents(String contents) {
		this.contents = contents;
	}
	
}
