package com.alibaba.spring.web.util;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

/**
 * {@link WebUtils} Test
 *
 * @author <a href="mailto:taogu.mxx@alibaba-inc.com">taogu.mxx</a> (Office)
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see WebUtils
 * @since 2017.01.13
 */
public class WebUtilsTest {


    @Test
    public void testIsRunningBelowServlet3Container() {

        MockServletContext servletContext = new MockServletContext();

        servletContext.setMajorVersion(3);

        Assert.assertFalse(WebUtils.isRunningBelowServlet3Container());

        servletContext.setMajorVersion(2);

        Assert.assertFalse(WebUtils.isRunningBelowServlet3Container());

    }

    @Test
    public void testGetServletContext() {

        MockServletContext servletContext = new MockServletContext();

        MockHttpServletRequest request = new MockHttpServletRequest(servletContext);

        Assert.assertEquals(servletContext, WebUtils.getServletContext(request));

    }


//    @Test
//    public void testFindFilterRegistrations() {
//
//        MockServletContext servletContext = new MockServletContext();
//
//        Map<String, ? extends FilterRegistration> filterRegistrations =
//                findFilterRegistrations(servletContext, Filter.class);
//
//        Assert.assertTrue(filterRegistrations.isEmpty());
//
//    }

}
