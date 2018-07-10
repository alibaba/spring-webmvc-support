package com.alibaba.spring.web.handler;

import com.alibaba.spring.web.server.ServerHttpExchange;

import java.io.IOException;

/**
 * WebHandler is a functional interface, the implementation of {@link ServerHttpExchange} may be
 * based on Servlet Spec.
 *
 * @param <R> the result type after handling.
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ServletWebPreHandler
 * @since 1.0.1
 */
public interface WebHandler<R> {

    /**
     * Handle HTTP request and return a result
     *
     * @param httpExchange {@link ServerHttpExchange} instance
     * @return handled result
     * @throws IOException
     */
    R handle(ServerHttpExchange httpExchange) throws IOException;

    /**
     * Is Async supported or not.
     *
     * @return If supported, return <code>true</code>, or <code>false</code>.
     */
    boolean isAsyncSupported();

}
