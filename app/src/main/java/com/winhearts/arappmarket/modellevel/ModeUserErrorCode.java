package com.winhearts.arappmarket.modellevel;

/**
 * 数据层统一接口
 * @author liw
 *
 * @param <T>
 */
public interface ModeUserErrorCode<T> {
	/**
	 * 成功获取json
	 * @param t
	 */
	void onJsonSuccess(T t);
	

	
	void onRequestFail(int code, Throwable e);
}
