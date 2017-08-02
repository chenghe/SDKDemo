package com.cgnb;

public interface RequestListener {

	public void error(int code, String json);
	public void success(int code, String json);
}
