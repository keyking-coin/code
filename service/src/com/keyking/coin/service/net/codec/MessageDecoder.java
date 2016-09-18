package com.keyking.coin.service.net.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.ServerLog;

public class MessageDecoder extends CumulativeProtocolDecoder {
	IoBuffer reader = IoBuffer.allocate(1024);
	int preLen = 0;
	@Override
	protected boolean doDecode(IoSession session, IoBuffer buffer , ProtocolDecoderOutput out) throws Exception {
		int len = preLen;
		if (len == 0){
			len = buffer.getInt();
		}
		int remain = buffer.remaining();
		int readLen = len;
		if (readLen >= remain){
			preLen = readLen - remain;
			readLen = remain;
			ServerLog.info("more data received >>> " + preLen);
		}else if (preLen > 0){
			preLen = 0;
		}
		byte[] datas = new byte[readLen];
		buffer.get(datas);
		if (preLen > 0){
			reader.put(datas);
			return false;
		}
		byte[] total = null;
		if (reader.position() == 0){
			total = datas;
		}else{
			reader.put(datas);
			int pos = reader.position();
			reader.rewind();
			total = new byte[pos];
			reader.get(total);
			reader.clear();
		}
		DataBuffer data = DataBuffer.wrap(total);
		out.write(data);
		return true;
	}
	
}
 
 
 
