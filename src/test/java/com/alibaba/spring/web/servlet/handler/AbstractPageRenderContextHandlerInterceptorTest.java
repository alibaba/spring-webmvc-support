package com.alibaba.spring.web.servlet.handler;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link AbstractPageRenderContextHandlerInterceptor} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see AbstractPageRenderContextHandlerInterceptor
 * @since 2017.02.01
 */
public class AbstractPageRenderContextHandlerInterceptorTest {


    @Test
    public void testPostHandler() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();

        MockHttpServletResponse response = new MockHttpServletResponse();

        Object handler = new Object();

        ModelAndView modelAndView = new ModelAndView();

        AbstractPageRenderContextHandlerInterceptor handlerInterceptor = new AbstractPageRenderContextHandlerInterceptor() {

            @Override
            protected void postHandleOnPageRenderContext(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

                modelAndView.getModel().put("test-key", "test-value");

            }
        };

        handlerInterceptor.preHandle(request, response, handler);

        handlerInterceptor.postHandle(request, response, handler, modelAndView);

        handlerInterceptor.afterCompletion(request, response, handler, null);

        handlerInterceptor.afterConcurrentHandlingStarted(request, response, handler);

        Assert.assertNull(modelAndView.getModel().get("test-key"));

        modelAndView.setViewName("hello");

        handlerInterceptor.postHandle(request, response, handler, modelAndView);

        Assert.assertEquals("test-value", modelAndView.getModel().get("test-key"));

        handlerInterceptor.postHandle(null, null, null, null);

    }

}
