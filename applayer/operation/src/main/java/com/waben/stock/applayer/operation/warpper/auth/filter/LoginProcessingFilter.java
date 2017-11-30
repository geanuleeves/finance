package com.waben.stock.applayer.operation.warpper.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.stock.applayer.operation.exception.AuthMethodNotSupportedException;
import com.waben.stock.applayer.operation.util.WebUtil;
import com.waben.stock.applayer.operation.warpper.auth.LoginRequest;
import com.waben.stock.applayer.operation.warpper.auth.UPTypeAuthenticationToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Created by yuyidi on 2017/7/21.
 * @desc
 */
public class LoginProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private ObjectMapper objectMapper;

    public LoginProcessingFilter(String defaultFilterProcessesUrl, AuthenticationSuccessHandler successHandler,
                                 AuthenticationFailureHandler failureHandler, ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl,"POST"));
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.objectMapper = objectMapper;
        setAuthenticationSuccessHandler(successHandler);
        setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
            AuthenticationException, IOException, ServletException {
        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Authentication method not supported. Request method: " + request.getMethod());
            }
            throw new AuthMethodNotSupportedException("Authentication method not supported");
        }
        Boolean isOperater = Boolean.valueOf(request.getParameter("operater"));
        LoginRequest loginRequest = new LoginRequest(request.getParameter("username"), request.getParameter("password"));
        loginRequest.setOperator(isOperater);
//        LoginRequest loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);
        if (StringUtils.isBlank(loginRequest.getUsername()) || StringUtils.isBlank(loginRequest.getPassword())) {
            throw new AuthenticationServiceException("用户名或密码未输入");
        }
        UsernamePasswordAuthenticationToken token = new UPTypeAuthenticationToken(loginRequest.getUsername
                (), loginRequest.getPassword(),loginRequest.getOperator());
        return this.getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain
            chain, Authentication authResult) throws IOException, ServletException {
        logger.info("认证成功");
        super.successfulAuthentication(request,response,chain,authResult);
//        SecurityContextHolder.getContext().setAuthentication(authResult);
//        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
//        SecurityContextHolder.clearContext();
//        failureHandler.onAuthenticationFailure(request, response, failed);
        super.unsuccessfulAuthentication(request,response,failed);
    }
}
