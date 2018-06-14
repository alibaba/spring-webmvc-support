package com.alibaba.spring.web.servlet.handler;

import com.alibaba.spring.web.method.HandlerMethodAnnotationResolver;
import com.alibaba.spring.web.method.HandlerMethodResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
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
import java.util.Collections;
import java.util.Map;

/**
 * {@link Annotation Annotated} {@link HandlerMethod} {@link HandlerInterceptor} Adapter
 * The implementation class must be a Spring Bean( annotated {@link Component} or {@link Bean} ).
 *
 * @author <a href="mailto:taogu.mxx@alibaba-inc.com">taogu.mxx</a> (Office)
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Annotation
 * @see HandlerMethod
 * @see HandlerInterceptor
 * @see AnnotatedHandlerMethodHandlerInterceptorAdapter
 * @since 2017.01.12
 * @deprecated 1.0.2 Use {@link AnnotatedHandlerMethodHandlerInterceptorAdapter}
 */
@Deprecated
public abstract class AnnotatedMethodHandlerInterceptorAdapter<A extends Annotation>
        extends HandlerInterceptorAdapter {

    private final WebApplicationContext webApplicationContext;

    private final Class<A> annotationType;

    private volatile boolean initialized = false;

    private volatile Map<Method, A> handlerMethodAnnotationsMap;

    public AnnotatedMethodHandlerInterceptorAdapter(WebApplicationContext webApplicationContext)
            throws IllegalStateException {

        this.webApplicationContext = webApplicationContext;

        this.annotationType = resolveAnnotationType();

        this.handlerMethodAnnotationsMap = Collections.emptyMap();

    }

    public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (!initialized) {
            init(request);
        }


        A annotation = getAnnotation(request, handler);

        if (annotation != null) {

            return preHandle(request, response, (HandlerMethod) handler, annotation);


        } else {

            return super.preHandle(request, response, handler);

        }

    }

    public final void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                                 ModelAndView modelAndView) throws Exception {

        A annotation = getAnnotation(request, handler);

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

        A annotation = getAnnotation(request, handler);

        if (annotation != null) {

            afterCompletion(request, response, (HandlerMethod) handler, annotation, ex);

        } else {

            super.afterCompletion(request, response, handler, ex);

        }

    }


    public final void afterConcurrentHandlingStarted(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        A annotation = getAnnotation(request, handler);

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

    /**
     * Initializes
     *
     * @param request {@link HttpServletRequest}
     */
    void init(HttpServletRequest request) {

        this.handlerMethodAnnotationsMap = initHandlerMethodAnnotationsMap(request);

        this.initialized = true;

    }

    /**
     * @param request {@link HttpServletRequest}
     * @return
     */
    private Map<Method, A> initHandlerMethodAnnotationsMap(HttpServletRequest request) {

        Collection<HandlerMethod> handlerMethods = resolveHandlerMethods(request);

        HandlerMethodAnnotationResolver resolver = new HandlerMethodAnnotationResolver(handlerMethods);

        Map<Method, A> handlerMethodAnnotationsMap = resolver.resolve(annotationType);

        return Collections.unmodifiableMap(handlerMethodAnnotationsMap);

    }

    private static Collection<HandlerMethod> resolveHandlerMethods(HttpServletRequest request) {

        HandlerMethodResolver resolver = new HandlerMethodResolver();

        return resolver.resolveHandlerMethods(request);

    }

    private Method getHandlerMethod(Object handler) {

        Method method = null;

        if (handler instanceof HandlerMethod) {

            HandlerMethod handlerMethod = (HandlerMethod) handler;

            method = handlerMethod.getMethod();

        }

        return method;

    }

    private A getAnnotation(HttpServletRequest request, Object handler) {

        Method method = getHandlerMethod(handler);

        return method == null ? null : handlerMethodAnnotationsMap.get(method);

    }


    private final Class<A> resolveAnnotationType() {

        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

        return (Class<A>) actualTypeArguments[0];

    }

}
