package com.alibaba.spring.web.method;

import com.alibaba.spring.beans.factory.config.GenericBeanPostProcessorAdapter;
import com.alibaba.spring.web.servlet.mvc.util.WebMvcUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Repository;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * {@link HandlerMethod} {@link Repository}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see HandlerMethod
 * @see Repository
 * @see GenericBeanPostProcessorAdapter
 * @since 2017.02.01
 */
@Repository
public class HandlerMethodRepository implements ApplicationListener<ContextRefreshedEvent> {

    private static final Map<Object, HandlerMethod> STUB_HANDLER_METHOD_CACHE = Collections.emptyMap();

    /**
     * ApplicationContext aware {@link HandlerMethod} cache
     */
    private Map<ApplicationContext, Map<Object, HandlerMethod>> allHandlerMethodsCache =
            new HashMap<ApplicationContext, Map<Object, HandlerMethod>>();

    /**
     * Get All {@link HandlerMethod}s
     *
     * @return The {@link Collections#unmodifiableCollection(Collection) unmodifiable collection} of
     * {@link HandlerMethod}
     */
    public Collection<HandlerMethod> getHandlerMethods() {

        List<HandlerMethod> allHandlerMethods = new LinkedList<HandlerMethod>();

        for (Map<Object, HandlerMethod> cache : allHandlerMethodsCache.values()) {
            allHandlerMethods.addAll(cache.values());
        }

        return allHandlerMethods;
    }

    /**
     * Get {@link HandlerMethod} by handler object
     *
     * @param handler handler object from {@link HandlerInterceptor} method's parameter
     * @return {@link HandlerMethod} if found.
     */
    public HandlerMethod get(Object handler) {
        return get(WebMvcUtils.currentRequest(), handler);
    }

    /**
     * Get {@link HandlerMethod} by handler object in specified request
     *
     * @param handler handler object from {@link HandlerInterceptor} method's parameter
     * @return {@link HandlerMethod} if found.
     */
    public HandlerMethod get(HttpServletRequest request, Object handler) {
        ApplicationContext applicationContext = WebMvcUtils.getWebApplicationContext(request);
        Map<Object, HandlerMethod> handlerMethodsCache = this.allHandlerMethodsCache.get(applicationContext);
        return handlerMethodsCache == null ? null : handlerMethodsCache.get(handler);
    }

    /**
     * Get all instances of {@link HandlerMethod} from specified {@link ApplicationContext}.
     *
     * @param applicationContext {@link ApplicationContext}
     * @return a read-only {@link Collection} of {@link HandlerMethod}
     */
    public Collection<HandlerMethod> getHandlerMethods(ApplicationContext applicationContext) {
        Map<Object, HandlerMethod> cache = allHandlerMethodsCache.get(applicationContext);
        return cache == null ? Collections.<HandlerMethod>emptyList() : cache.values();
    }

    /**
     * Replace STUB_HANDLER_METHOD_CACHE to be actual when current {@link ApplicationContext} is initialized
     *
     * @param event {@link ContextRefreshedEvent}
     * @since 1.0.1
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        // current {@link ApplicationContext}
        ApplicationContext applicationContext = event.getApplicationContext();

        Map<Object, HandlerMethod> handlerMethodsCache = new HashMap<Object, HandlerMethod>();

        Map<String, RequestMappingHandlerMapping> mappings =
                applicationContext.getBeansOfType(RequestMappingHandlerMapping.class);

        for (RequestMappingHandlerMapping mapping : mappings.values()) {
            for (HandlerMethod handlerMethod : mapping.getHandlerMethods().values()) {
                handlerMethodsCache.put(handlerMethod, handlerMethod);
            }
        }

        // Immutable cache
        handlerMethodsCache = Collections.unmodifiableMap(handlerMethodsCache);

        // Update cache
        this.allHandlerMethodsCache.put(applicationContext, handlerMethodsCache);

    }
}
