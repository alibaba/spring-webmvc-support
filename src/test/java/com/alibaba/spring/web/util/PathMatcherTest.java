package com.alibaba.spring.web.util;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * {@link PathMatcher}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.1
 */
public class PathMatcherTest {

    @Test
    public void testMatch() {

        PathMatcher pathMatcher = new AntPathMatcher();

        Assert.assertTrue(pathMatcher.match("/abc////{var}", "/abc/def"));
        Assert.assertTrue(pathMatcher.match("/abc/{var}", "/abc/////def"));

    }
}
