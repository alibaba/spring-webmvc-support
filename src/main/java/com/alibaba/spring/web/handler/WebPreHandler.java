package com.alibaba.spring.web.handler;

import com.alibaba.spring.web.server.ServerHttpExchange;

import java.io.IOException;

/**
 * WebPreHandler is a functional interface for Pre-Handle
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ServletWebPreHandler
 * @since 1.0.0
 */
public interface WebPreHandler extends WebHandler<Boolean> {

    /**
     * Pre-Handle HTTP request, e.g To parse parameters, validate, Encapsulate
     *
     * @param httpExchange {@link ServerHttpExchange} instance
     * @return if valid , return <code>true</code>, or <code>false</code>
     * @throws IOException
     */
    @Override
    Boolean handle(ServerHttpExchange httpExchange) throws IOException;

}
