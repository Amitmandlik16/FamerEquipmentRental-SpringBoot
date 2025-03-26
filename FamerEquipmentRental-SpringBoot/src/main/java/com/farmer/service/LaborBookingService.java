package com.farmer.service;

import com.farmer.entity.Labor;
import com.farmer.entity.LaborBooking;
import com.farmer.repository.LaborBookingRepository;
import com.farmer.repository.LaborRepository;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class LaborBookingService {

	@Autowired
	private LaborRepository laborRepository;

	@Autowired
	private LaborBookingRepository laborBookingRepository;

	@Autowired
	private LaborEmailService laborEmailService;

	@Autowired
	private LaborPDFService laborPDFService;

	// ✅ Create Labor Booking
	public LaborBooking createLaborBooking(LaborBooking booking) {
		Labor labor = laborRepository.findById(booking.getLabor().getId())
				.orElseThrow(() -> new RuntimeException("Labor not found with ID: " + booking.getLabor().getId()));

		// ✅ Check availability before booking
		if (!isLaborAvailable(labor.getId(), booking.getStartDate(), booking.getEndDate())) {
			throw new RuntimeException("Labor is not available for the requested time period.");
		}

		// ✅ Set default status as PENDING
		booking.setStatus("PENDING");
		LaborBooking savedBooking = laborBookingRepository.save(booking);

		// ✅ Send Booking Request Email
		laborEmailService.sendLaborBookingRequestEmail(labor, savedBooking);

		return savedBooking;
	}

	// ✅ Get All Labor Bookings
	public List<LaborBooking> getAllLaborBookings() {
		return laborBookingRepository.findAll();
	}

	// ✅ Approve or Reject Labor Booking
	public LaborBooking updateLaborBookingStatus(Long bookingId, String status) {
		LaborBooking booking = laborBookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Labor booking not found with ID: " + bookingId));

		booking.setStatus(status.toUpperCase());
		laborBookingRepository.save(booking);

		// ✅ Send Booking Status Email
		laborEmailService.sendLaborBookingStatusEmail(booking);

		return booking;
	}

	// ✅ Mark Labor Booking as Completed
	public String markLaborBookingAsCompleted(Long bookingId) {
		LaborBooking booking = laborBookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Labor booking not found with ID: " + bookingId));

		if (!"APPROVED".equalsIgnoreCase(booking.getStatus())) {
			throw new RuntimeException("Cannot mark booking as completed. Booking is not approved.");
		}

		booking.setStatus("COMPLETED");
		laborBookingRepository.save(booking);

		// ✅ Send Payment Receipt Email
		laborEmailService.sendLaborPaymentReceiptEmail(booking);

		return "Labor booking marked as completed successfully.";
	}

	// ✅ Get Available and Booked Dates
	public Map<String, Object> getLaborBookingCalendar(Long laborId) {
		List<Object[]> bookedDates = laborBookingRepository.findBookingDatesByLaborId(laborId);

		List<String> availableDates = generateAvailableDates(laborId, bookedDates);
		List<Map<String, String>> formattedBookedDates = formatBookedDates(bookedDates);

		Map<String, Object> response = new HashMap<>();
		response.put("availableDates", availableDates);
		response.put("bookedDates", formattedBookedDates);

		return response;
	}

	// ✅ Generate available dates (Next 30 Days)
	private List<String> generateAvailableDates(Long laborId, List<Object[]> bookedDates) {
		List<String> availableDates = new ArrayList<>();
		LocalDate today = LocalDate.now();

		for (int i = 0; i < 30; i++) {
			LocalDate date = today.plusDays(i);
			boolean isBooked = false;
			for (Object[] booking : bookedDates) {
				LocalDate startDate = (LocalDate) booking[0];
				LocalDate endDate = (LocalDate) booking[1];
				if ((date.isEqual(startDate) || date.isAfter(startDate)) && date.isBefore(endDate.plusDays(1))) {
					isBooked = true;
					break;
				}
			}
			if (!isBooked) {
				availableDates.add(date.toString());
			}
		}
		return availableDates;
	}

	// ✅ Format booked dates properly
	private List<Map<String, String>> formatBookedDates(List<Object[]> bookedDates) {
		List<Map<String, String>> formattedBookedDates = new ArrayList<>();
		for (Object[] booking : bookedDates) {
			Map<String, String> dateRange = new HashMap<>();
			dateRange.put("startDate", booking[0].toString());
			dateRange.put("endDate", booking[1].toString());
			formattedBookedDates.add(dateRange);
		}
		return formattedBookedDates;
	}

	// ✅ Check Labor Availability
	private boolean isLaborAvailable(Long laborId, LocalDate startDate, LocalDate endDate) {
		List<LaborBooking> overlappingBookings = laborBookingRepository.findOverlappingBookings(laborId, startDate,
				endDate);
		return overlappingBookings.isEmpty();
	}

	// ✅ Get Labor Booking by ID
	public LaborBooking getLaborBookingById(Long bookingId) {
		return laborBookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Labor booking not found with ID: " + bookingId));
	}

	// ✅ Generate PDF Receipt
	public void generateLaborBookingReceipt(Long bookingId, HttpServletResponse response) throws IOException {
		LaborBooking booking = getLaborBookingById(bookingId);
		laborPDFService.generateLaborBookingReceipt(booking, response);
	}

	// ✅ Get All Labor Bookings by Labor ID
	public List<LaborBooking> getAllBookingsByLaborId(Long laborId) {
		return laborBookingRepository.findByLaborId(laborId);
	}

	// ✅ Calculate Total Price for Booking
	private double calculateTotalPrice(LaborBooking booking) {
		long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate()) + 1;
		return booking.getLabor().getPricePerDay() * daysBetween;
	}
}
