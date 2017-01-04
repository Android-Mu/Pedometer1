package com.click369.pedometer.common.utils;

public interface HttpCallbackListener {

	void onFinish(String response);

	void onError(Exception e);

}
