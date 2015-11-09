package org.ubicomplab.campus.whereami;

public class CommonResult {
	
	public static final int RESULT_OK = 200;
	public static final int RESULT_ERROR = 400;
	
	private int resultCode;
	private String resultMessage;
	private WiFiLocation resultObject;
	
	
	public CommonResult() {
		super();
	}
	public CommonResult(int resultCode, String resultMessage,
			WiFiLocation resultObject) {
		super();
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
		this.resultObject = resultObject;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public WiFiLocation getResultObject() {
		return resultObject;
	}
	public void setResultObject(WiFiLocation resultObject) {
		this.resultObject = resultObject;
	}
	@Override
	public String toString() {
		return "CommonResult [resultCode=" + resultCode + ", resultMessage="
				+ resultMessage + ", resultObject=" + resultObject + "]";
	}
	
}
