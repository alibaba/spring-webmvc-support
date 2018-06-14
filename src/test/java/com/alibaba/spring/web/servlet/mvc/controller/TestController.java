package com.alibaba.spring.web.servlet.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Test {@link Controller}
 *
 * @author <a href="mailto:taogu.mxx@alibaba-inc.com">taogu.mxx</a> (Office)
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Controller
 * @since 2017.02.02
 */
@Controller
public class TestController {

    @RequestMapping("echo")
    public String echo(Model model) {

        return "echo";
    }

}
