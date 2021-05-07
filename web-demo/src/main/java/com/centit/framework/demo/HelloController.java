package com.centit.framework.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(HttpServletRequest request) {
        System.out.println(request.getSession());
        Enumeration<String> headNames = request.getHeaderNames();
        while (headNames.hasMoreElements()) {
            String headName = headNames.nextElement();
            System.out.println(headName + ":" + request.getHeader(headName));
        }
        return "Hello World !";
    }

}
