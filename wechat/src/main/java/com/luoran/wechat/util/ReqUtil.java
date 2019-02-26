package com.luoran.wechat.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author lifetime
 *
 */
@Component
public class ReqUtil {
	@Value("${weixin.appid}")
	private String appid;
	
	public String getContentPath(){
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		
		return request.getContextPath();
	}
	
	public String getAttr(String key) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		Object obj = request.getAttribute(key);
		return obj != null ? obj.toString() : null;
	}

	public String getParam(String key) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		return request.getParameter(key);
	}

	public String getOpenId(){
		return getAttr(appid + "open_id");
	}
	
	public String getWebAccessToken(){
		return getAttr("web_access_token");
	}
}
