package com.alibaba.spring.web.util;

import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.util.List;
import java.util.Map;

/**
 * {@link HandlerMethod} Utilities
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see HandlerMethod
 * @since 2017.01.12
 */
public abstract class HandlerMethodUtils {


    /**
     * Find specified {@link Annotation} type maps from {@link HandlerMethod}
     *
     * @param handlerMethod   {@link HandlerMethod}
     * @param annotationClass {@link Annotation} type
     * @param <A>             {@link Annotation} type
     * @return {@link Annotation} type maps , the {@link ElementType} as key ,
     * the list of {@link Annotation} as value.
     * If {@link Annotation} was annotated on {@link HandlerMethod}'s parameters{@link ElementType#PARAMETER} ,
     * the associated {@link Annotation} list may contain multiple elements.
     */
    public static <A extends Annotation> Map<ElementType, List<A>> findAnnotations(HandlerMethod handlerMethod,
                                                                                   Class<A> annotationClass) {
        return com.alibaba.spring.util.AnnotationUtils.findAnnotations(handlerMethod.getMethod(), annotationClass);
    }

    /**
     * Find {@link Annotation} from {@link MethodParameter method parameter or method return type}
     *
     * @param methodParameter {@link MethodParameter method parameter or method return type}
     * @param annotationClass {@link Annotation} type
     * @param <A>             {@link Annotation} type
     * @return {@link Annotation} If found , return <code>null</code>
     */
    public static <A extends Annotation> A findAnnotation(MethodParameter methodParameter, Class<A> annotationClass) {

        A annotation = null;

        boolean isReturnType = methodParameter.getParameterIndex() < 0;

        if (isReturnType) {

            annotation = methodParameter.getMethodAnnotation(annotationClass);

        } else { // is parameter

            annotation = methodParameter.getParameterAnnotation(annotationClass);

        }


        return annotation;

    }
}
