package com.technicalinterest.group.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @package: com.technicalinterest.group
 * @className: properties
 * @description:
 * @author: Shuyu.Wang
 * @date: 2020-04-19 22:03
 * @since: 0.1
 **/
@Data
@Component
@ConfigurationProperties(prefix = "referer")
public class RefererProperties {
	// 白名单域名
	private List<String> refererDomain;
}
