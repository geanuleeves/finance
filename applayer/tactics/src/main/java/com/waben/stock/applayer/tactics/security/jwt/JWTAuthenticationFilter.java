package com.waben.stock.applayer.tactics.security.jwt;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.waben.stock.applayer.tactics.security.CustomUserDetails;
import com.waben.stock.applayer.tactics.service.JedisCache;

public class JWTAuthenticationFilter extends GenericFilterBean {

	private JedisCache jedisCache;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		// 获取请求中的token
		String token = httpRequest.getHeader(JWTTokenUtil.HEADER_STRING);
		if (token != null && !"".equals(token)) {
			// 获取token中的信息
			try {
				Map<String, Object> tokenInfo = JWTTokenUtil.getTokenInfo(token);
				String username = (String) tokenInfo.get("sub");
				// 判断该token是否为最新登陆的token
				String cacheToken = jedisCache.getToken(username);
				if (cacheToken != null && !"".equals(cacheToken) && !cacheToken.equals(token)) {
					httpRequest.getSession().invalidate();
					HttpServletResponse httpResponse = (HttpServletResponse) response;
					httpResponse.setStatus(HttpStatus.FORBIDDEN.value());
					SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
				} else {
					// 判断username是否存在，以及token是否过期
					Long userId = new Long((Integer) tokenInfo.get("userId"));
					String serialCode = (String) tokenInfo.get("serialCode");
					if (username != null && !"".equals(username)) {
						Date exp = (Date) tokenInfo.get("exp");
						if (exp != null && exp.getTime() * 1000 > System.currentTimeMillis()) {
							// 如果为正确的token，将身份验证放入上下文中
							List<GrantedAuthority> authorities = AuthorityUtils
									.commaSeparatedStringToAuthorityList((String) tokenInfo.get("authorities"));
							CustomUserDetails userDeatails = new CustomUserDetails(userId, serialCode, username, null,
									authorities);
							UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
									username, null, authorities);
							authentication.setDetails(userDeatails);
							SecurityContextHolder.getContext().setAuthentication(authentication);
						}
					}
				}
			} catch (Exception ex) {
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.setStatus(HttpStatus.FORBIDDEN.value());
			}
		}

		filterChain.doFilter(request, response);
	}

	public void setJedisCache(JedisCache jedisCache) {
		this.jedisCache = jedisCache;
	}

}