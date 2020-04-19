package com.technicalinterest.group.interceptor;

import com.alibaba.fastjson.JSON;
import com.technicalinterest.group.api.vo.ApiResult;
import com.technicalinterest.group.service.Enum.ResultEnum;
import com.technicalinterest.group.service.util.IpAdrressUtil;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @package: com.technicalinterest.group.interceptor
 * @className: RateLimitInterceptor
 * @description: ip+url 令牌桶限流
 * @author: Shuyu.Wang
 * @date: 2020-04-19 16:38
 * @since: 0.1
 **/
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {
	/**
	 * 桶的最大容量
	 */
	int capacity=1;
	/**
	 * 令牌 的补充量
	 */
	int refillTokens=1;

	/**
	 * 补充令牌的时间间隔
	 */
	Duration refillDuration=Duration.ofSeconds(1);

	private static final Map<String, Bucket> cache = new ConcurrentHashMap<>();

	private Bucket createBucket() {
		//
		Refill refill = Refill.of(refillTokens, refillDuration);
		Bandwidth limit = Bandwidth.classic(capacity, refill);
		return Bucket4j.builder().addLimit(limit).build();
	}

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
		String ip = IpAdrressUtil.getIpAdrress(httpServletRequest);
		String uri=httpServletRequest.getRequestURI();
		Bucket bucket = cache.computeIfAbsent(ip+uri, k -> createBucket());
		if (bucket.tryConsume(1)) {
			return true;
		} else {
			log.info("ip:{}  uri:{}  请求过于频繁，被限制请求",ip,uri);
			ApiResult result = new ApiResult(ResultEnum.REQ_FREQUENT);
			returnJson(httpServletResponse, JSON.toJSONString(result));
			return false;
		}
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

	}

	private void returnJson(HttpServletResponse response, String json) throws Exception {
		PrintWriter writer = null;
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			log.error("LoginInterceptor response error ---> {}", e.getMessage(), e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
