package com.alibaba.spring.web.servlet.handler;

import com.alibaba.spring.web.handler.WebHandler;
import com.alibaba.spring.web.handler.WebPreHandler;
import com.alibaba.spring.web.handler.annotation.WebHandlers;
import com.alibaba.spring.web.server.ServerHttpExchange;
import com.alibaba.spring.web.servlet.server.ServletServerHttpExchange;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.alibaba.spring.util.BeanUtils.getSortedBeans;
import static java.util.Arrays.asList;
import static org.springframework.core.annotation.AnnotationAwareOrderComparator.sort;

/**
 * Annotation based for {@link WebHandler} {@link HandlerInterceptor} based on Servlet
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see WebHandler
 * @see HandlerInterceptor
 * @since 1.0.1
 */
public class ServletWebHandlerInterceptor extends AnnotatedHandlerMethodHandlerInterceptorAdapter<WebHandlers> {

    private final Map<HandlerMethod, List<WebPreHandler>> handlerMethodWebPreHandlers =
            new HashMap<HandlerMethod, List<WebPreHandler>>();

    @Override
    protected void initHandlerMethod(HandlerMethod handlerMethod, WebHandlers webHandlers) {

        Set<Class<? extends WebHandler>> webHandlerClasses =
                new HashSet<Class<? extends WebHandler>>(asList(webHandlers.value()));

        List<WebPreHandler> webPreHandlers = new LinkedList<WebPreHandler>();

        for (Class<? extends WebHandler> webHandlerClass : webHandlerClasses) {

            if (ClassUtils.isAssignable(WebPreHandler.class,webHandlerClass)) { // WebPreHandler
                webPreHandlers.addAll(getSortedBeans(getApplicationContext(), WebPreHandler.class));
            }

        }

        initWebPreHandlers(handlerMethod, webPreHandlers);

    }

    private void initWebPreHandlers(HandlerMethod handlerMethod, List<WebPreHandler> webPreHandlers) {
        sort(webPreHandlers);
        handlerMethodWebPreHandlers.put(handlerMethod, webPreHandlers);
    }

    @Override
    protected boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                HandlerMethod handlerMethod, WebHandlers annotation) throws Exception {

        List<WebPreHandler> webPreHandlers = getWebPreHandlers(handlerMethod);

        Boolean accepted = null;

        ServerHttpExchange httpExchange = new ServletServerHttpExchange(request, response, getApplicationContext());

        for (WebPreHandler webPreHandler : webPreHandlers) {

            accepted = webPreHandler.handle(httpExchange);

            if (Boolean.TRUE.equals(accepted)) {

            } else {
                accepted = false;
                break;
            }
        }

        return accepted;

    }

    private List<WebPreHandler> getWebPreHandlers(Object handlerMethod) {
        return handlerMethodWebPreHandlers.get(handlerMethod);
    }

}
