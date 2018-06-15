package com.alibaba.spring.web.method;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Repository;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link HandlerMethod} {@link Annotation} Resolver
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see HandlerMethod
 * @see Annotation
 * @see Repository
 * @since 2017.02.02
 */
public class HandlerMethodAnnotationResolver {

    private final Collection<HandlerMethod> handlerMethods;

    public HandlerMethodAnnotationResolver(Collection<HandlerMethod> handlerMethods) {
        this.handlerMethods = handlerMethods;
    }

    /**
     * Resolve {@link Method} and {@link Annotation} {@link Map} by specified {@link Annotation} type.
     *
     * @param annotationType {@link Annotation} {@link Class}
     * @param <A>            {@link Annotation} Type
     * @return non-null {@link Collections#unmodifiableMap(Map) unmodifiable map}
     */
    public <A extends Annotation> Map<Method, A> resolve(Class<A> annotationType) {

        Map<Method, A> resolvedHandlerMethods = new LinkedHashMap<Method, A>();

        for (HandlerMethod handlerMethod : handlerMethods) {

            A annotation = handlerMethod.getMethodAnnotation(annotationType);

            if (annotation == null) {

                annotation = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), annotationType);

            }

            if (annotation != null) {

                resolvedHandlerMethods.put(handlerMethod.getMethod(), annotation);

            }

        }

        return Collections.unmodifiableMap(resolvedHandlerMethods);

    }


}
