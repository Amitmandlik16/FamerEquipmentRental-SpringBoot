package com.farmer.controller;

import com.farmer.entity.LaborBooking;
import com.farmer.service.LaborBookingService;
import com.farmer.service.LaborPDFService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/booking/labor")
@CrossOrigin(origins = "*")
public class LaborBookingController {

	@Autowired
	private LaborBookingService bookingService;

	@Autowired
	private LaborPDFService laborPDFService;

	// ✅ Create Labor Booking
	@PostMapping("/request")
	public ResponseEntity<LaborBooking> createLaborBooking(@RequestBody LaborBooking booking) {
		return ResponseEntity.ok(bookingService.createLaborBooking(booking));
	}

	// ✅ Get All Labor Bookings
	@GetMapping("/all")
	public ResponseEntity<List<LaborBooking>> getAllLaborBookings() {
		return ResponseEntity.ok(bookingService.getAllLaborBookings());
	}

	// ✅ Approve or Reject Labor Booking
	@PutMapping("/update/{bookingId}/{status}")
	public ResponseEntity<LaborBooking> updateBookingStatus(@PathVariable Long bookingId, @PathVariable String status) {
		return ResponseEntity.ok(bookingService.updateLaborBookingStatus(bookingId, status));
	}

	// ✅ Mark as Completed
	@PutMapping("/mark-completed/{bookingId}")
	public ResponseEntity<String> markBookingAsCompleted(@PathVariable Long bookingId) {
		bookingService.markLaborBookingAsCompleted(bookingId);
		return ResponseEntity.ok("Labor service marked as completed!");
	}

	// ✅ Get Calendar (Available/Booked Dates)
	@GetMapping("/calendar/{laborId}")
	public ResponseEntity<?> getLaborBookingCalendar(@PathVariable Long laborId) {
		return ResponseEntity.ok(bookingService.getLaborBookingCalendar(laborId));
	}

	@GetMapping("/download-receipt/{bookingId}")
	public void downloadLaborBookingReceipt(@PathVariable Long bookingId, HttpServletResponse response)
			throws IOException {
		LaborBooking booking = bookingService.getLaborBookingById(bookingId);
		laborPDFService.generateLaborBookingReceipt(booking, response);
	}

}
