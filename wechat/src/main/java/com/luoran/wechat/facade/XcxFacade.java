package com.luoran.wechat.facade;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

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
	
	
	 /***
     * @Author WSL
     * @Description //TODO  请求微信接口的二维码
     **/
    public String getWeixinCode() {
    	refreshToken();
    	String url = XcxGetWXACodeUnlimit.replaceAll("\\$\\{ACCESS_TOKEN\\}", wechatCache.getXcxAccessToken());
        //设置请求二维码的参数
        JSONObject param = new JSONObject();
        param.put("path", "pages/init/index/index");//二维码扫描传输的路劲
        param.put("width", 430);//二维码宽度
        param.put("scene", "1234567890qwertyuiopasdfghjklzxc");//传的参数 传的参数只能限制为32位参数
        param.put("auto_color", false);
        param.put("is_hyaline", true);
        PrintWriter out = null;
        String result = "";
        InputStream inputStream = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("Content-Type", "application/json;charset-gbk");
            conn.setRequestProperty("responseType", "arraybuffer");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            //获取流数据
            inputStream = conn.getInputStream();
            // 将获取流转为base64格式
            byte[] data = null;
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
            result = new String(Base64.getEncoder().encode(data));
            //当import java.util.Base64;无法导入时，只能在网上找找其他的jar包，写法换成下面这种
            //result = new String(Base64.encodeBase64(data));
            //qrcode = ReadUrlUtil.sendPost(requestUrl, param);// 获取返回值
//            logger.info("获取wxacodeunlimit的内容为: {}", result);
        } catch (Exception e) {
        	logger.error("与微信服务器通讯异常：" + e.getMessage(), e.getCause());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            	logger.error(e.getMessage(), e.getCause());
            }
        }
        return result;
    }
}
