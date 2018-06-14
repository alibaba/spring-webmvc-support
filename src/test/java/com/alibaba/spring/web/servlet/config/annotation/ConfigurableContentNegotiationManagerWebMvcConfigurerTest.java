package com.alibaba.spring.web.servlet.config.annotation;

import com.alibaba.spring.util.FieldUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.*;

/**
 * {@link ConfigurableContentNegotiationManagerWebMvcConfigurer} Test
 *
 * @author <a href="mailto:taogu.mxx@alibaba-inc.com">taogu.mxx</a> (Office)
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConfigurableContentNegotiationManagerWebMvcConfigurer
 * @since 2017.03.23
 */
public class ConfigurableContentNegotiationManagerWebMvcConfigurerTest {


    @Test
    public void testConfigureContentNegotiationOnDefaultValues() {

        WebMvcConfigurer webMvcConfigurer =
                new ConfigurableContentNegotiationManagerWebMvcConfigurer(new HashMap<String, String>());

        ContentNegotiationConfigurer configurer = new ContentNegotiationConfigurer(new MockServletContext());

        webMvcConfigurer.configureContentNegotiation(configurer);

        ContentNegotiationManagerFactoryBean factoryBean =
                FieldUtils.getFieldValue(configurer, "factory", ContentNegotiationManagerFactoryBean.class);

        Assert.assertTrue(FieldUtils.getFieldValue(factoryBean, "favorPathExtension", boolean.class));
        Assert.assertFalse(FieldUtils.getFieldValue(factoryBean, "favorParameter", boolean.class));
        Assert.assertFalse(FieldUtils.getFieldValue(factoryBean, "ignoreAcceptHeader", boolean.class));
        Assert.assertNull(FieldUtils.getFieldValue(factoryBean, "useJaf", Boolean.class));
        Assert.assertEquals("format", FieldUtils.getFieldValue(factoryBean, "parameterName", String.class));
        Assert.assertTrue(FieldUtils.getFieldValue(factoryBean, "mediaTypes", Map.class).isEmpty());
        Assert.assertNull(FieldUtils.getFieldValue(factoryBean, "defaultContentType", MediaType.class));

    }

    @Test
    public void testConfigureContentNegotiation() {

        Map<String, String> properties = new HashMap<String, String>();

        properties.put("favorPathExtension", "false");
        properties.put("favorParameter", "true");
        properties.put("ignoreAcceptHeader", "true");
        properties.put("useJaf", "true");
        properties.put("parameterName", "test-format");
        properties.put("mediaTypes.html", TEXT_HTML_VALUE);
        properties.put("mediaTypes.xml", APPLICATION_XML_VALUE);
        properties.put("mediaTypes.json", APPLICATION_JSON_VALUE);
        properties.put("mediaTypes.gif", IMAGE_GIF_VALUE);
        properties.put("mediaTypes.jpeg", IMAGE_JPEG_VALUE);
        properties.put("defaultContentType", TEXT_HTML_VALUE);


        WebMvcConfigurer webMvcConfigurer = new ConfigurableContentNegotiationManagerWebMvcConfigurer(properties);

        ContentNegotiationConfigurer configurer = new ContentNegotiationConfigurer(new MockServletContext());

        ContentNegotiationManagerFactoryBean factoryBean =
                FieldUtils.getFieldValue(configurer, "factory", ContentNegotiationManagerFactoryBean.class);

        webMvcConfigurer.configureContentNegotiation(configurer);


        Assert.assertFalse(FieldUtils.getFieldValue(factoryBean, "favorPathExtension", boolean.class));
        Assert.assertTrue(FieldUtils.getFieldValue(factoryBean, "favorParameter", boolean.class));
        Assert.assertTrue(FieldUtils.getFieldValue(factoryBean, "ignoreAcceptHeader", boolean.class));
        Assert.assertTrue(FieldUtils.getFieldValue(factoryBean, "useJaf", Boolean.class));
        Assert.assertEquals("test-format", FieldUtils.getFieldValue(factoryBean, "parameterName", String.class));

        Map<String, MediaType> mediaTypesMap = FieldUtils.getFieldValue(factoryBean, "mediaTypes", Map.class);

        Assert.assertEquals("html", mediaTypesMap.get("html").getSubtype());
        Assert.assertEquals("xml", mediaTypesMap.get("xml").getSubtype());
        Assert.assertEquals("json", mediaTypesMap.get("json").getSubtype());
        Assert.assertEquals("gif", mediaTypesMap.get("gif").getSubtype());
        Assert.assertEquals("jpeg", mediaTypesMap.get("jpeg").getSubtype());

    }

}
