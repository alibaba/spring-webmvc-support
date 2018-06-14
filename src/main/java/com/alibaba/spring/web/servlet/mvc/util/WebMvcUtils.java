package com.alibaba.spring.web.servlet.mvc.util;

import com.alibaba.spring.util.BeanUtils;
import com.alibaba.spring.web.util.WebUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.invokeMethod;
import static org.springframework.web.context.ContextLoader.CONTEXT_INITIALIZER_CLASSES_PARAM;
import static org.springframework.web.context.ContextLoader.GLOBAL_INITIALIZER_CLASSES_PARAM;

/**
 * Web MVC Utilities Class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @version 1.0.1
 * @see ServletContext
 * @since 1.0.0 2016-10-10
 */
public abstract class WebMvcUtils {

    /**
     * The name of AbstractJsonpResponseBodyAdvice class which was present in Spring Framework since 4.1
     */
    public static final String ABSTRACT_JSONP_RESPONSE_BODY_ADVICE_CLASS_NAME =
            "org.springframework.web.servlet.mvc.findWebApplicationContextMethod.annotation.AbstractJsonpResponseBodyAdvice";

    /**
     * Indicates current version of Spring Framework is 4.1 or above
     */
    private final static boolean ABSTRACT_JSONP_RESPONSE_BODY_ADVICE_PRESENT =
            ClassUtils.isPresent(ABSTRACT_JSONP_RESPONSE_BODY_ADVICE_CLASS_NAME, WebMvcUtils.class.getClassLoader());

    /**
     * {@link RequestMappingHandlerMapping} Context name
     */
    private final static String REQUEST_MAPPING_HANDLER_MAPPING_CONTEXT_NAME = RequestMappingHandlerMapping.class.getName();

    /**
     * Any number of these characters are considered delimiters between
     * multiple values in a single init-param String value.
     *
     * @see ContextLoader#INIT_PARAM_DELIMITERS
     */
    private static final String INIT_PARAM_DELIMITERS = ",; \t\n";

    /**
     * RequestContextUtils#findWebApplicationContext(HttpServletRequest, ServletContext) method
     *
     * @since Spring 4.2.1
     */
    private static final Method findWebApplicationContextMethod;


    static {

        findWebApplicationContextMethod = findMethod(RequestContextUtils.class,
                "findWebApplicationContext", HttpServletRequest.class, ServletContext.class);

    }

//    /**
//     * Determine whether the handler findWebApplicationContextMethod is a response body findWebApplicationContextMethod.
//     *
//     * @param handlerMethod {@link HandlerMethod}
//     * @return true if the handler findWebApplicationContextMethod is a response body findWebApplicationContextMethod.
//     */
//    public static boolean isResponseBodyMethod(HandlerMethod handlerMethod) {
//        MethodParameter methodParameter = handlerMethod.getReturnType();
//        return methodParameter.getMethodAnnotation(ResponseBody.class) != null;
//    }

    /**
     * Determine whether the Bean Type is present annotated by {@link ControllerAdvice}
     *
     * @param beanType Bean Type
     * @return If {@link ControllerAdvice} bean type is present , return <code>true</code> , or <code>false</code>.
     */
    public static boolean isControllerAdviceBeanType(Class<?> beanType) {
        return AnnotationUtils.findAnnotation(beanType, ControllerAdvice.class) != null;
    }

//    /**
//     * Determine whether the handler findWebApplicationContextMethod is present annotated by {@link ControllerAdvice}
//     * when org.springframework.web.servlet.mvc.findWebApplicationContextMethod.annotation.ResponseBodyAdvice is present and
//     * can be loaded which indicates the current version of Spring Framework is 4.1 or above.
//     *
//     * @param handlerMethod {@link HandlerMethod}
//     * @return
//     */
//    public static boolean isResponseBodyAdvicePresent(HandlerMethod handlerMethod) {
//        if (ABSTRACT_JSONP_RESPONSE_BODY_ADVICE_PRESENT) {
//            Class<?> controllerType = handlerMethod.getBeanType();
//            return isControllerAdviceBeanType(controllerType);
//        }
//        return false;
//    }

