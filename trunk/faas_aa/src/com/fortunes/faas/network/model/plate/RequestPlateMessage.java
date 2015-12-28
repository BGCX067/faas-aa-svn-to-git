package com.fortunes.faas.network.model.plate;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;

import com.fortunes.faas.network.model.Message;

public abstract class RequestPlateMessage implements Message{
	
	private static final int META_LENGTH = 12;
	public static final byte[] PROTOCOL_PREFIX = {
		(byte)0xAA,
		(byte)0x55,
		(byte)0xAA,
		(byte)0x55
	};
	
	public abstract IoBuffer toBuffer();
	public abstract int getContentsLength();
	public abstract int getProtocolType();
	
	protected IoBuffer initBuffer() {
		IoBuffer buffer = IoBuffer.allocate(getContentsLength()+ META_LENGTH);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(RequestPlateMessage.PROTOCOL_PREFIX);
		buffer.putInt(getContentsLength());
		buffer.putInt(getProtocolType());
		return buffer;
	}
	
	
	

}
