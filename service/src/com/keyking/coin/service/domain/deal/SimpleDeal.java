package com.keyking.coin.service.domain.deal;

import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;

public class SimpleDeal implements SerializeEntity{
	
	Deal deal;
	
	public SimpleDeal(Deal deal){
		this.deal = deal;
	}

	@Override
	public void serialize(DataBuffer out) {
		deal.simpleSerialize(out);
	}
}
