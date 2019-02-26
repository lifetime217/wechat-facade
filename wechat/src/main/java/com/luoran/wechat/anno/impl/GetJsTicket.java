package com.luoran.wechat.anno.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.luoran.wechat.cache.IWechatCache;
import com.luoran.wechat.facade.JsFacade;
import com.luoran.wechat.util.Sign;

/**
 * @author lifetime
 *
 */
@Aspect
@Component
public class GetJsTicket {

	@Autowired
	private JsFacade jsFacade;

	@Autowired
	private IWechatCache cache;

	@Autowired
	private Sign sign;

	@Before("within(@org.springframework.stereotype.Controller *) && @annotation(com.luoran.wechat.anno.JSTicket)")
	public void getTicket() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		jsFacade.refreshJSTicket();
		String url = request.getRequestURL().toString();
		String queryString = request.getQueryString();
		if (!StringUtils.isEmpty(queryString)) {
			if (queryString.indexOf("#") >= 0) {
				url += "?" + queryString.substring(0, queryString.indexOf("#"));
			} else {
				url += "?" + queryString;
			}
		}
		Map<String, String> params = sign.sign(cache.getJsapiTicket(), url);
		for (Iterator<Entry<String, String>> it = params.entrySet().iterator(); it.hasNext();) {
			Entry<String, String> entry = it.next();
			request.setAttribute(entry.getKey(), entry.getValue());
		}
	}

}
