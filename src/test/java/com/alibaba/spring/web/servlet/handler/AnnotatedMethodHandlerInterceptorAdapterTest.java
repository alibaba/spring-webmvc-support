package com.alibaba.spring.web.servlet.handler;

import com.alibaba.spring.web.TestWebMvcConfigurer;
import com.alibaba.spring.web.method.HandlerMethodResolver;
import com.alibaba.spring.web.servlet.mvc.controller.TestController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

import static org.springframework.web.context.WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import static org.springframework.web.servlet.DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE;

/**
 * {@link AnnotatedMethodHandlerInterceptorAdapter} Test
 *
 * @author <a href="mailto:taogu.mxx@alibaba-inc.com">taogu.mxx</a> (Office)
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see AnnotatedMethodHandlerInterceptorAdapter
 * @since 2017.02.02
 */
public class AnnotatedMethodHandlerInterceptorAdapterTest {

    private AnnotationConfigWebApplicationContext webApplicationContext;

    private MockServletContext servletContext;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private ModelAndView modelAndView;

    private RequestMappingMethodHandlerInterceptorAdapter requestMappingMethodHandlerInterceptor;

    private Collection<HandlerMethod> handlerMethods;

    @Before
    public void init() {

        HandlerMethodResolver handlerMethodResolver = new HandlerMethodResolver();

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

        requestMappingMethodHandlerInterceptor = new RequestMappingMethodHandlerInterceptorAdapter(webApplicationContext);

        requestMappingMethodHandlerInterceptor.init(request);

        handlerMethods = handlerMethodResolver.resolveHandlerMethods(request, servletContext);

    }


    @Test
    public void testPreHandle() throws Exception {

        for (HandlerMethod handlerMethod : handlerMethods) {

            boolean result = requestMappingMethodHandlerInterceptor.preHandle(request, response, handlerMethod);

            Assert.assertFalse(result);

        }

        boolean result = requestMappingMethodHandlerInterceptor.preHandle(request, response, new Object());

        Assert.assertTrue(result);

    }

    @Test
    public void testPostHandle() throws Exception {

        for (HandlerMethod handlerMethod : handlerMethods) {

            requestMappingMethodHandlerInterceptor.postHandle(request, response, handlerMethod, modelAndView);

        }

        requestMappingMethodHandlerInterceptor.postHandle(request, response, new Object(), modelAndView);

    }

    @Test
    public void testAfterCompletion() throws Exception {

        for (HandlerMethod handlerMethod : handlerMethods) {

            requestMappingMethodHandlerInterceptor.afterCompletion(request, response, handlerMethod, new Exception());

        }

        requestMappingMethodHandlerInterceptor.afterCompletion(request, response, new Object(), new Exception());

    }

    @Test
    public void testAfterConcurrentHandlingStarted() throws Exception {

        for (HandlerMethod handlerMethod : handlerMethods) {

            requestMappingMethodHandlerInterceptor.afterConcurrentHandlingStarted(request, response, handlerMethod);

        }

        requestMappingMethodHandlerInterceptor.afterConcurrentHandlingStarted(request, response, new Object());

    }

    @Test
    public void testGetAnnotationType() {

        Assert.assertEquals(RequestMapping.class, requestMappingMethodHandlerInterceptor.getAnnotationType());

    }

    @Ignore
    protected static class RequestMappingMethodHandlerInterceptorAdapter extends
            AnnotatedMethodHandlerInterceptorAdapter<RequestMapping> {

        public RequestMappingMethodHandlerInterceptorAdapter(WebApplicationContext webApplicationContext) throws IllegalStateException {
            super(webApplicationContext);
        }

        @Override
        protected boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                    HandlerMethod handlerMethod, RequestMapping annotation) throws Exception {

            Assert.assertArrayEquals(new String[]{"echo"}, annotation.value());
            Assert.assertEquals("echo", handlerMethod.getMethod().getName());

            return false;
        }

        protected void postHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod,
                                  RequestMapping annotation, ModelAndView modelAndView) throws Exception {

            Assert.assertArrayEquals(new String[]{"echo"}, annotation.value());
            Assert.assertEquals("echo", handlerMethod.getMethod().getName());

        }

        protected void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                       HandlerMethod handlerMethod, RequestMapping annotation, Exception ex)
                throws Exception {

            Assert.assertArrayEquals(new String[]{"echo"}, annotation.value());
            Assert.assertEquals("echo", handlerMethod.getMethod().getName());

        }

        protected void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response,
                                                      HandlerMethod handlerMethod, RequestMapping annotation) throws Exception {

            Assert.assertArrayEquals(new String[]{"echo"}, annotation.value());
            Assert.assertEquals("echo", handlerMethod.getMethod().getName());

        }

    }
}
