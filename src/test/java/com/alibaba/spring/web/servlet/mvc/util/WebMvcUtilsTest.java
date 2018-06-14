package com.alibaba.spring.web.servlet.mvc.util;

import com.alibaba.spring.web.TestWebMvcConfigurer;
import com.alibaba.spring.web.servlet.mvc.controller.TestController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.context.ContextLoader.CONTEXT_INITIALIZER_CLASSES_PARAM;
import static org.springframework.web.context.ContextLoader.GLOBAL_INITIALIZER_CLASSES_PARAM;
import static org.springframework.web.context.WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;

/**
 * {@link WebMvcUtils} Test
 *
 * @author <a href="mailto:taogu.mxx@alibaba-inc.com">taogu.mxx</a> (Office)
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see WebMvcUtils
 * @since 2017.01.13
 */
@ControllerAdvice
public class WebMvcUtilsTest {

    private MockServletContext servletContext;

    private MockHttpServletRequest request;

    @Before
    public void init() {

        servletContext = new MockServletContext();

        request = new MockHttpServletRequest(servletContext);

        RequestContextHolder.resetRequestAttributes();

    }

    @Test
    public void testIsControllerAdviceBeanType() {

        Assert.assertFalse(WebMvcUtils.isControllerAdviceBeanType(WebMvcUtils.class));

        Assert.assertTrue(WebMvcUtils.isControllerAdviceBeanType(WebMvcUtilsTest.class));

    }

    @Test
    public void testCurrentRequest() {

        MockHttpServletRequest request = new MockHttpServletRequest();

        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);

        RequestContextHolder.setRequestAttributes(requestAttributes);

        HttpServletRequest httpServletRequest = WebMvcUtils.currentRequest();

        Assert.assertEquals(request, httpServletRequest);

    }

    @Test(expected = IllegalStateException.class)
    public void testCurrentRequestWithoutRequestAttributes() {

        WebMvcUtils.currentRequest();

    }

    @Test(expected = IllegalStateException.class)
    public void testCurrentRequestInNotServletContainer() {

        RequestContextHolder.setRequestAttributes(new RequestAttributes() {

            @Override
            public Object getAttribute(String name, int scope) {
                return null;
            }

            @Override
            public void setAttribute(String name, Object value, int scope) {

            }

            @Override
            public void removeAttribute(String name, int scope) {

            }

            @Override
            public String[] getAttributeNames(int scope) {
                return new String[0];
            }

            @Override
            public void registerDestructionCallback(String name, Runnable callback, int scope) {

            }

            @Override
            public Object resolveReference(String key) {
                return null;
            }

            @Override
            public String getSessionId() {
                return null;
            }

            @Override
            public Object getSessionMutex() {
                return null;
            }

        });

        WebMvcUtils.currentRequest();


    }

    @Test
    public void testCurrentServletContext() {

        MockHttpServletRequest request = new MockHttpServletRequest();

        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);

        RequestContextHolder.setRequestAttributes(requestAttributes);

        ServletContext servletContext = WebMvcUtils.currentServletContext();

        Assert.assertNotNull(servletContext);

        request = new MockHttpServletRequest(new MockServletContext());

        requestAttributes = new ServletRequestAttributes(request);

        RequestContextHolder.setRequestAttributes(requestAttributes);

        servletContext = WebMvcUtils.currentServletContext();

        Assert.assertNotNull(servletContext);

    }

    @Test
    public void testAppendInitParameter() {

        String parameterValue = WebMvcUtils.appendInitParameter("value1");

        Assert.assertEquals("value1", parameterValue);

        parameterValue = WebMvcUtils.appendInitParameter("value1", "value2");

        Assert.assertEquals("value1,value2", parameterValue);

    }

    @Test
    public void testAppendInitParameters() {

        MockServletContext servletContext = new MockServletContext();

        String parameterName = "name";

        String parameterValue = "value";

        Assert.assertNull(servletContext.getInitParameter(parameterName));

        WebMvcUtils.appendInitParameters(servletContext, parameterName);

        Assert.assertNull(servletContext.getInitParameter(parameterName));

        WebMvcUtils.appendInitParameters(servletContext, parameterName, parameterValue, parameterValue);

        Assert.assertEquals("value,value", servletContext.getInitParameter(parameterName));

    }

    @Test
    public void testAppendGlobalInitializerClassInitParameter() {

        MockServletContext servletContext = new MockServletContext();

        WebMvcUtils.appendGlobalInitializerClassInitParameter(servletContext, ApplicationContextInitializer.class);

        Assert.assertEquals(ApplicationContextInitializer.class.getName(),
                servletContext.getInitParameter(GLOBAL_INITIALIZER_CLASSES_PARAM));

    }

    @Test
    public void testAppendContextInitializerClassInitParameter() {

        MockServletContext servletContext = new MockServletContext();

        WebMvcUtils.appendContextInitializerClassInitParameter(servletContext, ApplicationContextInitializer.class);

        Assert.assertEquals(ApplicationContextInitializer.class.getName(),
                servletContext.getInitParameter(CONTEXT_INITIALIZER_CLASSES_PARAM));

    }

