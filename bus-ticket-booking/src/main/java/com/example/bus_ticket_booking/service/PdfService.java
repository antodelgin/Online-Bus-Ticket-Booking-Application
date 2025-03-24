package com.example.bus_ticket_booking.service;

import com.example.bus_ticket_booking.entity.Booking;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generateETicketPdf(Booking booking) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Bus Ticket Booking - E-Ticket")
                    .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\nThank you for choosing our service! Have a safe and comfortable journey.")
                    .setItalic().setFontSize(12).setTextAlignment(TextAlignment.CENTER));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"); // Example: 22 Mar 2025, 17:30
            String formattedBookingTime = booking.getBookingTime().format(formatter);
            String formattedDepartureTime = booking.getBus().getDepartureTime().format(formatter);

            // Passenger Details
            document.add(new Paragraph("\nPassenger Details").setBold().setFontSize(14));
            Table passengerTable = new Table(UnitValue.createPercentArray(new float[]{3, 7})).useAllAvailableWidth();

            passengerTable.addCell(new Cell().add(new Paragraph("Ticket ID:")));
            passengerTable.addCell(new Cell().add(new Paragraph(String.valueOf(booking.getId()))));

            passengerTable.addCell(new Cell().add(new Paragraph("Passenger Name:")));
            passengerTable.addCell(new Cell().add(new Paragraph(booking.getName())));

            passengerTable.addCell(new Cell().add(new Paragraph("Email:")));
            passengerTable.addCell(new Cell().add(new Paragraph(booking.getEmail())));

            passengerTable.addCell(new Cell().add(new Paragraph("Phone:")));
            passengerTable.addCell(new Cell().add(new Paragraph(booking.getPhoneNumber())));

            passengerTable.addCell(new Cell().add(new Paragraph("Age:")));
            passengerTable.addCell(new Cell().add(new Paragraph(String.valueOf(booking.getAge()))));

            passengerTable.addCell(new Cell().add(new Paragraph("Gender:")));
            passengerTable.addCell(new Cell().add(new Paragraph(booking.getGender())));

            passengerTable.addCell(new Cell().add(new Paragraph("Booking Time:")));
            passengerTable.addCell(new Cell().add(new Paragraph(formattedBookingTime)));

            document.add(passengerTable);

            // Bus Details
            document.add(new Paragraph("\nBus Details").setBold().setFontSize(14));
            Table busTable = new Table(UnitValue.createPercentArray(new float[]{3, 7})).useAllAvailableWidth();

            busTable.addCell(new Cell().add(new Paragraph("Bus:")));
            busTable.addCell(new Cell().add(new Paragraph(booking.getBus().getBusName())));

            busTable.addCell(new Cell().add(new Paragraph("Bus Number:")));
            busTable.addCell(new Cell().add(new Paragraph(booking.getBus().getBusNumber())));

            busTable.addCell(new Cell().add(new Paragraph("From:")));
            busTable.addCell(new Cell().add(new Paragraph(booking.getBus().getSource())));

            busTable.addCell(new Cell().add(new Paragraph("To:")));
            busTable.addCell(new Cell().add(new Paragraph(booking.getBus().getDestination())));

            busTable.addCell(new Cell().add(new Paragraph("Departure Time:")));
            busTable.addCell(new Cell().add(new Paragraph(formattedDepartureTime)));

            busTable.addCell(new Cell().add(new Paragraph("Duration (Hours):")));
            busTable.addCell(new Cell().add(new Paragraph(booking.getBus().getDuration().toString())));

            PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            busTable.addCell(new Cell().add(new Paragraph("Price (Rupee):")));
            busTable.addCell(new Cell().add(new Paragraph(booking.getBus().getPrice().toString())));

            document.add(busTable);

            // Footer message
            document.add(new Paragraph("\nThis is an electronically generated ticket. No signature required.")
                    .setItalic().setFontSize(10).setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("For queries, contact our customer support at support@busticket.com")
                    .setFontSize(10).setTextAlignment(TextAlignment.CENTER));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

}
