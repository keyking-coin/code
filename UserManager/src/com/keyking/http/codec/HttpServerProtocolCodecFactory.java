package com.keyking.http.codec;

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

import com.keyking.http.response.HttpResponseMessage;

public class HttpServerProtocolCodecFactory extends DemuxingProtocolCodecFactory {
	public HttpServerProtocolCodecFactory() {
		super.addMessageDecoder(HttpRequestDecoder.class);
		super.addMessageEncoder(HttpResponseMessage.class,HttpResponseEncoder.class);
	}
}  
