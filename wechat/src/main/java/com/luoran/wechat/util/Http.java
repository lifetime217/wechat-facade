package com.luoran.wechat.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * @author lifetime
 *
 */
public final class Http {

	private static PoolingHttpClientConnectionManager clientConnectionManager;
	private static final int MaxTotalPool = 200;
	private static final int MaxConPerRoute = 50;
	private static final int SocketTimeout = 2000;
	private static final int ConnectionRequestTimeout = 3000;
	private static final int ConnectTimeout = 1000;

	static {
		clientConnectionManager = new PoolingHttpClientConnectionManager();
		clientConnectionManager.setMaxTotal(MaxTotalPool);
		clientConnectionManager.setDefaultMaxPerRoute(MaxConPerRoute);
		SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(SocketTimeout).build();
		clientConnectionManager.setDefaultSocketConfig(socketConfig);
	}

	public static CloseableHttpClient getConnection() {
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(ConnectionRequestTimeout).setConnectTimeout(ConnectTimeout).build();
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(clientConnectionManager).setDefaultRequestConfig(requestConfig).build();
		if (clientConnectionManager != null && clientConnectionManager.getTotalStats() != null) {
			System.out.println("HttpClient Pool Status : " + clientConnectionManager.getTotalStats().toString());
		}
		return httpClient;
	}

	public static final String get(String url) throws Exception {
		CloseableHttpClient httpclient = getConnection();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse resp = httpclient.execute(httpGet);
		HttpEntity entity = resp.getEntity();
		return EntityUtils.toString(entity, "UTF-8");
	}

	public static final String post(String url) throws Exception {
		return post(url, null);
	}

	public static final String post(String url, Map<String, String> params) throws Exception {
		CloseableHttpClient httpclient = getConnection();
		HttpPost post = new HttpPost(url);
		if (params != null && !params.isEmpty()) {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			for (Iterator<Entry<String, String>> it = params.entrySet().iterator(); it.hasNext();) {
				Entry<String, String> entry = it.next();
				BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
				parameters.add(pair);
			}
			post.setEntity(new UrlEncodedFormEntity(parameters, Charset.forName("UTF-8")));
		}
		CloseableHttpResponse resp = httpclient.execute(post);
		return EntityUtils.toString(resp.getEntity(), "UTF-8");
	}

	public static final String postJson(String url, String json) throws Exception {
		CloseableHttpClient httpclient = getConnection();
		HttpPost post = new HttpPost(url);
		if (json == null) {
			return post(url);
		}
		post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
		CloseableHttpResponse resp = httpclient.execute(post);
		return EntityUtils.toString(resp.getEntity(), "UTF-8");
	}
	
	

}
