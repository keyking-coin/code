package com.keyking.coin.service.net.logic;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.PictureUtil;
import com.keyking.coin.util.ServerLog;

public class LoadPic extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		String fileName = buffer.getUTF();
		resp.add(fileName);
		PictureUtil.tryInitPicWH(fileName,resp);
		byte[] data = PictureUtil.tryLoadPicData(fileName);
		if (data != null){
			ServerLog.info("load pic ----> name is " + fileName);
			resp.add(data);
			resp.setSucces();
		}
		return resp;
	}

}
