package com.alibaba.spring.web.handler.annotation;

import com.alibaba.spring.web.handler.WebHandler;

import java.lang.annotation.*;

/**
 * A Mapping annotation for {@link WebHandler} of AE Search business
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.1
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebHandlers {

    /**
     * The concrete types of {@link WebHandler}
     *
     * @return Non Null
     */
    Class<? extends WebHandler>[] value();
}
