package com.alibaba.spring.web.handler;


import com.alibaba.spring.web.server.ServerHttpExchange;
import com.alibaba.spring.web.servlet.server.ServletServerHttpExchange;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet based implementation of {@link WebPreHandler}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see WebPreHandler
 * @since 1.0.1
 */
public abstract class ServletWebPreHandler implements WebPreHandler {

    /**
     * Pre-Handle method is implementation
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    protected abstract boolean doPreHandle(HttpServletRequest request, HttpServletResponse response)
            throws IOException;

    @Override
    public Boolean handle(ServerHttpExchange httpExchange) throws IOException {
        ServletServerHttpExchange exchange = (ServletServerHttpExchange) httpExchange;
        return doPreHandle(exchange.getServletRequest(), exchange.getServletResponse());
    }

    public boolean isAsyncSupported() {
        return false;
    }
}
