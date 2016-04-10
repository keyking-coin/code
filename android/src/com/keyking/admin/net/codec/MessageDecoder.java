package com.keyking.admin.net.codec;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.keyking.admin.net.DataBuffer;

public class MessageDecoder extends CumulativeProtocolDecoder {
	List<Byte> bytes = new ArrayList<Byte>();
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
		}else if (preLen > 0){
			preLen = 0;
		}
		byte[] datas = new byte[readLen];
		buffer.get(datas);
		if (preLen > 0){
			for (int i = 0 ; i < readLen ; i++){
				bytes.add(datas[i]);
			}
			return false;
		}
		byte[] total = null;
		if (bytes.size() == 0){
			total = datas;
		}else{
			int size = bytes.size();
			int totalLen = datas.length + size;
			total = new byte[totalLen];
			for (int i = 0 ; i < size ;i++){
				total[i] = bytes.get(i).byteValue();
			}
			//System.arraycopy(bytes.toArray(new Object[size]),0,total,0,size);
			System.arraycopy(datas,0,total,size,readLen);
			bytes.clear();
		}
		DataBuffer data = DataBuffer.wrap(total);
		out.write(data);
		return buffer.remaining() == 0;
	}


}
