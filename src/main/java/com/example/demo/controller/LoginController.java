package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
	// 登録画面の表示
	@GetMapping("/")
	public String index() {
		return "login";
	}
}
