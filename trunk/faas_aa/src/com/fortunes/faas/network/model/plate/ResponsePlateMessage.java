package com.fortunes.faas.network.model.plate;

import com.fortunes.faas.network.model.Message;
import com.fortunes.faas.network.model.plate.response.PlateInfo;

public class ResponsePlateMessage implements Message{
	
	private int contentsLength;
	private int type;
	private byte[] contents;
	
	
	public static final int META_LENGTH = 12;
	public static final byte[] PROTOCOL_PREFIX = {
		(byte)0xAA,
		(byte)0x55,
		(byte)0xAA,
		(byte)0x55
	};
	
	
	public ResponsePlateMessage parse(){
		return this;
	}
	
	public void setType(int type) {
		this.type = type;
	}


	public int getType() {
		return type;
	}


	public void setContentsLength(int contentsLength) {
		this.contentsLength = contentsLength;
	}


	public int getContentsLength() {
		return contentsLength;
	}

	public void setContents(byte[] contents) {
		this.contents = contents;
	}

	public byte[] getContents() {
		return contents;
	}
	
	
	

}
