package com.alibaba.spring.web.method;

import com.alibaba.spring.web.servlet.mvc.util.WebMvcUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import static com.alibaba.spring.web.servlet.mvc.util.WebMvcUtils.getRequestMappingHandlerMapping;
import static com.alibaba.spring.web.util.WebUtils.getServletContext;

/**
 * {@link HandlerMethod} Resolver
 *
 * @author <a href="mailto:taogu.mxx@alibaba-inc.com">taogu.mxx</a> (Office)
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see HandlerMethod
 * @since 2017.02.02
 */
public class HandlerMethodResolver {

    /**
     * {@link HandlerMethod}s {@link ServletContext} attribute name
     */
    private static final String HANDLER_METHOD_ATTRIBUTE_NAME = HandlerMethod.class.getName();

    /**
     * Resolve {@link HandlerMethod}s from {@link ServletContext}
     *
     * @param request        {@link HttpServletRequest}
     * @param servletContext {@link ServletContext}
     * @return non-null {@link Collections#unmodifiableCollection(Collection) unmodifiable collection}
     * @throws IllegalStateException if the root WebApplicationContext could not be found
     */
    public Collection<HandlerMethod> resolveHandlerMethods(HttpServletRequest request, ServletContext servletContext) throws IllegalStateException {

        WebApplicationContext webApplicationContext = WebMvcUtils.getWebApplicationContext(request, servletContext);

        Collection<RequestMappingHandlerMapping> requestMappingHandlerMappings =
                webApplicationContext.getBeansOfType(RequestMappingHandlerMapping.class).values();

        Collection<HandlerMethod> handlerMethods = new LinkedList<HandlerMethod>();

        for (RequestMappingHandlerMapping requestMappingHandlerMapping : requestMappingHandlerMappings) {

            handlerMethods.addAll(requestMappingHandlerMapping.getHandlerMethods().values());

        }

        return Collections.unmodifiableCollection(handlerMethods);

    }

    /**
     * Resolve {@link HandlerMethod}s from {@link ServletContext}
     *
     * @param request {@link HttpServletRequest}
     * @return non-null {@link Collections#unmodifiableCollection(Collection) unmodifiable collection}
     * @throws IllegalStateException if the root WebApplicationContext could not be found
     */
    public Collection<HandlerMethod> resolveHandlerMethods(HttpServletRequest request) throws IllegalStateException {

        return resolveHandlerMethods(request, getServletContext(request));

    }

    /**
     * Resolve {@link HandlerMethod} from {@link ServletContext}
     *
     * @param request        {@link HttpServletRequest}
     * @param servletContext {@link ServletContext}
     * @return {@link HandlerMethod}
     * @throws IllegalStateException if the root WebApplicationContext could not be found
     */
    public HandlerMethod resolveHandlerMethod(HttpServletRequest request, ServletContext servletContext) throws IllegalStateException {

        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(HANDLER_METHOD_ATTRIBUTE_NAME);

        if (handlerMethod == null) {

            RequestMappingHandlerMapping handlerMapping = getRequestMappingHandlerMapping(request, servletContext);

            try {

                HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);

                Object handler = handlerExecutionChain == null ? null : handlerExecutionChain.getHandler();

                if (handler instanceof HandlerMethod) {

                    handlerMethod = (HandlerMethod) handler;

                    request.setAttribute(HANDLER_METHOD_ATTRIBUTE_NAME, handlerMethod);

                }


            } catch (Exception e) {

                throw new IllegalStateException(e);

            }

        }

        return handlerMethod;

    }

    /**
     * Resolve {@link HandlerMethod} from {@link ServletContext}
     *
     * @param request {@link HttpServletRequest}
     * @return {@link HandlerMethod}
     * @throws IllegalStateException if the root WebApplicationContext could not be found
     */
    public HandlerMethod resolveHandlerMethod(HttpServletRequest request) throws IllegalStateException {

        return resolveHandlerMethod(request, getServletContext(request));

    }

}