    /**
     * Current {@link HttpServletRequest Request} from {@link ThreadLocal}
     *
     * @return {@link HttpServletRequest Request}
     * @throws IllegalStateException If current web environment could not be in Servlet Container with Spring Web MVC
     * @see FrameworkServlet#initContextHolders(HttpServletRequest, LocaleContext, RequestAttributes)
     * @see RequestContextFilter#initContextHolders(HttpServletRequest, ServletRequestAttributes)
     */
    public static HttpServletRequest currentRequest() throws IllegalStateException {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (!(requestAttributes instanceof ServletRequestAttributes)) {

            throw new IllegalStateException("Current web environment could not be in Servlet Container " +
                    "with Spring Web MVC , because " +
                    DispatcherServlet.class.getName() +
                    " or " +
                    RequestContextFilter.class.getName() +
                    " never be used !");

        }

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;

        return servletRequestAttributes.getRequest();

    }


    /**
     * Get the {@link WebApplicationContext} from {@link HttpServletRequest}
     *
     * @param request        {@link HttpServletRequest}
     * @param servletContext {@link ServletContext}
     * @return {@link WebApplicationContext}
     * @throws IllegalStateException if no servlet-specific context has been found
     * @see RequestContextUtils#getWebApplicationContext(ServletRequest)
     * @see DispatcherServlet#WEB_APPLICATION_CONTEXT_ATTRIBUTE
     */
    public static WebApplicationContext getWebApplicationContext(HttpServletRequest request, ServletContext servletContext) {

        WebApplicationContext webApplicationContext = null;

        if (findWebApplicationContextMethod != null) {

            try {

                webApplicationContext = (WebApplicationContext)
                        invokeMethod(findWebApplicationContextMethod, null, request, servletContext);

            } catch (IllegalStateException e) {

            }

        }

        if (webApplicationContext == null) {

            webApplicationContext = RequestContextUtils.getWebApplicationContext(request, servletContext);

        }

        return webApplicationContext;

    }

    /**
     * Get the {@link WebApplicationContext} from {@link HttpServletRequest}
     *
     * @param request {@link HttpServletRequest}
     * @return {@link WebApplicationContext}
     * @throws IllegalStateException if no servlet-specific context has been found
     * @see RequestContextUtils#getWebApplicationContext(ServletRequest)
     * @see DispatcherServlet#WEB_APPLICATION_CONTEXT_ATTRIBUTE
     */
    public static WebApplicationContext getWebApplicationContext(HttpServletRequest request) {
        return getWebApplicationContext(request, WebUtils.getServletContext(request));
    }


    /**
     * Get Current WebApplicationContext.
     *
     * @return {@link WebApplicationContext} instance.
     */
    public static WebApplicationContext currentWebApplicationContext() {

        return getWebApplicationContext(currentRequest());

    }


    /**
     * {@link RequestMappingHandlerMapping} from {@link HttpServletRequest} {@link ServletContext}
     *
     * @param request        {@link HttpServletRequest}
     * @param servletContext {@link ServletContext}
     * @return {@link RequestMappingHandlerMapping}
     */
    public static RequestMappingHandlerMapping getRequestMappingHandlerMapping(HttpServletRequest request,
                                                                               ServletContext servletContext) {


        WebApplicationContext webApplicationContext = getWebApplicationContext(request, servletContext);

        return getRequestMappingHandlerMapping(webApplicationContext);

    }

    /**
     * {@link RequestMappingHandlerMapping} from {@link HttpServletRequest} {@link ServletContext}
     *
     * @param request {@link HttpServletRequest}
     * @return {@link RequestMappingHandlerMapping}
     */
    public static RequestMappingHandlerMapping getRequestMappingHandlerMapping(HttpServletRequest request) {

        return getRequestMappingHandlerMapping(request, WebUtils.getServletContext(request));

    }

    /**
     * {@link RequestMappingHandlerMapping} from {@link WebApplicationContext}
     *
     * @param webApplicationContext {@link WebApplicationContext}
     * @return {@link RequestMappingHandlerMapping}
     */
    public static RequestMappingHandlerMapping getRequestMappingHandlerMapping(WebApplicationContext webApplicationContext) {

        RequestMappingHandlerMapping requestMappingHandlerMapping =
                BeanUtils.getOptionalBean(webApplicationContext, RequestMappingHandlerMapping.class);

        return requestMappingHandlerMapping;

    }


    /**
     * Get {@link ServletContext} in current {@link HttpServletRequest request}
     *
     * @return current request {@link ServletContext}
     * @throws IllegalStateException If current web environment could not be in Servlet Container with Spring Web MVC
     * @see RequestContextHolder#getRequestAttributes()
     * @see FrameworkServlet#initContextHolders(HttpServletRequest, LocaleContext, RequestAttributes)
     * @see RequestContextFilter#initContextHolders(HttpServletRequest, ServletRequestAttributes)
     * @see HttpServletRequest
     * @see ServletContext
     */
    public static ServletContext currentServletContext() throws IllegalStateException {

        HttpServletRequest request = currentRequest();

        ServletContext servletContext = WebUtils.getServletContext(request);

        return servletContext;

    }

