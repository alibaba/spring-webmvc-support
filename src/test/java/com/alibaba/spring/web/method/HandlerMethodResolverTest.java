package com.alibaba.spring.web.method;

import com.alibaba.spring.web.TestWebMvcConfigurer;
import com.alibaba.spring.web.servlet.mvc.controller.TestController;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.lang.reflect.Method;
import java.util.Collection;

import static org.springframework.web.context.WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import static org.springframework.web.servlet.DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE;

/**
 * {@link HandlerMethodResolver}
 *
 * @author <a href="mailto:taogu.mxx@alibaba-inc.com">taogu.mxx</a> (Office)
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see HandlerMethodResolver
 * @since 2017.02.02
 */
public class HandlerMethodResolverTest {


    private HandlerMethodResolver handlerMethodResolver;

    private MockServletContext servletContext;

    private MockHttpServletRequest request;

    private AnnotationConfigWebApplicationContext webApplicationContext;

    @Before
    public void init() {

        handlerMethodResolver = new HandlerMethodResolver();

        servletContext = new MockServletContext();

        request = new MockHttpServletRequest(servletContext);

        webApplicationContext = new AnnotationConfigWebApplicationContext();

        webApplicationContext.setServletContext(servletContext);

        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

        servletContext.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

        webApplicationContext.register(TestController.class);
        webApplicationContext.register(DelegatingWebMvcConfiguration.class);
        webApplicationContext.register(TestWebMvcConfigurer.class);

        webApplicationContext.refresh();

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request), true);
    }

    @After
    public void destroy() {

        webApplicationContext.destroy();

        request.removeAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        servletContext.removeAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        RequestContextHolder.resetRequestAttributes();

    }

    @Test
    public void testResolveHandlerMethods() {

        Collection<HandlerMethod> handlerMethods = handlerMethodResolver.resolveHandlerMethods(request, servletContext);

        Assert.assertEquals(1, handlerMethods.size());

        handlerMethods = handlerMethodResolver.resolveHandlerMethods(request);

        Assert.assertEquals(1, handlerMethods.size());


    }

    @Test
    public void testResolveHandlerMethod() {

        HandlerMethod handlerMethod = handlerMethodResolver.resolveHandlerMethod(request, servletContext);

        Assert.assertNull(handlerMethod);


        request.setRequestURI("/echo");

        handlerMethod = handlerMethodResolver.resolveHandlerMethod(request, servletContext);

        Assert.assertNotNull(handlerMethod);

        Method method = handlerMethod.getMethod();

        Assert.assertEquals("echo", method.getName());

        handlerMethod = handlerMethodResolver.resolveHandlerMethod(request);

        Assert.assertNotNull(handlerMethod);

        method = handlerMethod.getMethod();

        Assert.assertEquals("echo", method.getName());

    }


}
