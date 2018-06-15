package com.alibaba.spring.web.util;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

/**
 * {@link HandlerMethodUtils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see HandlerMethodUtils
 * @since 2017.01.12
 */
public class HandlerMethodUtilsTest {


    @Test
    public void testFindAnnotation() throws Exception {

        HandlerMethod handlerMethod = new HandlerMethod(new RuntimeAnnotationHandler(), "handle", String.class);

        RuntimeAnnotation runtimeAnnotation = HandlerMethodUtils.findAnnotation(handlerMethod.getReturnType(),
                RuntimeAnnotation.class);

        Assert.assertEquals("method", runtimeAnnotation.value());

        runtimeAnnotation = HandlerMethodUtils.findAnnotation(handlerMethod.getMethodParameters()[0],
                RuntimeAnnotation.class);

        Assert.assertEquals("parameter", runtimeAnnotation.value());

        handlerMethod = new HandlerMethod(new ClassAnnotationHandler(), "handle", String.class);

        runtimeAnnotation = HandlerMethodUtils.findAnnotation(handlerMethod.getReturnType(),
                RuntimeAnnotation.class);

        Assert.assertNull(runtimeAnnotation);

        runtimeAnnotation = HandlerMethodUtils.findAnnotation(handlerMethod.getMethodParameters()[0],
                RuntimeAnnotation.class);

        Assert.assertNull(runtimeAnnotation);

        ClassAnnotation classAnnotation = HandlerMethodUtils.findAnnotation(handlerMethod.getReturnType(),
                ClassAnnotation.class);

        Assert.assertNull(classAnnotation);
    }


    @Test
    public void testFindAnnotations() throws Exception {

        HandlerMethod handlerMethod = new HandlerMethod(new RuntimeAnnotationHandler(), "handle",
                String.class, String.class);

        Map<ElementType, List<RuntimeAnnotation>> annotationsMap =
                HandlerMethodUtils.findAnnotations(handlerMethod, RuntimeAnnotation.class);

        Assert.assertEquals(3, annotationsMap.size());

        List<RuntimeAnnotation> annotationsList = annotationsMap.get(ElementType.TYPE);

        Assert.assertEquals(1, annotationsList.size());

        RuntimeAnnotation runtimeAnnotation = annotationsList.get(0);

        Assert.assertEquals("type", runtimeAnnotation.value());

        annotationsList = annotationsMap.get(ElementType.METHOD);

        Assert.assertEquals(1, annotationsList.size());

        runtimeAnnotation = annotationsList.get(0);

        Assert.assertEquals("method", runtimeAnnotation.value());

        annotationsList = annotationsMap.get(ElementType.PARAMETER);

        Assert.assertEquals(2, annotationsList.size());

        runtimeAnnotation = annotationsList.get(0);

        Assert.assertEquals("parameter1", runtimeAnnotation.value());

        runtimeAnnotation = annotationsList.get(1);

        Assert.assertEquals("parameter2", runtimeAnnotation.value());


        annotationsList = annotationsMap.get(ElementType.PACKAGE);

        Assert.assertNull(annotationsList);


        handlerMethod = new HandlerMethod(new ClassAnnotationHandler(), "handle", String.class);

        annotationsMap =
                HandlerMethodUtils.findAnnotations(handlerMethod, RuntimeAnnotation.class);

        Assert.assertTrue(annotationsMap.isEmpty());

        Map<ElementType, List<ClassAnnotation>> classAnnotationsMap = HandlerMethodUtils.findAnnotations(handlerMethod,
                ClassAnnotation.class);

        Assert.assertTrue(classAnnotationsMap.isEmpty());

    }

    @RuntimeAnnotation("type")
    private static class RuntimeAnnotationHandler {

        @RuntimeAnnotation("method")
        public String handle(@RuntimeAnnotation("parameter") String message) {
            return message;
        }

        @RuntimeAnnotation("method")
        public String handle(@RuntimeAnnotation("parameter1") String message,
                             @RuntimeAnnotation("parameter2") String message2) {
            return message + message2;
        }

    }

    @ClassAnnotation
    private static class ClassAnnotationHandler {

        @ClassAnnotation
        public String handle(@ClassAnnotation String message) {
            return message;
        }

    }


    @Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface RuntimeAnnotation {

        String value();

    }

    @Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD})
    @Retention(RetentionPolicy.CLASS)
    private static @interface ClassAnnotation {

    }
}