//    @Test
//    public void testAppendFrameworkServletContextInitializerClassInitParameter() {
//
//        MockServletContext servletContext = new MockServletContext();
//
//        WebMvcUtils.appendFrameworkServletContextInitializerClassInitParameter(servletContext, ApplicationContextInitializer.class);
//
//        Assert.assertEquals(ApplicationContextInitializer.class.getName(),
//                servletContext.getInitParameter(GLOBAL_INITIALIZER_CLASSES_PARAM));
//
//    }

    @Test
    public void testIsPageRenderRequest() {

        Assert.assertFalse(WebMvcUtils.isPageRenderRequest(null));

        ModelAndView modelAndView = new ModelAndView();

        Assert.assertFalse(WebMvcUtils.isPageRenderRequest(modelAndView));

        modelAndView.setViewName("hello");

        Assert.assertTrue(WebMvcUtils.isPageRenderRequest(modelAndView));

    }

    @Test(expected = IllegalStateException.class)
    public void testGetWebApplicationContextWithoutServletContext() {

        WebMvcUtils.getWebApplicationContext(request, null);

    }

    @Test(expected = IllegalStateException.class)
    public void testGetWebApplicationContextWithoutWebApplicationContext() {

        WebMvcUtils.getWebApplicationContext(request, servletContext);

    }

    @Test
    public void testGetWebApplicationContext() {

        WebApplicationContext webApplicationContext = new GenericWebApplicationContext();

        request.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

        WebApplicationContext applicationContext = WebMvcUtils.getWebApplicationContext(request, servletContext);

        Assert.assertEquals(webApplicationContext, applicationContext);

        applicationContext = WebMvcUtils.getWebApplicationContext(request);

        Assert.assertEquals(webApplicationContext, applicationContext);

    }

    @Test
    public void testCurrentWebApplicationContext() {

        WebApplicationContext webApplicationContext = new GenericWebApplicationContext();

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        servletContext.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

        WebApplicationContext applicationContext = WebMvcUtils.currentWebApplicationContext();

        Assert.assertEquals(webApplicationContext, applicationContext);

    }

    @Test
    public void testGetRequestMappingHandlerMapping() {

        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();

        servletContext.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

        webApplicationContext.setServletContext(servletContext);

        webApplicationContext.register(TestController.class);
        webApplicationContext.register(DelegatingWebMvcConfiguration.class);
        webApplicationContext.register(TestWebMvcConfigurer.class);

        webApplicationContext.refresh();

        request.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

        RequestMappingHandlerMapping requestMappingHandlerMapping =
                WebMvcUtils.getRequestMappingHandlerMapping(request, servletContext);

        Assert.assertNotNull(requestMappingHandlerMapping);

        Assert.assertFalse(requestMappingHandlerMapping.getHandlerMethods().isEmpty());

        requestMappingHandlerMapping =
                WebMvcUtils.getRequestMappingHandlerMapping(request);

        Assert.assertNotNull(requestMappingHandlerMapping);

        Assert.assertFalse(requestMappingHandlerMapping.getHandlerMethods().isEmpty());

    }

}