    protected static String appendInitParameter(String existedParameterValue, String... parameterValues) {

        String[] existedParameterValues = StringUtils.hasLength(existedParameterValue) ?
                existedParameterValue.split(INIT_PARAM_DELIMITERS) :
                new String[0];

        List<String> parameterValuesList = new ArrayList<String>();

        if (!ObjectUtils.isEmpty(existedParameterValues)) {
            parameterValuesList.addAll(Arrays.asList(existedParameterValues));
        }

        parameterValuesList.addAll(Arrays.asList(parameterValues));

        String newParameterValue = StringUtils.arrayToDelimitedString(parameterValuesList.toArray(), ",");

        return newParameterValue;
    }

    /**
     * Append {@link ServletContext#setInitParameter(String, String) ServletContext Intialized Parameters}
     *
     * @param servletContext  {@link ServletContext}
     * @param parameterName   the name of init parameter
     * @param parameterValues the values of init parameters
     */
    public static void appendInitParameters(ServletContext servletContext, String parameterName, String... parameterValues) {

        Assert.notNull(servletContext);
        Assert.hasLength(parameterName);
        Assert.notNull(parameterValues);

        String existedParameterValue = servletContext.getInitParameter(parameterName);

        String newParameterValue = appendInitParameter(existedParameterValue, parameterValues);

        if (StringUtils.hasLength(newParameterValue)) {
            servletContext.setInitParameter(parameterName, newParameterValue);
        }

    }

    /**
     * Append  initialized parameter for {@link ApplicationContextInitializer Global Initializer Class}
     *
     * @param servletContext          {@link ServletContext}
     * @param contextInitializerClass the class of {@link ApplicationContextInitializer}
     * @see ContextLoader#GLOBAL_INITIALIZER_CLASSES_PARAM
     */
    public static void appendGlobalInitializerClassInitParameter(ServletContext servletContext,
                                                                 Class<? extends ApplicationContextInitializer> contextInitializerClass) {

        String contextInitializerClassName = contextInitializerClass.getName();

        appendInitParameters(servletContext, GLOBAL_INITIALIZER_CLASSES_PARAM, contextInitializerClassName);

    }

    /**
     * Append  initialized parameter for {@link ApplicationContextInitializer Context Initializer Class}
     *
     * @param servletContext          {@link ServletContext}
     * @param contextInitializerClass the class of {@link ApplicationContextInitializer}
     * @see ContextLoader#CONTEXT_INITIALIZER_CLASSES_PARAM
     */
    public static void appendContextInitializerClassInitParameter(ServletContext servletContext,
                                                                  Class<? extends ApplicationContextInitializer> contextInitializerClass) {

        String contextInitializerClassName = contextInitializerClass.getName();

        appendInitParameters(servletContext, CONTEXT_INITIALIZER_CLASSES_PARAM, contextInitializerClassName);

    }


    /**
     * Append initialized parameter for {@link ApplicationContextInitializer Context Initializer Class} into {@link
     * FrameworkServlet}
     *
     * @param servletContext          {@link ServletContext}
     * @param contextInitializerClass the class of {@link ApplicationContextInitializer}
     * @see FrameworkServlet#applyInitializers(ConfigurableApplicationContext)
     */
    public static void appendFrameworkServletContextInitializerClassInitParameter(
            ServletContext servletContext,
            Class<? extends ApplicationContextInitializer> contextInitializerClass) {

        Collection<? extends ServletRegistration> servletRegistrations =
                WebUtils.findServletRegistrations(servletContext, FrameworkServlet.class).values();

        for (ServletRegistration servletRegistration : servletRegistrations) {
            String contextInitializerClassName = servletRegistration.getInitParameter(CONTEXT_INITIALIZER_CLASSES_PARAM);
            String newContextInitializerClassName = appendInitParameter(contextInitializerClassName, contextInitializerClass.getName());
            servletRegistration.setInitParameter(CONTEXT_INITIALIZER_CLASSES_PARAM, newContextInitializerClassName);
        }

    }

    /**
     * Is page render request
     *
     * @param modelAndView {@link ModelAndView}
     * @return If current request is for page render , return <code>true</code> , or <code>false</code>.
     */
    public static boolean isPageRenderRequest(ModelAndView modelAndView) {

        if (modelAndView != null) {

            String viewName = modelAndView.getViewName();

            return StringUtils.hasText(viewName);

        }

        return false;

    }


}
