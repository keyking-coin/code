package cn.jpush.api.common.resp;

@SuppressWarnings("serial")
public class DefaultResult extends BaseResult {

	public static DefaultResult fromResponse(ResponseWrapper responseWrapper) {
		DefaultResult result = null;

		if (responseWrapper.isServerResponse()) {
			result = new DefaultResult();
		}

		result.setResponseWrapper(responseWrapper);

		return result;
	}

}
