package com.luoran.wechat.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.luoran.wechat.WxConstant;
import com.luoran.wechat.cache.IWechatCache;
import com.luoran.wechat.util.Http;

/**
 * 让网页端具备操作微信SDK能力（如：扫码，拍照 等）
 * 
 * @author lifetime
 *
 */
@Component
public class JsFacade implements WxConstant {
	private final static Logger logger = LoggerFactory.getLogger(JsFacade.class);

	@Autowired
	private IWechatCache wechatCache;

	@Autowired
	private GzhFacade gzhFacade;

	/**
	 * 检测js_ticket是否可用，过期则刷新
	 * 
	 * @return
	 */
	public void refreshJSTicket() {
		if (!wechatCache.isValidJsapiTicket()) {
			if (!wechatCache.isValidGzhAccessToken()) {
				gzhFacade.refreshToken();
			}
			try {
				String url = WebJSSDKTicket.replaceAll("\\$\\{ACCESS_TOKEN\\}", wechatCache.getGzhAccessToken());
				String res = Http.get(url);
				wechatCache.setJsapiResult(res);
			} catch (Exception e) {
				logger.error(e.getMessage(), e.getCause());
			}
		}
	}
}
