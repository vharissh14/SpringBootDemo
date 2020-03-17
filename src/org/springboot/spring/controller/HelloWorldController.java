package org.springboot.spring.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

	@RequestMapping(value="/")
	public String hello() {
		return "Hello world - Welcome to Java!!";
	}
	
	@RequestMapping(value="/hello")
	public String hello1() {
		return "<title>Helo</title>";
	}
}
