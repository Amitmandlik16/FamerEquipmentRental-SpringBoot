package com.farmer.controller;

import com.farmer.entity.Booking;
import com.farmer.service.BookingService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/booking")
@CrossOrigin(origins = "*")
public class BookingController {

	@Autowired
	private BookingService bookingService;
	
	// ✅ Get Booking Requests by Farmer ID
		@GetMapping("/requests/{farmerId}")
		public ResponseEntity<List<Booking>> getBookingsByFarmerId(@PathVariable Long farmerId) {
			List<Booking> bookings = bookingService.getBookingsByFarmerId(farmerId);
			return ResponseEntity.ok(bookings);
		}

	// ✅ Create Booking Request
	@PostMapping("/request")
	public ResponseEntity<Booking> createBooking(@RequestBody Map<String, Object> request) {
		Long equipmentId = Long.parseLong(request.get("equipmentId").toString());
		Long borrowerId = Long.parseLong(request.get("borrowerId").toString());
		String startDate = request.get("startDate").toString();
		String endDate = request.get("endDate").toString();
		String paymentMethod = request.get("paymentMethod").toString();
		return ResponseEntity
				.ok(bookingService.createBooking(equipmentId, borrowerId, startDate, endDate, paymentMethod));
	}

	// ✅ Approve/Reject Booking
	@PutMapping("/update/{bookingId}/{status}")
	public ResponseEntity<String> approveBooking(@PathVariable Long bookingId, @PathVariable String status) {
		return ResponseEntity.ok(bookingService.approveOrRejectBooking(bookingId, status));
	}

	// ✅ Mark Equipment as Received
	@PutMapping("/mark-received/{bookingId}")
	public ResponseEntity<String> markAsReceived(@PathVariable Long bookingId) {
		return ResponseEntity.ok(bookingService.markAsReceived(bookingId));
	}

	// ✅ Mark Payment as Completed
	@PutMapping("/payment-completed/{bookingId}")
	public ResponseEntity<String> markPaymentCompleted(@PathVariable Long bookingId) {
		return ResponseEntity.ok(bookingService.markPaymentCompleted(bookingId));
	}

	// ✅ Check Equipment Booked Status
	@GetMapping("/calendar/{equipmentId}")
	public ResponseEntity<Map<String, Object>> checkAvailability(@PathVariable Long equipmentId) {
		return ResponseEntity.ok(bookingService.checkAvailability(equipmentId));
	}

	// ✅ Download PDF Receipt
	@GetMapping("/pdf/{bookingId}")
	public void downloadReceipt(@PathVariable Long bookingId, HttpServletResponse response) throws IOException {
		bookingService.generateReceipt(bookingId, response);
	}
}
