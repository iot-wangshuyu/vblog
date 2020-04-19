package com.technicalinterest.group.interceptor;

import com.technicalinterest.group.properties.RefererProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;

/**
 * @package: com.technicalinterest.group.interceptor
 * @className: RefererInterceptor
 * @description: 防盗链连接器
 * @author: Shuyu.Wang
 * @date: 2020-04-19 21:58
 * @since: 0.1
 **/

public class RefererInterceptor extends HandlerInterceptorAdapter {
		// URL匹配器
		private AntPathMatcher matcher = new AntPathMatcher();
		@Autowired
		private RefererProperties properties;
		@Override
		public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
			String referer = req.getHeader("referer");
			String host = req.getServerName();
			// 只验证POST请求
			if ("POST".equals(req.getMethod())|| "GET".equals(req.getMethod())) {
				if (referer == null) {
					// 状态置为404
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return false;
				}
				java.net.URL url = null;
				try {
					url = new java.net.URL(referer);
				} catch (MalformedURLException e) {
					// URL解析异常，也置为404
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return false;
				}
				// 首先判断请求域名和referer域名是否相同
				if (!host.equals(url.getHost())) {
					// 如果不等，判断是否在白名单中
					if (properties.getRefererDomain() != null) {
						for (String s : properties.getRefererDomain()) {
							if (s.equals(url.getHost())) {
								return true;
							}
						}
					}
					return false;
				}
			}
			return true;
		}

}
