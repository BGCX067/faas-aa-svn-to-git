package com.fortunes.faas.network.model.plate.request;

import org.apache.mina.core.buffer.IoBuffer;

import com.fortunes.faas.network.model.plate.RequestPlateMessage;

public class HeartBeat extends RequestPlateMessage{
	
	//AA 55 AA 55 10 00 00 00 01 00 00 00 AA AA AA AA AA AA AA AA AA AA AA AA AA AA AA AA AA
	
	public static final int TYPE = 0x001;
	
	@Override
	public int getProtocolType() {
		return TYPE;
	}
	
	@Override
	public int getContentsLength() {
		return 16;
	}
	

	@Override
	public IoBuffer toBuffer() {
		IoBuffer buffer = initBuffer();
		final byte[] heartBeat = {
			(byte)0xAA,	
			(byte)0xAA,	
			(byte)0xAA,	
			(byte)0xAA,	
			(byte)0xAA,	
			(byte)0xAA,	
			(byte)0xAA,	
			(byte)0xAA,	
			(byte)0xAA,	
			(byte)0xAA,	
			(byte)0xAA,	
			(byte)0xAA,	
			(byte)0xAA,	
			(byte)0xAA,	
			(byte)0xAA,	
			(byte)0xAA	
		};
		buffer.put(heartBeat);
		return buffer;
	}


	

}
