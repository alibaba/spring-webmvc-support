package com.alibaba.spring.web.method;

import com.alibaba.spring.web.servlet.mvc.controller.TestController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import static org.springframework.web.context.WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;

/**
 * {@link HandlerMethodRepository} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see HandlerMethodRepository
 * @since 2017.02.02
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        HandlerMethodRepository.class,
        TestController.class,
        RequestMappingHandlerMapping.class})
public class HandlerMethodRepositoryTest {

    @Autowired
    private HandlerMethodRepository handlerMethodRepository;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private DefaultListableBeanFactory beanFactory;

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void init() {

        ServletContext servletContext = new MockServletContext();

        WebApplicationContext webApplicationContext = new GenericWebApplicationContext(beanFactory, servletContext) {

            public boolean equals(Object object) {
                return applicationContext.equals(object);
            }

            public int hashCode() {
                return applicationContext.hashCode();
            }

        };

        HttpServletRequest request = new MockHttpServletRequest(servletContext);

        servletContext.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    }

    @Test
    public void testGet() {

        Assert.assertNotNull(handlerMethodRepository);

        Assert.assertEquals(1, handlerMethodRepository.getHandlerMethods().size());

        for (HandlerMethod handlerMethod : handlerMethodRepository.getHandlerMethods()) {
            Assert.assertEquals(handlerMethod, handlerMethodRepository.get(handlerMethod));
        }

    }

    @Test
    public void testGetHandlerMethods() {

        Collection<HandlerMethod> handlerMethods = handlerMethodRepository.getHandlerMethods(applicationContext);
        Assert.assertEquals(1, handlerMethods.size());

    }

}
