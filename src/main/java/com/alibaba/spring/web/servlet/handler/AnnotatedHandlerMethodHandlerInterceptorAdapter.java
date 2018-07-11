package com.alibaba.spring.web.servlet.handler;

import com.alibaba.spring.web.method.HandlerMethodRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * {@link Annotation Annotated} {@link HandlerMethod} {@link HandlerInterceptor} Adapter
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Annotation
 * @see HandlerMethod
 * @see HandlerInterceptor
 * @since 2017.06.21
 */
public abstract class AnnotatedHandlerMethodHandlerInterceptorAdapter<A extends Annotation> extends
        WebMvcConfigurerAdapter implements AsyncHandlerInterceptor, ApplicationListener<ContextRefreshedEvent> {

    private final Class<A> annotationType;

    private final Map<Method, A> annotatedMethodsCache;

    private final boolean autoRegistered;

    private ApplicationContext applicationContext;

    /**
     * Default Constructor with auto-registering into {@link HandlerInterceptor} chain If current instance is declared
     * as a Spring Bean.
     *
     * @see #AnnotatedHandlerMethodHandlerInterceptorAdapter(boolean)
     */
    protected AnnotatedHandlerMethodHandlerInterceptorAdapter() {
        this(true);
    }

    /**
     * @param autoRegistered If <code>true</code>, that means current instance as a Spring Bean will be registered into
     *                       {@link HandlerInterceptor} chain.
     */
    protected AnnotatedHandlerMethodHandlerInterceptorAdapter(boolean autoRegistered) {

        annotationType = resolveAnnotationType();

        annotatedMethodsCache = new HashMap<Method, A>();

        this.autoRegistered = autoRegistered;
    }

    @Override
    public final void addInterceptors(InterceptorRegistry registry) {
        // Add Itself if autoRegistered is true
        if (autoRegistered) {
            registry.addInterceptor(this);
        }
    }

    public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        A annotation = getAnnotation(handler);

        if (annotation != null) {

            return preHandle(request, response, (HandlerMethod) handler, annotation);

        }

        return true;

    }

    public final void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                                 ModelAndView modelAndView) throws Exception {

        A annotation = getAnnotation(handler);

        if (annotation != null) {

            postHandle(request, response, (HandlerMethod) handler, annotation, modelAndView);

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

        }

    }

    public final void afterConcurrentHandlingStarted(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        A annotation = getAnnotation(handler);

        if (annotation != null) {

            afterConcurrentHandlingStarted(request, response, (HandlerMethod) handler, annotation);

        }

    }

    /**
     * This implementation is empty.
     *
     * @param request       {@link HttpServletRequest}
     * @param response      {@link HttpServletResponse}
     * @param handlerMethod Spring MVC {@link HandlerMethod}
     * @param annotation    {@link A Annotation} instance
     * @return {@code true} if the execution chain should proceed with the
     * next interceptor or the handler itself. Else, DispatcherServlet assumes
     * that this interceptor has already dealt with the response itself.
     * @throws Exception execution {@link Exception}
     */
    protected boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                HandlerMethod handlerMethod, A annotation) throws Exception {

        return true;

    }

    /**
     * This implementation is empty.
     *
     * @param request       {@link HttpServletRequest}
     * @param response      {@link HttpServletResponse}
     * @param handlerMethod Spring MVC {@link HandlerMethod}
     * @param annotation    {@link A Annotation} instance
     * @param modelAndView  Spring MVC {@link ModelAndView}
     * @throws Exception execution {@link Exception}
     */
    protected void postHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod,
                              A annotation, ModelAndView modelAndView) throws Exception {

    }

    /**
     * This implementation is empty.
     *
     * @param request       {@link HttpServletRequest}
     * @param response      {@link HttpServletResponse}
     * @param handlerMethod Spring MVC {@link HandlerMethod}
     * @param annotation    {@link A Annotation} instance
     * @param ex            {@link HandlerMethod} execution {@link Exception}
     * @throws Exception execution {@link Exception}
     */
    protected void afterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod,
                                   A annotation, Exception ex) throws Exception {

    }

    /**
     * This implementation is empty.
     *
     * @param request       {@link HttpServletRequest}
     * @param response      {@link HttpServletResponse}
     * @param handlerMethod Spring MVC {@link HandlerMethod}
     * @param annotation    {@link A Annotation} instance
     * @throws Exception execution {@link Exception}
     */
    protected void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response,
                                                  HandlerMethod handlerMethod, A annotation) throws Exception {

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        ApplicationContext applicationContext = event.getApplicationContext();

        this.applicationContext = applicationContext;

        HandlerMethodRepository repository = new HandlerMethodRepository();

        repository.onApplicationEvent(event);

        Collection<HandlerMethod> handlerMethods = repository.getHandlerMethods(applicationContext);

        initHandlerMethods(handlerMethods);

    }

    /**
     * Annotation type
     *
     * @return non-null
     */
    public final Class<A> getAnnotationType() {
        return annotationType;
    }

    private final Class<A> resolveAnnotationType() {

        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

        return (Class<A>) actualTypeArguments[0];

    }

    /**
     * Get an annotated {@link Annotation} instance from somewhere,e.g {@link HandlerMethod}
     *
     * @param handler {@link HandlerMethod} or {@link Method}
     * @return {@link Annotation}
     */
    protected A getAnnotation(Object handler) {

        Method method = null;

        if (handler instanceof HandlerMethod) {
            method = ((HandlerMethod) handler).getMethod();
        } else if (handler instanceof Method) {
            method = (Method) handler;
        }

        return method == null ? null : annotatedMethodsCache.get(method);
    }

    /**
     * Find all annotated {@link Method} in current {@link ApplicationContext}
     *
     * @return {@link Method} Set
     */
    protected Set<Method> getAnnotatedMethods() {
        return annotatedMethodsCache.keySet();
    }

    /**
     * Get a {@link ApplicationContext} instance
     *
     * @return a {@link ApplicationContext} instance
     */
    protected ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    private void initHandlerMethods(Collection<HandlerMethod> handlerMethods) {

        for (HandlerMethod handlerMethod : handlerMethods) {

            Method method = handlerMethod.getMethod();

            // Find @Annotation from Method first
            A annotation = findAnnotation(method, annotationType);

            // If Absent , try to find @Annotation from Bean Type later

            if (annotation == null) {

                Class<?> beanType = handlerMethod.getBeanType();

                annotation = findAnnotation(beanType, annotationType);

            }

            if (annotation != null) {

                initAnnotatedMethodsCache(handlerMethod, annotation);

                initHandlerMethod(handlerMethod, annotation);

            }
        }
    }

    private void initAnnotatedMethodsCache(HandlerMethod handlerMethod, A annotation) {

        Method method = handlerMethod.getMethod();

        annotatedMethodsCache.put(method, annotation);
    }

    /**
     * Customize to initialize {@link HandlerMethod} and {@link A annotation} on Sub-Class If requried.
     *
     * @param handlerMethod {@link HandlerMethod} a handler method annotated {@link A}
     * @param annotation    {@link A annotation} annotated on handlerMethod
     */
    protected void initHandlerMethod(HandlerMethod handlerMethod, A annotation) {

    }


}
