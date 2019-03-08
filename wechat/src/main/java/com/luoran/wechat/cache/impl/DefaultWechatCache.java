package com.luoran.wechat.cache.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.luoran.wechat.cache.IWechatCache;

/**
 * 微信默认的缓存策略（本地缓存）
 * 
 * @author lifetime
 *
 */
@Component("DefaultWechatCache")
public class DefaultWechatCache implements IWechatCache {
	private final static Logger logger = LoggerFactory.getLogger(DefaultWechatCache.class);

	private String gzhAccessToken;
	private long gzhAccessTokenExpireTime;
	private long gzhAccessTokenLastupdateTime;
	private String gzhAccessTokenResult;

	private String jsapiTicket;
	private long jsapiTicketExpireTime;
	private long jsapiTicketLastupdateTime;
	private String jsapiResult;

	private Map<String, String> xcxSessionCache = new ConcurrentHashMap<String, String>();

	private String xcxAccessToken;
	private long xcxAccessTokenExpireTime;
	private long xcxAccessTokenLastupdateTime;
	private String xcxAccessTokenResult;

	private File synFile;
	private Timer timer;

	public void afterPropertiesSet() throws Exception {
		synFile = new File(System.getProperty("java.io.tmpdir") + File.pathSeparator + "DefaultWechatCache.ini");
		if (!synFile.exists()) {
			if (!synFile.createNewFile()) {
				logger.error("微信默认缓存临时文件创建失败。");
				synFile = null;
			}
		}
		timer = new Timer("DefaultWechatCache-Period[" + getSynPeriod() + "]", true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				writeDisk();
			}
		}, 0, getSynPeriod());
	}

	protected void writeDisk() {
		if (synFile == null) {
			return;
		}
		Properties prop = new Properties();
		if (gzhAccessToken != null) {
			prop.setProperty("gzhAccessToken", gzhAccessToken);
			prop.setProperty("gzhAccessTokenExpireTime", String.valueOf(gzhAccessTokenExpireTime));
			prop.setProperty("gzhAccessTokenLastupdateTime", String.valueOf(gzhAccessTokenLastupdateTime));
			prop.setProperty("gzhAccessTokenResult", gzhAccessTokenResult);
		}

		if (jsapiTicket != null) {
			prop.setProperty("jsapiTicket", jsapiTicket);
			prop.setProperty("jsapiTicketExpireTime", String.valueOf(jsapiTicketExpireTime));
			prop.setProperty("jsapiTicketLastupdateTime", String.valueOf(jsapiTicketLastupdateTime));
			prop.setProperty("jsapiResult", jsapiResult);
		}

		try {
			prop.store(new FileWriter(synFile), "DefaultWechatCache Config");
			logger.debug("微信配置成功写入文件：" + synFile.getPath());
		} catch (IOException e) {
			logger.error(e.getMessage(), e.fillInStackTrace());
		}
	}

	protected void synDisk() {
		if (synFile == null) {
			return;
		}
		Properties prop = new Properties();
		try {
			prop.load(new FileReader(synFile));
			setGzhAccessToken(prop.getProperty("gzhAccessToken"));
			setGzhAccessTokenExpireTime(Long.parseLong(prop.getProperty("gzhAccessTokenExpireTime")));
			setGzhAccessTokenLastupdateTime(Long.parseLong(prop.getProperty("gzhAccessTokenLastupdateTime")));
			setGzhAccessTokenResult(gzhAccessTokenResult);

			setJsapiResult(prop.getProperty("jsapiResult"));
			setJsapiTicket(prop.getProperty("jsapiTicket"));
			setJsapiTicketExpireTime(Long.parseLong(prop.getProperty("jsapiTicketExpireTime")));
			setJsapiTicketLastupdateTime(Long.parseLong(prop.getProperty("jsapiTicketLastupdateTime")));
			logger.info("微信配置成功从文件写入应用程序：" + synFile.getPath());
			logger.debug(configString());
		} catch (Exception e) {
			logger.error(e.getMessage(), e.fillInStackTrace());
		}
	}

	public int getSynPeriod() {
		return 120000;// 2分钟
	}

	public String getGzhAccessToken() {
		return gzhAccessToken;
	}

	public void setGzhAccessToken(String token) {
		gzhAccessToken = token;
	}

	public long getGzhAccessTokenExpireTime() {
		return gzhAccessTokenExpireTime;
	}

	public void setGzhAccessTokenExpireTime(long gzhAccessTokenExpireTime) {
		this.gzhAccessTokenExpireTime = gzhAccessTokenExpireTime;
	}

	public long getGzhAccessTokenLastupdateTime() {
		return gzhAccessTokenLastupdateTime;
	}

	public void setGzhAccessTokenLastupdateTime(long gzhAccessTokenLastupdateTime) {
		this.gzhAccessTokenLastupdateTime = gzhAccessTokenLastupdateTime;
	}

	public String getGzhAccessTokenResult() {
		return gzhAccessTokenResult;
	}

	public void setGzhAccessTokenResult(String gzhResult) {
		this.gzhAccessTokenResult = gzhResult;
		JSONObject obj = JSONObject.parseObject(gzhResult);
		if (obj.getString("access_token") != null) {
			setGzhAccessToken(obj.getString("access_token"));
			setGzhAccessTokenExpireTime(obj.getInteger("expires_in") - 300);// 主动让accessToken提前5分钟过期
			setGzhAccessTokenLastupdateTime(System.currentTimeMillis() / 1000);
		} else {
			logger.error("获取公众号AccessToken失败：{} ", gzhResult);
		}
	}

	public boolean isValidGzhAccessToken() {
		return (System.currentTimeMillis() / 1000 - getGzhAccessTokenLastupdateTime()) < getGzhAccessTokenExpireTime();
	}

	public long getJsapiTicketExpireTime() {
		return jsapiTicketExpireTime;
	}

	public void setJsapiTicketExpireTime(long jsapiTicketExpireTime) {
		this.jsapiTicketExpireTime = jsapiTicketExpireTime;
	}

	public long getJsapiTicketLastupdateTime() {
		return jsapiTicketLastupdateTime;
	}

	public void setJsapiTicketLastupdateTime(long jsapiTicketLastupdateTime) {
		this.jsapiTicketLastupdateTime = jsapiTicketLastupdateTime;
	}

	public String getJsapiResult() {
		return jsapiResult;
	}

	public void setJsapiResult(String jsapiResult) {
		this.jsapiResult = jsapiResult;
		JSONObject obj = JSONObject.parseObject(jsapiResult);
		if (obj.getString("ticket") != null) {
			setJsapiTicket(obj.getString("ticket"));
			setJsapiTicketExpireTime(obj.getLongValue("expires_in") - 300);// 主动让Jsapi_ticket提前5分钟过期
			setJsapiTicketLastupdateTime(System.currentTimeMillis() / 1000);
		} else {
			logger.error("获取Jsapi_ticket失败：{} ", jsapiResult);
		}
	}

	public String getJsapiTicket() {
		return jsapiTicket;
	}

	public void setJsapiTicket(String ticket) {
		jsapiTicket = ticket;
	}

	public boolean isValidJsapiTicket() {
		return (System.currentTimeMillis() / 1000 - getJsapiTicketLastupdateTime()) < getJsapiTicketExpireTime();
	}

	@Override
	public String toString() {
		return new StringBuilder().append(timer.toString()).append(getSynPeriod()).append("\n").append(configString())
				.toString();
	}

	public String configString() {
		JSONObject obj = new JSONObject();
		obj.put("gzhAccessToken", gzhAccessToken);
		obj.put("gzhAccessTokenExpireTime", gzhAccessTokenExpireTime);
		obj.put("gzhAccessTokenLastupdateTime", gzhAccessTokenLastupdateTime);
		obj.put("gzhAccessTokenResult", gzhAccessTokenResult);
		obj.put("jsapiTicket", jsapiTicket);
		obj.put("jsapiTicketExpireTime", jsapiTicketExpireTime);
		obj.put("jsapiTicketLastupdateTime", jsapiTicketLastupdateTime);
		obj.put("jsapiResult", jsapiResult);
		return obj.toJSONString();
	}

	public void putXcxSessionKey(String sessionKey, String xcxSessionString) {
		xcxSessionCache.put(sessionKey, xcxSessionString);
	}

	public String getXcxSessionString(String sessionKey) {
		return xcxSessionCache.get(sessionKey);
	}

	public String getXcxAccessToken() {
		return xcxAccessToken;
	}

	public void setXcxAccessToken(String xcxAccessToken) {
		this.xcxAccessToken = xcxAccessToken;
	}

	public long getXcxAccessTokenExpireTime() {
		return xcxAccessTokenExpireTime;
	}

	public void setXcxAccessTokenExpireTime(long xcxAccessTokenExpireTime) {
		this.xcxAccessTokenExpireTime = xcxAccessTokenExpireTime;
	}

	public long getXcxAccessTokenLastupdateTime() {
		return xcxAccessTokenLastupdateTime;
	}

	public void setXcxAccessTokenLastupdateTime(long xcxAccessTokenLastupdateTime) {
		this.xcxAccessTokenLastupdateTime = xcxAccessTokenLastupdateTime;
	}

	public String getXcxAccessTokenResult() {
		return xcxAccessTokenResult;
	}

	/**
	 * 赋值accessToken的返回值并且设置过期时间
	 */
	public void setXcxAccessTokenResult(String xcxAccessTokenResult) {
		this.xcxAccessTokenResult = xcxAccessTokenResult;
		JSONObject obj = JSONObject.parseObject(xcxAccessTokenResult);
		if (obj.getString("access_token") != null) {
			setGzhAccessToken(obj.getString("access_token"));
			setGzhAccessTokenExpireTime(obj.getInteger("expires_in") - 300);// 主动让accessToken提前5分钟过期
			setGzhAccessTokenLastupdateTime(System.currentTimeMillis() / 1000);
		} else {
			logger.error("获取小程序AccessToken失败：{} ", xcxAccessTokenResult);
		}
	}

	/**
	 * 验证小程序的access_token的时间
	 */
	@Override
	public boolean isValidXcxAccessToken() {
		// TODO Auto-generated method stub
		return (System.currentTimeMillis() / 1000 - getXcxAccessTokenLastupdateTime()) < getXcxAccessTokenExpireTime();
	}
}
