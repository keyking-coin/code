package com.keyking.admin.net.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.keyking.admin.net.DataBuffer;

public class MessageDecoder extends CumulativeProtocolDecoder {

	@Override
	protected boolean doDecode(IoSession session, IoBuffer buffer , ProtocolDecoderOutput out) throws Exception {
		int len = buffer.getInt();
		if (len != buffer.remaining()) {
			buffer.position(buffer.position() + buffer.remaining());
			return false;
		}
		byte[] datas = new byte[len];
		buffer.get(datas);
		DataBuffer data = DataBuffer.wrap(datas);
		out.write(data);
		return true;
	}


}
