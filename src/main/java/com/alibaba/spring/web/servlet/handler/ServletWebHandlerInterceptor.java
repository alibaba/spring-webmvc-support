package com.alibaba.spring.web.servlet.handler;

import com.alibaba.spring.web.handler.WebHandler;
import com.alibaba.spring.web.handler.WebPreHandler;
import com.alibaba.spring.web.handler.annotation.WebHandlers;
import com.alibaba.spring.web.server.ServerHttpExchange;
import com.alibaba.spring.web.servlet.server.ServletServerHttpExchange;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

import static java.lang.reflect.Modifier.isAbstract;
import static org.springframework.beans.factory.BeanFactoryUtils.beansOfTypeIncludingAncestors;
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

    private final Map<Method, Set<WebPreHandler>> handlerMethodWebPreHandlers =
            new HashMap<Method, Set<WebPreHandler>>();

    @Override
    protected void initHandlerMethod(HandlerMethod handlerMethod, WebHandlers webHandlers) {

        Set<Class<? extends WebHandler>> webHandlerClasses = resolveWebHandlerClasses(webHandlers);

        initWebPreHandlers(handlerMethod, webHandlerClasses);

    }

    private Set<Class<? extends WebHandler>> resolveWebHandlerClasses(WebHandlers webHandlers) {

        Set<Class<? extends WebHandler>> webHandlerClasses = new LinkedHashSet<Class<? extends WebHandler>>();

        for (Class<? extends WebHandler> webHandlerClass : webHandlers.value()) {
            assertWebHandlerClass(webHandlerClass);
            webHandlerClasses.add(webHandlerClass);
        }

        return webHandlerClasses;
    }

    private static void assertWebHandlerClass(Class<? extends WebHandler> webHandlerClass) {

        Assert.isTrue(!webHandlerClass.isInterface(),
                "@WebHandlers.value() attribute must not be an interface!");
        Assert.isTrue(!isAbstract(webHandlerClass.getModifiers()),
                "@WebHandlers.value() attribute must not be an abstract class!");

    }

    private Set<WebPreHandler> filterWebPreHandlers(Set<Class<? extends WebHandler>> webHandlerClasses) {

        List<WebPreHandler> webPreHandlers = new LinkedList<WebPreHandler>();

        for (Class<? extends WebHandler> webHandlerClass : webHandlerClasses) {

            if (ClassUtils.isAssignable(WebPreHandler.class, webHandlerClass)) { // WebPreHandler
                Class<? extends WebPreHandler> webPreHandlerClass = (Class<? extends WebPreHandler>) webHandlerClass;
                webPreHandlers.addAll(beansOfTypeIncludingAncestors(getApplicationContext(), webPreHandlerClass).values());
            }

        }

        // sort
        sort(webPreHandlers);

        // remove duplicated instances
        return new LinkedHashSet<WebPreHandler>(webPreHandlers);
    }

    private void initWebPreHandlers(HandlerMethod handlerMethod, Set<Class<? extends WebHandler>> webHandlerClasses) {

        Set<WebPreHandler> webPreHandlers = filterWebPreHandlers(webHandlerClasses);

        Method method = handlerMethod.getMethod();
        handlerMethodWebPreHandlers.put(method, webPreHandlers);

    }

    @Override
    protected boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                HandlerMethod handlerMethod, WebHandlers annotation) throws Exception {

        Set<WebPreHandler> webPreHandlers = getWebPreHandlers(handlerMethod);

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

    private Set<WebPreHandler> getWebPreHandlers(HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        return handlerMethodWebPreHandlers.get(method);
    }

}
