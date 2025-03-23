package com.farmer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin(origins = "*")
public class WelcomeController {

	// ✅ Show Welcome Page
	@GetMapping("/")
	public String showWelcomePage() {
		return "welcome";
	}
}
