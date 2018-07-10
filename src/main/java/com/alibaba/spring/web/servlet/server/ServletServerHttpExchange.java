package com.alibaba.spring.web.servlet.server;

import com.alibaba.spring.web.server.ServerHttpExchange;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet {@link ServerHttpExchange} implementation.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.1
 */
public class ServletServerHttpExchange implements ServerHttpExchange {

    private final HttpServletRequest servletRequest;

    private final HttpServletResponse servletResponse;

    private final ServerHttpRequest request;

    private final ServerHttpResponse response;

    private final ApplicationContext applicationContext;

    public ServletServerHttpExchange(HttpServletRequest request, HttpServletResponse response,
                                     ApplicationContext applicationContext) {
        this.servletRequest = request;
        this.servletResponse = response;
        this.request = new ServletServerHttpRequest(request);
        this.response = new ServletServerHttpResponse(response);
        this.applicationContext = applicationContext;
    }

    @Override
    public ServerHttpRequest getRequest() {
        return request;
    }

    @Override
    public ServerHttpResponse getResponse() {
        return response;
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new LinkedHashMap<String, Object>();
        List<String> attributeNames = Collections.list(servletRequest.getAttributeNames());
        for (String attributeName : attributeNames) {
            attributes.put(attributeName, servletRequest.getAttribute(attributeName));
        }
        return attributes;
    }

    @Override
    public <T> T getAttribute(String name) {
        return (T) servletRequest.getAttribute(name);
    }

    @Override
    public ServerHttpExchange setAttribute(String name, Object value) {
        servletRequest.setAttribute(name, value);
        return this;
    }

    @Override
    public LocaleContext getLocaleContext() {
        return LocaleContextHolder.getLocaleContext();
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }
}
