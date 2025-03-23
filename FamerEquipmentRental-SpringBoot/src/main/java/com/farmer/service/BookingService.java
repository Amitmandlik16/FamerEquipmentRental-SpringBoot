package com.farmer.service;

import com.farmer.entity.Booking;
import com.farmer.entity.Equipment;
import com.farmer.entity.Farmer;
import com.farmer.repository.BookingRepository;
import com.farmer.repository.EquipmentRepository;
import com.farmer.repository.FarmerRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BookingService {

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private EquipmentRepository equipmentRepository;

	@Autowired
	private FarmerRepository farmerRepository;

	@Autowired
	private PDFService pdfService;

	@Autowired
	private EmailService emailService;

	// ✅ Create Booking
	public Booking createBooking(Long equipmentId, Long borrowerId, String startDate, String endDate,
			String paymentMethod) {
		Equipment equipment = equipmentRepository.findById(equipmentId)
				.orElseThrow(() -> new RuntimeException("Equipment not found"));
		Farmer borrower = farmerRepository.findById(borrowerId)
				.orElseThrow(() -> new RuntimeException("Farmer not found"));

		if (isEquipmentBooked(equipmentId, startDate, endDate)) {
			throw new RuntimeException("Equipment is not available for the requested period.");
		}

		Booking booking = new Booking();
		booking.setEquipment(equipment);
		booking.setBorrower(borrower);
		booking.setStartDate(LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE));
		booking.setEndDate(LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE));
		booking.setPaymentMethod(paymentMethod);
		booking.setStatus("PENDING");
		bookingRepository.save(booking);

		emailService.sendBookingRequestEmail(equipment.getOwner(), borrower, booking);
		return booking;
	}

	// ✅ Approve/Reject Booking
	public String approveOrRejectBooking(Long bookingId, String status) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		booking.setStatus(status);
		bookingRepository.save(booking);

		emailService.sendBookingStatusEmail(booking);
		return "Booking " + status.toLowerCase() + " successfully.";
	}

	// ✅ Mark as Received
	public String markAsReceived(Long bookingId) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));
		booking.setStatus("RECEIVED");
		bookingRepository.save(booking);
		return "Equipment marked as received.";
	}

	// ✅ Mark Payment Completed
	public String markPaymentCompleted(Long bookingId) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));
		booking.setStatus("PAYMENT_COMPLETED");
		bookingRepository.save(booking);
		emailService.sendPaymentReceiptEmail(booking);
		return "Payment marked as completed.";
	}

	// ✅ Check Equipment Availability
	public Map<String, Object> checkAvailability(Long equipmentId) {
		List<Booking> bookings = bookingRepository.findByEquipmentId(equipmentId);

		List<String> bookedDates = new ArrayList<>();
		for (Booking booking : bookings) {
			LocalDate start = booking.getStartDate();
			LocalDate end = booking.getEndDate();
			while (!start.isAfter(end)) {
				bookedDates.add(start.toString());
				start = start.plusDays(1);
			}
		}

		Map<String, Object> availabilityMap = new HashMap<>();
		availabilityMap.put("equipmentId", equipmentId);
		availabilityMap.put("bookedDates", bookedDates);
		return availabilityMap;
	}

	// ✅ Generate PDF Receipt
	public void generateReceipt(Long bookingId, HttpServletResponse response) throws IOException {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));
		pdfService.generateBookingReceipt(booking, response);
	}

	// ✅ Check if equipment is already booked
	private boolean isEquipmentBooked(Long equipmentId, String startDate, String endDate) {
		List<Booking> bookings = bookingRepository.findByEquipmentId(equipmentId);
		LocalDate requestedStart = LocalDate.parse(startDate);
		LocalDate requestedEnd = LocalDate.parse(endDate);

		for (Booking booking : bookings) {
			if (!(requestedEnd.isBefore(booking.getStartDate()) || requestedStart.isAfter(booking.getEndDate()))) {
				return true;
			}
		}
		return false;
	}
}
