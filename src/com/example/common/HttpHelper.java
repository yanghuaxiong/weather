package com.example.common;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

/**
 * HTTP网络操作工具类
 * @author qiujy
 */
public class HttpHelper {
	/**线程安全的*/
	private static HttpClient httpClient;
	
	static{
		if(null == httpClient){	
		    HttpParams httpParams = new BasicHttpParams();
		    HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		    HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);  //字符集
		    HttpProtocolParams.setUseExpectContinue(httpParams, true);
		    
		    ConnManagerParams.setMaxTotalConnections(httpParams, 10);  //最大连接数 
		    ConnManagerParams.setTimeout(httpParams, 60000);  //获取连接的最大等待时间
		    ConnPerRouteBean connPerRoute = new ConnPerRouteBean(8);//每个路由最大连接数
		    ConnManagerParams.setMaxConnectionsPerRoute(httpParams,connPerRoute);  
		    HttpConnectionParams.setConnectionTimeout(httpParams, 20000); //连接超时时间 
		    HttpConnectionParams.setSoTimeout(httpParams, 30000);//读取超时时间
		    SchemeRegistry schreg = new SchemeRegistry();
		    schreg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		    schreg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		    
		    // 设置接收 Cookie的策略,用与浏览器一样的策略
		    HttpClientParams.setCookiePolicy(httpParams, CookiePolicy.BROWSER_COMPATIBILITY);

		    //处理同一个HttpClient同时发出多个请求时可能发生的多线程问题  
		    ClientConnectionManager connManager = new ThreadSafeClientConnManager(httpParams, schreg);
		    httpClient = new DefaultHttpClient(connManager, httpParams);
		}
	}
	
	private HttpHelper(){}
	
	public static HttpClient getHttpClient(){
		return httpClient;
	}
	
}
