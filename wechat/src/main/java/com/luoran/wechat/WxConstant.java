package com.luoran.wechat;

/**
 * 微信常量定义
 * 
 * @author lifetime
 *
 */
public interface WxConstant {

	/**
	 * 微信公众平台基础token，其他接口必须的参数
	 */
	public String Base_GetToken = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=${APPID}&secret=${SECRET}";

	/**
	 * 微信公众号静默授权与绿框授权时的常规url转化目的
	 */
	public String GzhAuthorizeCode = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=${APPID}&redirect_uri=${REDIRECT_URI}&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

	/**
	 * 公众号发送模板消息的接口，所需参数为：[平台基础token]
	 */
	public String GzhSendTemplateMsg = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=${ACCESS_TOKEN}";

	
	
	/**
	 * 获取网页授权接口调用凭证
	 */
	public String WebJSAccessToken = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=${APPID}&secret=${SECRET}&code=${CODE}&grant_type=authorization_code";

	/**
	 * 基于网页授权的accesstoken获取openid对应的微信信息
	 */
	public String WebJSGetUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=${ACCESS_TOKEN}&openid=${OPENID}&lang=zh_CN";

	/**
	 * 调用微信能力的sdk ticket，先需获取WebJSAccessToken
	 */
	public String WebJSSDKTicket = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=${ACCESS_TOKEN}&type=jsapi";

	
	
	/**
	 * 通过 wx.login() 接口获得临时登录凭证 code 后传到开发者服务器调用此接口完成登录流程
	 */
	public String XcxCode2Session = "https://api.weixin.qq.com/sns/jscode2session?appid=${APPID}&secret=${SECRET}&js_code=${JSCODE}&grant_type=authorization_code";
}
