package com.alibaba.spring.web.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.LocaleContextResolver;

import java.util.Collections;
import java.util.Map;

/**
 * Exchange interface for an HTTP request-response interaction.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.1
 */
public interface ServerHttpExchange {

    /**
     * Return the current HTTP request.
     */
    ServerHttpRequest getRequest();

    /**
     * Return the current HTTP response.
     */
    ServerHttpResponse getResponse();

    /**
     * Return a immutable map of request attributes for the current exchange.
     *
     * @see Collections#unmodifiableMap(Map)
     */
    Map<String, Object> getAttributes();

    /**
     * Return the request attribute value if present.
     *
     * @param name the attribute name
     * @param <T>  the attribute type
     * @return the attribute value
     */
    <T> T getAttribute(String name);

    /**
     * Set attribute into request attributes for the current exchange.
     *
     * @param name  the attribute name
     * @param value the attribute value
     * @return {@link ServerHttpExchange}
     */
    ServerHttpExchange setAttribute(String name, Object value);

    /**
     * Return the {@link LocaleContext} using the configured
     * {@link LocaleContextResolver}.
     */
    LocaleContext getLocaleContext();

    /**
     * Return the {@link ApplicationContext} associated with the web application
     *
     * @see ApplicationContext
     */
    ApplicationContext getApplicationContext();

}
