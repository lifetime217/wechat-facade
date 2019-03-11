package com.luoran.wechat.cache;

import org.springframework.beans.factory.InitializingBean;

/**
 * 微信token缓存
 * 
 * @author lifetime
 *
 */
public interface IWechatCache extends InitializingBean {

	/**
	 * 缓存的刷盘周期（单位：秒）
	 * 
	 * @return
	 */
	public int getSynPeriod();

	/**
	 * 是否是有效的公众号全局accessToken
	 * 
	 * @return
	 */
	public boolean isValidGzhAccessToken();

	/**
	 * 获取公众号全局accessToken
	 * 
	 * @return
	 */
	public String getGzhAccessToken();

	/**
	 * 设置公众号全局accessToken
	 * 
	 * @param token
	 */
	public void setGzhAccessToken(String token);

	/**
	 * 获取公众号全局accessToken的过期时间（单位：秒）
	 * 
	 * @return
	 */
	public long getGzhAccessTokenExpireTime();

	/**
	 * 设置公众号全局accessToken的过期时间（单位：秒）
	 * 
	 * @param gzhAccessTokenExpireTime
	 */
	public void setGzhAccessTokenExpireTime(long gzhAccessTokenExpireTime);

	/**
	 * 获取公众号全局accessToken的最后更新时间（单位：秒）
	 * 
	 * @return
	 */
	public long getGzhAccessTokenLastupdateTime();

	/**
	 * 设置公众号全局accessToken的最后更新时间（单位：秒）
	 * 
	 * @param lastGzhAccessTokenLastupdateTime
	 */
	public void setGzhAccessTokenLastupdateTime(long gzhAccessTokenLastupdateTime);

	/**
	 * 接口获取公众号accessToken时微信返回的结果字符串
	 * 
	 * @return
	 */
	public String getGzhAccessTokenResult();

	/**
	 * 设置接口获取公众号accessToken时微信返回的结果字符串
	 * 
	 * @param gzhResult
	 */
	public void setGzhAccessTokenResult(String gzhResult);

	/**
	 * 是否是有效的jsapi_ticket
	 * 
	 * @return
	 */
	public boolean isValidJsapiTicket();

	/**
	 * 获取公众号用于调用微信JS接口的临时票据jsapi_ticket的过期时间（单位：秒）
	 * 
	 * @return
	 */
	public long getJsapiTicketExpireTime();

	/**
	 * 设置公众号用于调用微信JS接口的临时票据jsapi_ticket的过期时间（单位：秒）
	 * 
	 * @param gzhJsapiTicketExpireTime
	 */
	public void setJsapiTicketExpireTime(long gzhJsapiTicketExpireTime);

	/**
	 * 获取jsapi_ticket的最后更新时间（单位：秒）
	 * 
	 * @return
	 */
	public long getJsapiTicketLastupdateTime();

	/**
	 * 设置jsapi_ticket的最后更新时间（单位：秒）
	 * 
	 * @param jsapiTicketLastupdateTime
	 */
	public void setJsapiTicketLastupdateTime(long jsapiTicketLastupdateTime);

	/**
	 * 接口获取Jsapi_ticket时微信返回的结果字符串
	 * 
	 * @return
	 */
	public String getJsapiResult();

	/**
	 * 设置接口获取Jsapi_ticket时微信返回的结果字符串
	 * 
	 * @param jsapiResult
	 */
	public void setJsapiResult(String jsapiResult);

	/**
	 * 获取公众号用于调用微信JS接口的临时票据jsapi_ticket
	 * 
	 * @return
	 */
	public String getJsapiTicket();

	/**
	 * 设置公众号用于调用微信JS接口的临时票据jsapi_ticket
	 * 
	 * @param ticket
	 */
	public void setJsapiTicket(String ticket);

	/**
	 * 设置微信用户在小程序端session key
	 * 
	 * @param sessionKey
	 *            session的key
	 * @param xcxSessionString
	 *            明文session串
	 */
	public void putXcxSessionKey(String sessionKey, String xcxSessionString);

	/**
	 * 获取微信用户的session信息
	 * 
	 * @param sessionKey
	 * @return
	 */
	public String getXcxSessionString(String sessionKey);
	
	/**
	 * 是否是有效的小程序全局accessToken
	 * 
	 * @return
	 */
	public boolean isValidXcxAccessToken();
	
	/**
	 * 设置接口赋值小程序accessToken时微信返回的结果字符串
	 * 
	 * @param gzhResult
	 */
	public void setXcxAccessTokenResult(String gzhResult);
	
	/**
	 * 接口获取小程序accessToken时微信返回的结果字符串
	 * 
	 * @return
	 */
	public String getXcxAccessTokenResult();


}
