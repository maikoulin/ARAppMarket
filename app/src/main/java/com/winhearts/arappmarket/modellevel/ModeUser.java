package com.winhearts.arappmarket.modellevel;

/**
 * 数据层统一接口
 * @author liw
 *
 * @param <T>
 */
public interface ModeUser<T> {
	/**
	 * 成功获取json
	 * @param t
	 */
	void onJsonSuccess(T t);
	
	/**
	 * 失败获取
	 * @param e
	 */
	void onRequestFail(Throwable e);
}
