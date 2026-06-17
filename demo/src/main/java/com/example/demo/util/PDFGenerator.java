package com.example.demo.util;

import com.example.demo.entity.Profile;
import com.example.demo.enums.ProfileType;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PDFGenerator {

    public static byte[] generateIDCardPDF(Profile profile) throws DocumentException, IOException {
        Document document = new Document(new Rectangle(400, 640));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("ID CARD", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        Font typeFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, getTypeColor(profile.getProfileType()));
        Paragraph typeLine = new Paragraph(profile.getProfileType().toString(), typeFont);
        typeLine.setAlignment(Element.ALIGN_CENTER);
        typeLine.setSpacingAfter(15);
        document.add(typeLine);

        if (profile.getPhoto() != null && profile.getPhoto().length > 0) {
            try {
                Image photo = Image.getInstance(profile.getPhoto());
                photo.scaleToFit(120, 140);
                photo.setAlignment(Element.ALIGN_CENTER);
                photo.setSpacingAfter(15);
                document.add(photo);
            } catch (Exception e) {
                // Skip photo if it can't be rendered
            }
        }

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);

        addField(document, "ID:", profile.getUniqueId(), labelFont, valueFont);
        addField(document, "Name:", profile.getFirstName() + " " + profile.getLastName(), labelFont, valueFont);
        addField(document, "Department:", profile.getDepartment() != null ? profile.getDepartment() : "N/A", labelFont, valueFont);
        addField(document, "Email:", profile.getEmail(), labelFont, valueFont);
        addField(document, "Phone:", profile.getPhone() != null ? profile.getPhone() : "N/A", labelFont, valueFont);

        try {
            String qrData = "ID: " + profile.getUniqueId() + "\nName: " + profile.getFirstName() + " " + profile.getLastName();
            byte[] qrBytes = QRCodeGenerator.generateQRCodeImage(qrData);
            Image qrImage = Image.getInstance(qrBytes);
            qrImage.scaleToFit(80, 80);
            qrImage.setAlignment(Element.ALIGN_CENTER);
            qrImage.setSpacingBefore(15);
            document.add(qrImage);
        } catch (Exception e) {
            // Skip QR if generation fails
        }

        document.close();
        writer.close();
        return baos.toByteArray();
    }

    private static void addField(Document document, String label, String value, Font labelFont, Font valueFont) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(90);
        table.setSpacingBefore(5);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setFixedHeight(20);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setFixedHeight(20);

        table.addCell(labelCell);
        table.addCell(valueCell);
        document.add(table);
    }

    private static BaseColor getTypeColor(ProfileType type) {
        return switch (type) {
            case STUDENT -> BaseColor.BLUE;
            case EMPLOYEE -> BaseColor.GREEN;
            case USER -> BaseColor.ORANGE;
        };
    }
}