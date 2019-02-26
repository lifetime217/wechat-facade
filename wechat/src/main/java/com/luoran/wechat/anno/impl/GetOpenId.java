package com.luoran.wechat.anno.impl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;
import com.luoran.wechat.facade.WebFacade;

/**
 * @author lifetime
 *
 */
@Aspect
@Component
public class GetOpenId {
	private final static Logger logger = LoggerFactory.getLogger(GetOpenId.class);

	@Autowired
	private WebFacade webFacade;
	
	@Value("${weixin.appid}")
	private String appid;

	@Before("within(@org.springframework.stereotype.Controller *) && @annotation(com.luoran.wechat.anno.OpenId)")
	public void getOpenId() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		HttpServletResponse response = attributes.getResponse();
		String openId = getCookieValue(request, appid + "open_id");
		if (StringUtils.isEmpty(openId)) {
			if (StringUtils.isEmpty(request.getParameter("code"))) {
				logger.error("未能获取到微信网页授权时的code，请检查url是否转化过。转化方法见：WeixinUtil.urlConvert()");
				return;
			}
			JSONObject res = webFacade.refreshJsToken(request.getParameter("code"));
			if (res != null) {
				request.setAttribute(appid + "open_id", res.getString("openid"));
				request.setAttribute("web_access_token", res.getString("access_token"));
				Cookie cid = new Cookie(appid + "open_id", res.getString("openid"));
				cid.setMaxAge(Integer.MAX_VALUE);
				response.addCookie(cid);
			}
		} else {
			request.setAttribute(appid + "open_id", openId);
		}

	}

	String getCookieValue(HttpServletRequest request, String key) {
		Cookie[] cos = request.getCookies();
		if (cos != null) {
			for (int i = 0; i < cos.length; i++) {
				if (key.equals(cos[i].getName())) {
					return cos[i].getValue();
				}
			}
		}
		return null;
	}

}
