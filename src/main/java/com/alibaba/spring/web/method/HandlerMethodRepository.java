package com.alibaba.spring.web.method;

import com.alibaba.spring.beans.factory.config.GenericBeanPostProcessorAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link HandlerMethod} {@link Repository}
 *
 * @author <a href="mailto:taogu.mxx@alibaba-inc.com">taogu.mxx</a> (Office)
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see HandlerMethod
 * @see Repository
 * @see GenericBeanPostProcessorAdapter
 * @since 2017.02.01
 */
@Repository
public class HandlerMethodRepository {

    private Map<Object, HandlerMethod> handlerMethodsCache = Collections.emptyMap();

    @Autowired(required = false)
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @PostConstruct
    public void init() {

        if (requestMappingHandlerMapping != null) {

            Collection<HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods().values();

            Map<Object, HandlerMethod> handlerMethodsMap = new HashMap<Object, HandlerMethod>(handlerMethods.size());

            for (HandlerMethod handlerMethod : handlerMethods) {

                handlerMethodsMap.put(handlerMethod, handlerMethod);

            }

            handlerMethodsCache = Collections.unmodifiableMap(handlerMethodsMap);

        }

    }

    /**
     * Get All {@link HandlerMethod}s
     *
     * @return The {@link Collections#unmodifiableCollection(Collection) unmodifiable collection} of
     * {@link HandlerMethod}
     */
    public Collection<HandlerMethod> getHandlerMethods() {
        return handlerMethodsCache.values();
    }

    /**
     * Get {@link HandlerMethod} by handler object
     *
     * @param handler handler object from {@link HandlerInterceptor} method's parameter
     * @return {@link HandlerMethod} if found.
     */
    public HandlerMethod get(Object handler) {
        return handlerMethodsCache.get(handler);
    }


}
