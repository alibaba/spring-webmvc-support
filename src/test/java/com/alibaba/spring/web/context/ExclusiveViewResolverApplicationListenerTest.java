package com.alibaba.spring.web.context;

import com.alibaba.spring.util.FieldUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.mock.web.MockServletContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import java.util.List;
import java.util.Locale;

/**
 * {@link ExclusiveViewResolverApplicationListener} Test
 *
 * @author <a href="mailto:taogu.mxx@alibaba-inc.com">taogu.mxx</a> (Office)
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ExclusiveViewResolverApplicationListener
 * @since 2017.03.23
 */
public class ExclusiveViewResolverApplicationListenerTest {

    private AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();

    private ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();

    private MockServletContext servletContext = new MockServletContext();

    private BeanNameViewResolver viewResolver = new BeanNameViewResolver();

    @Before
    public void init() {

        applicationContext.setServletContext(servletContext);

        applicationContext.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {

            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

                beanFactory.registerSingleton("resolver", resolver);
                beanFactory.registerSingleton("viewResolver", viewResolver);

            }

        });

    }

    @Component("test-view-resolver")
    private static class TestViewResolver implements ViewResolver {


        @Override
        public View resolveViewName(String viewName, Locale locale) throws Exception {
            return null;
        }

    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void testOnApplicationContextOnNoSuchBeanDefinitionException() {

        // Register ViewResolver Beans
        applicationContext.register(ContentNegotiatingViewResolver.class);

        ApplicationListener<ContextRefreshedEvent> applicationListener =
                new ExclusiveViewResolverApplicationListener("not-existed-view-resolver");

        applicationContext.addApplicationListener(applicationListener);

        applicationContext.refresh();

    }

    @Test
    public void testOnApplicationContext() {

        // Register ViewResolver Beans
        applicationContext.register(TestViewResolver.class);

        ApplicationListener<ContextRefreshedEvent> applicationListener =
                new ExclusiveViewResolverApplicationListener("test-view-resolver");

        applicationContext.addApplicationListener(applicationListener);

        applicationContext.refresh();

        List<ViewResolver> viewResolvers = FieldUtils.getFieldValue(resolver, "viewResolvers");

        Assert.assertEquals(1, viewResolvers.size());

        Assert.assertEquals(TestViewResolver.class, viewResolvers.get(0).getClass());

    }
}
