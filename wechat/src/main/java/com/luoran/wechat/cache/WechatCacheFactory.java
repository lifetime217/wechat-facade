package com.luoran.wechat.cache;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author lifetime
 *
 */
@Component
@Primary
public class WechatCacheFactory implements FactoryBean<IWechatCache>, ApplicationContextAware {
	private ApplicationContext applicationContext;

	@Autowired
	private Environment env;

	public IWechatCache getObject() throws Exception {
		return applicationContext.getBean(env.getProperty("wechat.cache.impl", "DefaultWechatCache"), IWechatCache.class);
	}

	public Class<?> getObjectType() {
		return IWechatCache.class;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
