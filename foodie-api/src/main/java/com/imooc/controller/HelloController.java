package com.imooc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
public class HelloController {

    final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @ApiIgnore
    @GetMapping("/hello")
    public String hello(String name){

        logger.debug("info: hello~");
        logger.info("info: hello~");

        return "hellow world 1" + name;
    }

}
