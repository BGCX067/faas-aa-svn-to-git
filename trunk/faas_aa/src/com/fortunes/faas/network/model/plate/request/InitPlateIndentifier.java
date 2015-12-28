package com.fortunes.faas.network.model.plate.request;

import org.apache.mina.core.buffer.IoBuffer;

import com.fortunes.faas.network.model.plate.RequestPlateMessage;

/**
 * @author Neo.Liao
 *
 */
public class InitPlateIndentifier extends RequestPlateMessage{
	
	//AA 55 AA 55 04 00 00 00 03 02 00 00 01 00 00 00
	
	public static final int TYPE = 0x203;
	
	@Override
	public int getProtocolType() {
		return TYPE;
	}
	
	@Override
	public int getContentsLength() {
		return 4;
	}
	
	@Override
	public IoBuffer toBuffer() {
		IoBuffer buffer = initBuffer();
		int value = 1;
		buffer.putInt(value);
		return buffer;
	}
	

}
