package com.keyking.admin.net.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.keyking.admin.net.DataBuffer;

public class MessageDecoder extends CumulativeProtocolDecoder {

	@Override
	protected boolean doDecode(IoSession arg0, IoBuffer arg1,ProtocolDecoderOutput arg2) throws Exception {
		int len = arg1.getInt();
		if (len != arg1.remaining()) {
			arg1.position(arg1.position() + arg1.remaining());
			return false;
		}
		byte[] datas = new byte[len];
		arg1.get(datas);
		DataBuffer data = DataBuffer.wrap(datas);
		arg2.write(data);
		return true;
	}


}
