package cn.jpush.api.common.resp;

import com.google.gson.annotations.Expose;

@SuppressWarnings("serial")
public class BooleanResult extends DefaultResult {

	@Expose public boolean result;
	
}
