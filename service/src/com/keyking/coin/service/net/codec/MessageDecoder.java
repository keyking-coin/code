package com.keyking.coin.service.net.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.keyking.coin.service.net.buffer.DataBuffer;

public class MessageDecoder extends CumulativeProtocolDecoder {
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer buffer , ProtocolDecoderOutput out) throws Exception {
		int len = buffer.getInt();
		int remain = buffer.remaining();
		if (len != remain) {
			buffer.position(buffer.position() + remain);
			return false;
		}
		byte[] datas = new byte[len];
		buffer.get(datas);
		DataBuffer data = DataBuffer.wrap(datas);
		out.write(data);
		return buffer.remaining() > 0;
	}
}
 
 
 
