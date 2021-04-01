package com.centit.framework.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zfg
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    @RequestMapping("/page")
    public String demoPage() {
        return "login";
    }
}
