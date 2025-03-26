package com.farmer.service;

import com.farmer.entity.Booking;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class PDFService {
	
    private static final String LOGO_TOP_PATH = "src/main/resources/static/farmlogo2.jpeg";
    private static final String LOGO_BG_PATH = "https://t3.ftcdn.net/jpg/06/55/98/66/360_F_655986634_Ru3c5b8fedJfcwS4dGEI5y3bTokyWJEp.jpg";

    public void generateBookingReceipt(Booking booking, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=booking_receipt_" + booking.getId() + ".pdf");

        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Add Top Logo
            Image topLogo = Image.getInstance(LOGO_TOP_PATH);
            topLogo.scaleToFit(150, 150); // Increased size
            topLogo.setAlignment(Element.ALIGN_CENTER);
            document.add(topLogo);

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(0, 102, 204));
            Paragraph title = new Paragraph("Farmer Equipment Rental Receipt", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Professional Details Block
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(15f);
            table.setSpacingAfter(15f);

            addTableHeader(table, "Booking ID", booking.getId().toString());
            addTableHeader(table, "Equipment Name", booking.getEquipment().getName());
            addTableHeader(table, "Category", booking.getEquipment().getCategory());
            addTableHeader(table, "Condition", booking.getEquipment().getEquipmentCondition());
            addTableHeader(table, "Owner Name", booking.getEquipment().getOwner().getFirstName() + " " + booking.getEquipment().getOwner().getLastName());
            addTableHeader(table, "Owner Contact", booking.getEquipment().getOwner().getMobileNumber());
            addTableHeader(table, "Borrower Name", booking.getBorrower().getFirstName() + " " + booking.getBorrower().getLastName());
            addTableHeader(table, "Borrower Contact", booking.getBorrower().getMobileNumber());
            addTableHeader(table, "Start Date", booking.getStartDate().format(DateTimeFormatter.ISO_DATE));
            addTableHeader(table, "End Date", booking.getEndDate().format(DateTimeFormatter.ISO_DATE));
            addTableHeader(table, "Payment Method", booking.getPaymentMethod());
            addTableHeader(table, "Total Price", "₹" + calculateTotalPrice(booking));
            addTableHeader(table, "Rent/Payment Status", booking.getStatus());

            document.add(table);

            // Footer
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(0, 0, 0)); // Darker font
            Paragraph footer = new Paragraph("Thank you for using Farmer Equipment Rental Service", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.add(new Paragraph("\n"));
            Paragraph contact = new Paragraph("Contact us: farmerrentalservice@gmail.com", footerFont);
            contact.setAlignment(Element.ALIGN_CENTER);
            document.add(contact);

            // Add Background Watermark Logo below Footer with Darker Transparency
            PdfContentByte canvas = writer.getDirectContentUnder();
            Image bgLogo = Image.getInstance(LOGO_BG_PATH);
            bgLogo.setAbsolutePosition(100, 50); // Moved lower on the page
            bgLogo.scaleToFit(400, 400);
            bgLogo.setTransparency(new int[]{150, 150}); // Darker transparency
            canvas.addImage(bgLogo);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTableHeader(PdfPTable table, String header, String value) {
        PdfPCell cellHeader = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK)));
        cellHeader.setBackgroundColor(new Color(230, 230, 230));
        cellHeader.setPadding(6);

        PdfPCell cellValue = new PdfPCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK)));
        cellValue.setPadding(6);

        table.addCell(cellHeader);
        table.addCell(cellValue);
    }

    private double calculateTotalPrice(Booking booking) {
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate()) + 1;
        return booking.getEquipment().getPricePerDay() * daysBetween;
    }
}
