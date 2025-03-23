package com.farmer.service;

import com.farmer.entity.LaborBooking;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class LaborPDFService {

	// ✅ Generate Labor Booking Receipt PDF
	public void generateLaborBookingReceipt(LaborBooking booking, HttpServletResponse response) throws IOException {
		response.setContentType("application/pdf");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=labor_booking_receipt_" + booking.getId() + ".pdf";
		response.setHeader(headerKey, headerValue);

		Document document = new Document(PageSize.A4);
		try {
			PdfWriter.getInstance(document, response.getOutputStream());

			document.open();

			// ✅ Title
			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
			Paragraph title = new Paragraph("Farmer Labor Booking Receipt", titleFont);
			title.setAlignment(Paragraph.ALIGN_CENTER);

			document.add(title);
			document.add(new Paragraph("\n"));

			// ✅ Content Font
			Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

			// ✅ Booking Details
			document.add(new Paragraph("Booking ID: " + booking.getId(), contentFont));
			document.add(new Paragraph("Labor Name: " + booking.getLabor().getName(), contentFont));
			document.add(new Paragraph("Skills: " + booking.getLabor().getSkills(), contentFont));
			document.add(new Paragraph("Experience: " + booking.getLabor().getExperience() + " years", contentFont));
			document.add(new Paragraph("Location: " + booking.getLabor().getLocation(), contentFont));

			// ✅ Farmer Details
			document.add(new Paragraph("Booked By Farmer ID: " + booking.getFarmerId(), contentFont));

			// ✅ Booking Period
			document.add(new Paragraph("Start Date: " + booking.getStartDate().format(DateTimeFormatter.ISO_DATE),
					contentFont));
			document.add(
					new Paragraph("End Date: " + booking.getEndDate().format(DateTimeFormatter.ISO_DATE), contentFont));

			// ✅ Payment Details
			document.add(new Paragraph("Payment Method: " + booking.getPaymentMethod(), contentFont));
			document.add(new Paragraph("Total Price: ₹" + calculateTotalPrice(booking), contentFont));
			document.add(new Paragraph("Payment Status: " + booking.getStatus(), contentFont));

			document.close();

		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ✅ Calculate Total Price for Labor Booking
	private double calculateTotalPrice(LaborBooking booking) {
		long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate()) + 1;
		return booking.getLabor().getPricePerDay() * daysBetween;
	}
}
