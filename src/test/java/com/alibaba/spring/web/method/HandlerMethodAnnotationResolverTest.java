package com.alibaba.spring.web.method;

import com.alibaba.spring.web.servlet.mvc.controller.TestController;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * {@link HandlerMethodAnnotationResolver} Test
 *
 * @author <a href="mailto:taogu.mxx@alibaba-inc.com">taogu.mxx</a> (Office)
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see HandlerMethodAnnotationResolver
 * @since 2017.02.02
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HandlerMethodRepository.class, TestController.class, RequestMappingHandlerMapping.class})
public class HandlerMethodAnnotationResolverTest {

    @Autowired
    private HandlerMethodRepository handlerMethodRepository;

    @Test
    public void testResolve() {

        HandlerMethodAnnotationResolver resolver = new HandlerMethodAnnotationResolver(handlerMethodRepository.getHandlerMethods());

        Map<Method, RequestMapping> handlerMethodsMap = resolver.resolve(RequestMapping.class);

        Assert.assertEquals(1, handlerMethodsMap.size());

        for (Map.Entry<Method, RequestMapping> entry : handlerMethodsMap.entrySet()) {
            Method handlerMethod = entry.getKey();
            RequestMapping requestMapping = entry.getValue();
            Assert.assertArrayEquals(new String[]{"echo"}, requestMapping.value());
            Assert.assertEquals("echo", handlerMethod.getName());
        }

        Map<Method, Override> result = resolver.resolve(Override.class);

        Assert.assertTrue(result.isEmpty());

    }

}
