package com.alibaba.spring.web.method;

import com.alibaba.spring.web.servlet.mvc.controller.TestController;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * {@link HandlerMethodRepository} Test
 *
 * @author <a href="mailto:taogu.mxx@alibaba-inc.com">taogu.mxx</a> (Office)
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

    @Test
    public void testGet() {

        Assert.assertNotNull(handlerMethodRepository);

        Assert.assertEquals(1, handlerMethodRepository.getHandlerMethods().size());

        for (HandlerMethod handlerMethod : handlerMethodRepository.getHandlerMethods()) {
            Assert.assertEquals(handlerMethod, handlerMethodRepository.get(handlerMethod));
        }

    }

}
