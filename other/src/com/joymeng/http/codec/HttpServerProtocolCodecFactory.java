package com.joymeng.http.codec;

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

import com.joymeng.http.response.HttpResponseMessage;

public class HttpServerProtocolCodecFactory extends DemuxingProtocolCodecFactory {
	public HttpServerProtocolCodecFactory() {
		super.addMessageDecoder(HttpRequestDecoder.class);
		super.addMessageEncoder(HttpResponseMessage.class,HttpResponseEncoder.class);
	}
}  
