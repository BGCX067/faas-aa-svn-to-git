package com.fortunes.faas.network.client;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;

/**
 * 
 * @author Leo
 * @version 2011-4-12
 */
public class JavaObjectClient extends Client {

	@Override
	protected ProtocolCodecFactory getCodecFactory() {
		return new ObjectSerializationCodecFactory();
	}

}
