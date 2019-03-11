package com.luoran.wechat.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.luoran.wechat.WxConstant;
import com.luoran.wechat.cache.IWechatCache;
import com.luoran.wechat.util.Http;
import com.luoran.wechat.util.MD5;

/**
 * 小程序能力实现
 * 
 * @author lifetime
 *
 */
@Service
public class XcxFacade implements WxConstant {
	private final static Logger logger = LoggerFactory.getLogger(XcxFacade.class);

	@Autowired
	private IWechatCache wechatCache;

	@Autowired
	private Environment env;

	/**
	 * 获取微信小程序token
	 * 
	 * @return
	 */
	public void refreshToken() {
		if (wechatCache.isValidXcxAccessToken()) {
			// 未过期，不刷新
			return;
		}
		String url = Base_GetToken.replaceAll("\\$\\{APPID\\}", env.getProperty("mini.appid"));
		url = url.replaceAll("\\$\\{SECRET\\}", env.getProperty("mini.appsecret"));
		try {
			String res = Http.get(url);
			wechatCache.setXcxAccessTokenResult(res);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e.getCause());
		}
	}

	/**
	 * 小程序code转session
	 * 
	 * @param jsCode
	 * @return 返回用户侧的sessionId
	 */
	public JSONObject code2Session(String jsCode) {
		String url = XcxCode2Session.replaceAll("\\$\\{APPID\\}", env.getProperty("mini.appid"));
		url = url.replaceAll("\\$\\{SECRET\\}", env.getProperty("mini.appsecret"));
		url = url.replaceAll("\\$\\{JSCODE\\}", jsCode);
		JSONObject res = new JSONObject();
		try {
			String msg = Http.get(url);
			JSONObject obj = JSONObject.parseObject(msg);
			if (!obj.containsKey("errcode")) {
				String sessionKey = MD5.md5(obj.getString("openid"));
				wechatCache.putXcxSessionKey(sessionKey, msg);
				res.put("sessionKey", sessionKey);
				res.put("openid",obj.getString("openid"));
				return res;
			} else {
				logger.error("小程序Session获取失败：{}", msg);
				return null;
			}
		} catch (Exception e) {
			logger.error("与微信服务器通讯异常：" + e.getMessage(), e.getCause());
		}
		return null;
	}
}
