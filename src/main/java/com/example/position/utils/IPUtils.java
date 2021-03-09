package com.tianhao.springcloudpayment.utils;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPUtils {
	
	private static Pattern ipv4Pattern = Pattern.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
	
	public static String getRealIP(HttpServletRequest request) {
		String ip;
		
		// 反向代理
		ip = request.getHeader("X-Forwarded-For");
		if (!StringUtils.isBlank(ip) && !ip.equals("unknown")) {
			Matcher matcher = ipv4Pattern.matcher(ip);
			if (matcher.find()) {
				String rip = matcher.group();
				return rip;
			}
		}
		
		ip = request.getHeader("X-Real-IP");
		if (!StringUtils.isBlank(ip) && !ip.equals("unknown")) {
			return ip;
		}
		
		ip = request.getHeader("Proxy-Client-IP");
		if (!StringUtils.isBlank(ip) && !ip.equals("unknown")) {
			return ip;
		}
		
		ip = request.getHeader("WL-Proxy-Client-IP");
		if (!StringUtils.isBlank(ip) && !ip.equals("unknown")) {
			return ip;
		}
		
		ip = request.getHeader("HTTP_CLIENT_IP");
		if (!StringUtils.isBlank(ip) && !ip.equals("unknown")) {
			return ip;
		}
		
		ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		if (!StringUtils.isBlank(ip) && !ip.equals("unknown")) {
			return ip;
		}
		
		ip = request.getRemoteAddr();
		return ip;
	}
}
