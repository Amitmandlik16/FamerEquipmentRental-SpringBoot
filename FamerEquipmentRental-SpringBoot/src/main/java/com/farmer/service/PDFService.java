package com.farmer.service;

import com.farmer.entity.Booking;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class PDFService {

	// ✅ Generate Booking Receipt PDF
	public void generateBookingReceipt(Booking booking, HttpServletResponse response) throws IOException {
		response.setContentType("application/pdf");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=booking_receipt_" + booking.getId() + ".pdf";
		response.setHeader(headerKey, headerValue);

		Document document = new Document(PageSize.A4);
		try {
			PdfWriter.getInstance(document, response.getOutputStream());

		document.open();

		Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
		Paragraph title = new Paragraph("Farmer Equipment Rental Receipt", titleFont);
		title.setAlignment(Paragraph.ALIGN_CENTER);

		document.add(title);
		document.add(new Paragraph("\n"));

		Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
		document.add(new Paragraph("Booking ID: " + booking.getId(), contentFont));
		document.add(new Paragraph("Equipment Name: " + booking.getEquipment().getName(), contentFont));
		document.add(new Paragraph("Owner Name: " + booking.getEquipment().getOwner().getFirstName() + " "
				+ booking.getEquipment().getOwner().getLastName(), contentFont));
		document.add(new Paragraph(
				"Borrower Name: " + booking.getBorrower().getFirstName() + " " + booking.getBorrower().getLastName(),
				contentFont));
		document.add(
				new Paragraph("Start Date: " + booking.getStartDate().format(DateTimeFormatter.ISO_DATE), contentFont));
		document.add(
				new Paragraph("End Date: " + booking.getEndDate().format(DateTimeFormatter.ISO_DATE), contentFont));
		document.add(new Paragraph("Payment Method: " + booking.getPaymentMethod(), contentFont));
		document.add(new Paragraph("Total Price: ₹" + calculateTotalPrice(booking), contentFont));
		document.add(new Paragraph("Payment Status: " + booking.getStatus(), contentFont));

		document.close();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ✅ Calculate Total Price
	private double calculateTotalPrice(Booking booking) {
		long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate()) + 1;
		return booking.getEquipment().getPricePerDay() * daysBetween;
	}
}
