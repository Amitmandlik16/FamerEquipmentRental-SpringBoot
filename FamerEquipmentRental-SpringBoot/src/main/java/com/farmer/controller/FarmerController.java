package com.farmer.controller;

import com.farmer.dto.ForgotPasswordRequestDTO;
import com.farmer.entity.Complaint;
import com.farmer.entity.Farmer;
import com.farmer.entity.Feedback;
import com.farmer.service.ComplaintService;
import com.farmer.service.EmailService;
import com.farmer.service.FeedbackService;
import com.farmer.service.FarmerService;

import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/farmer")
public class FarmerController {

	@Autowired
	private FarmerService farmerService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private ComplaintService complaintService;

	@Autowired
	private FeedbackService feedbackService;

	// ✅ Submit Complaint API
	@PostMapping("/complaint")
	public ResponseEntity<Complaint> submitComplaint(@RequestBody Complaint complaint) {
		Complaint savedComplaint = complaintService.submitComplaint(complaint);
		return ResponseEntity.ok(savedComplaint);
	}

	// ✅ Get All Complaints API
	@GetMapping("/complaint/all")
	public ResponseEntity<List<Complaint>> getAllComplaints() {
		List<Complaint> complaintList = complaintService.getAllComplaints();
		return ResponseEntity.ok(complaintList);
	}

	// ✅ Register Feedback API
	@PostMapping("/feedback")
	public Feedback registerFeedback(@RequestBody Feedback feedback) {
		return feedbackService.saveFeedback(feedback);
	}

	// ✅ Get All Feedback API
	@GetMapping("/feedback/all")
	public ResponseEntity<List<Feedback>> getAllFeedback() {
		List<Feedback> feedbackList = feedbackService.getAllFeedbacks();
		return ResponseEntity.ok(feedbackList);
	}

	// ✅ Login API
	@PostMapping("/login")
	public ResponseEntity<Farmer> login(@RequestBody Map<String, String> request) {
		return ResponseEntity.ok(farmerService.login(request.get("username"), request.get("password")));
	}

	// ✅ Register Farmer API
	@PostMapping("/register")
	public ResponseEntity<Farmer> registerFarmer(@RequestBody Farmer farmer) throws IOException {
		return ResponseEntity.ok(farmerService.createFarmer(farmer));
	}

	// ✅ Get Farmer by ID
	@GetMapping("/{id}")
	public ResponseEntity<Farmer> getFarmerById(@PathVariable long id) {
		return ResponseEntity.ok(farmerService.getFarmerById(id));
	}

	// ✅ Update Farmer
	@PutMapping("/update/{id}")
	public ResponseEntity<Farmer> updateFarmer(@PathVariable long id, @RequestBody Farmer farmerDetails) {
		return ResponseEntity.ok(farmerService.updateFarmer(id, farmerDetails));
	}

	// ✅ Update Password
	@PutMapping("/update-password")
	public ResponseEntity<String> updatePassword(@RequestBody Map<String, String> request) {
		return ResponseEntity.ok(farmerService.updatePassword(request.get("username"), request.get("oldPassword"),
				request.get("newPassword")));
	}

	// ✅ Delete Farmer
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, Boolean>> deleteFarmer(@PathVariable Long id) {
		return ResponseEntity.ok(farmerService.deleteFarmer(id));
	}

	// ✅ Forgot Password API
	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
		return ResponseEntity.ok(farmerService.forgotPassword(request));
	}

	// ✅ Send Invitation Email
	@PostMapping("/invitemail")
	public ResponseEntity<String> sendInvitation(@RequestBody Map<String, Object> request) throws MessagingException {
		String invitedName = (String) request.get("invitedName");
		String invitedEmail = (String) request.get("invitedEmail");
		long id = Long.parseLong(request.get("id").toString());

		emailService.sendHtmlInvitationEmail(invitedName, invitedEmail, id);
		return ResponseEntity.ok("Invitation email sent to " + invitedEmail);
	}

}
