package com.luoran.wechat.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.luoran.wechat.WxConstant;
import com.luoran.wechat.cache.IWechatCache;
import com.luoran.wechat.util.Http;

/**
 * 微信公众号的能力实现
 * 
 * @author lifetime
 *
 */
@Component
public class GzhFacade implements WxConstant {
	private final static Logger logger = LoggerFactory.getLogger(GzhFacade.class);

	@Autowired
	private IWechatCache wechatCache;

	@Autowired
	private Environment env;

	/**
	 * 获取微信token
	 * 
	 * @return
	 */
	public void refreshToken() {
		if (wechatCache.isValidGzhAccessToken()) {
			// 未过期，不刷新
			return;
		}
		String url = Base_GetToken.replaceAll("\\$\\{APPID\\}", env.getProperty("weixin.appid"));
		url = url.replaceAll("\\$\\{SECRET\\}", env.getProperty("weixin.secret"));
		try {
			String res = Http.get(url);
			wechatCache.setGzhAccessTokenResult(res);
		} catch (Exception e) {
			logger.error(e.getMessage(), e.getCause());
		}
	}

	/**
	 * 发送模板消息
	 * 
	 * @param msgId
	 *            模板消息的id
	 * @param json
	 *            模板消息的内容
	 * @return
	 */
	public String sendTemplateMsg(String msgId, String json) {
		refreshToken();
		String url = GzhSendTemplateMsg.replaceAll("\\$\\{ACCESS_TOKEN\\}", wechatCache.getGzhAccessToken());
		try {
			return Http.postJson(url, json);
		} catch (Exception e) {
			logger.error(e.getMessage(), e.getCause());
			return null;
		}
	}

}
