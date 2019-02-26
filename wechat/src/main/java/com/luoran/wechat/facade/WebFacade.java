package com.luoran.wechat.facade;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.luoran.wechat.WxConstant;
import com.luoran.wechat.util.Http;

/**
 * 微信网页开发时的功能类<br>
 * <b>需配置：</b><br>
 * weixin.appid<br>
 * weixin.secret<br>
 * 
 * @author lifetime
 *
 */
@Component
public class WebFacade implements WxConstant {
	private final static Logger logger = LoggerFactory.getLogger(WebFacade.class);

	@Autowired
	private Environment env;

	/**
	 * 将普通url转化为绿框授权的url
	 * 
	 * @param url
	 * @return
	 */
	public String urlConvert(String url) {
		try {
			String decodeUrl = URLEncoder.encode(url, "UTF-8");
			String newUrl = GzhAuthorizeCode.replaceAll("\\$\\{APPID\\}", env.getProperty("weixin.appid")).replaceAll("\\$\\{REDIRECT_URI\\}", decodeUrl);
			return newUrl;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * 获取网页授权接口调用凭证
	 * 
	 * @param code
	 * @return 可能返回空值
	 */
	public JSONObject refreshJsToken(String code) {
		String url = WebJSAccessToken.replaceAll("\\$\\{APPID\\}", env.getProperty("weixin.appid"));
		url = url.replaceAll("\\$\\{SECRET\\}", env.getProperty("weixin.secret"));
		url = url.replaceAll("\\$\\{CODE\\}", code);
		try {
			String res = Http.get(url);
			logger.debug("根据Code[{}]换取Web网页AccessToken结果：{}", code, res);
			return JSONObject.parseObject(res);
		} catch (Exception e) {
			logger.error("与微信服务器通讯异常：" + e.getMessage(), e.getCause());
		}
		return null;
	}

	/**
	 * 获取网页授权后的微信用户信息
	 * 
	 * @param accessToken
	 * @param openId
	 * @return
	 */
	public JSONObject getInfoByOpenId(String accessToken, String openId) {
		if (accessToken == null) {
			return null;
		}
		String url = WebJSGetUserInfo.replaceAll("\\$\\{ACCESS_TOKEN\\}", accessToken);
		url = url.replaceAll("\\$\\{OPENID\\}", openId);
		try {
			String res = Http.get(url);
			JSONObject obj = JSONObject.parseObject(res);
			if (obj.getString("errcode") == null) {
				return obj;
			} else {
				logger.info(res);
			}
		} catch (Exception e) {
			logger.error("与微信服务器通讯异常：" + e.getMessage(), e.getCause());
		}
		return null;
	}

}
