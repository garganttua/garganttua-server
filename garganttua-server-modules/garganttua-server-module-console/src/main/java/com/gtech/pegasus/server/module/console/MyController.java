package com.gtech.pegasus.server.module.console;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author anand
 */
@Controller
public class MyController {
    
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
