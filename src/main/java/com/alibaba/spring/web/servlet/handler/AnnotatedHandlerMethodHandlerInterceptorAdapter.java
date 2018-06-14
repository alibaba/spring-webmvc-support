package com.alibaba.spring.web.servlet.handler;

import com.alibaba.spring.web.method.HandlerMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link Annotation Annotated} {@link HandlerMethod} {@link HandlerInterceptor} Adapter
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Annotation
 * @see HandlerMethod
 * @see HandlerInterceptor
 * @since 2017.06.21
 */
public class AnnotatedHandlerMethodHandlerInterceptorAdapter<A extends Annotation> extends HandlerInterceptorAdapter {

    private final Class<A> annotationType;

    private final ConcurrentMap<Method, A> annotatedMethodsCache;

    protected AnnotatedHandlerMethodHandlerInterceptorAdapter() {

        annotationType = resolveAnnotationType();

        annotatedMethodsCache = new ConcurrentHashMap<Method, A>();

    }

    public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        A annotation = getAnnotation(handler);

        if (annotation != null) {

            return preHandle(request, response, (HandlerMethod) handler, annotation);


        } else {

            return super.preHandle(request, response, handler);

        }

    }

    public final void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                                 ModelAndView modelAndView) throws Exception {

        A annotation = getAnnotation(handler);

        if (annotation != null) {

            postHandle(request, response, (HandlerMethod) handler, annotation, modelAndView);

        } else {

            super.postHandle(request, response, handler, modelAndView);

        }

    }

    /**
     * This implementation is empty.
     */
    public final void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

        A annotation = getAnnotation(handler);

        if (annotation != null) {

            afterCompletion(request, response, (HandlerMethod) handler, annotation, ex);

        } else {

            super.afterCompletion(request, response, handler, ex);

        }

    }

    public final void afterConcurrentHandlingStarted(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        A annotation = getAnnotation(handler);

        if (annotation != null) {

            afterConcurrentHandlingStarted(request, response, (HandlerMethod) handler, annotation);

        } else {

            super.afterConcurrentHandlingStarted(request, response, handler);

        }

    }

    /**
     * This implementation is empty.
     *
     * @param request
     * @param response
     * @param handlerMethod
     * @param annotation
     * @return
     * @throws Exception
     */
    protected boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                HandlerMethod handlerMethod, A annotation) throws Exception {

        return true;

    }

    /**
     * This implementation is empty.
     *
     * @param request
     * @param response
     * @param handlerMethod
     * @param annotation
     * @param modelAndView
     * @throws Exception
     */
    protected void postHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod,
                              A annotation, ModelAndView modelAndView) throws Exception {

    }

    /**
     * This implementation is empty.
     *
     * @param request
     * @param response
     * @param handlerMethod
     * @param annotation
     * @param ex
     * @throws Exception
     */
    protected void afterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod,
                                   A annotation, Exception ex) throws Exception {

    }

    /**
     * This implementation is empty.
     *
     * @param request
     * @param response
     * @param handlerMethod
     * @param annotation
     * @throws Exception
     */
    protected void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response,
                                                  HandlerMethod handlerMethod, A annotation) throws Exception {

    }

    /**
     * Annotation type
     *
     * @return non-null
     */
    public final Class<A> getAnnotationType() {
        return annotationType;
    }

    private static Collection<HandlerMethod> resolveHandlerMethods(HttpServletRequest request) {

        HandlerMethodResolver resolver = new HandlerMethodResolver();

        return resolver.resolveHandlerMethods(request);

    }

    private A getAnnotation(Object handler) {

        A annotation = null;

        if (handler instanceof HandlerMethod) {

            HandlerMethod handlerMethod = (HandlerMethod) handler;

            Method method = handlerMethod.getMethod();

            annotation = annotatedMethodsCache.get(method);

            if (annotation == null) {

                annotation = AnnotationUtils.findAnnotation(method, annotationType);

                if (annotation == null) {

                    annotation = AnnotationUtils.findAnnotation(method.getDeclaringClass(), annotationType);

                }

                if (annotation != null) {

                    annotatedMethodsCache.putIfAbsent(method, annotation);

                }

            }

        }

        return annotation;

    }

    private final Class<A> resolveAnnotationType() {

        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

        return (Class<A>) actualTypeArguments[0];

    }

}
