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
	 * 小程序code转session
	 * 
	 * @param jsCode
	 * @return 返回用户侧的sessionId
	 */
	public String code2Session(String jsCode) {
		String url = XcxCode2Session.replaceAll("\\$\\{APPID\\}", env.getProperty("weixin.appid"));
		url = XcxCode2Session.replaceAll("\\$\\{SECRET\\}", env.getProperty("weixin.secret"));
		url = XcxCode2Session.replaceAll("\\$\\{JSCODE\\}", jsCode);
		try {
			String msg = Http.get(url);
			JSONObject obj = JSONObject.parseObject(msg);
			if (obj.getInteger("errcode") == 0) {
				String sessionKey = MD5.md5(obj.getString("openid"));
				wechatCache.putXcxSessionKey(sessionKey, msg);
				return sessionKey;
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
