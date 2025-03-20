package com.farmer.service;

import org.springframework.stereotype.Service;

import com.farmer.dto.Admin;

@Service
public class AdminService {

	// ✅ Login logic
	public String login(Admin admin) {
		System.out.println("username=" + admin.getUsername());
		System.out.println("password=" + admin.getPassword());
		if (admin.getUsername().equals("admin") && admin.getPassword().equals("admin")) {
			return "Successfull";
		} else {
			return "Failed";
		}
	}
}
