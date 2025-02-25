package com.cool.services.invoice_generator.helper;


import com.cool.services.invoice_generator.entity.Invoice;
import com.cool.services.invoice_generator.entity.InvoiceItem;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

import static com.cool.services.invoice_generator.constant.ApplicationConstant.*;

public class InvoicePdfHelper {
    private static final Logger log = LoggerFactory.getLogger(InvoicePdfHelper.class);

    public void generateInvoicePdf(ByteArrayOutputStream outputStream, Invoice invoice){
        // Ensure it's a PDF file, for future use to save in db
//        String filePath = INVOICE + "_" + invoice.getId() + "_" + PDF_EXTENSION;
        Document document = getDocument(outputStream);
        addCompanyLogo(document);
        createDocument(document, invoice);
        document.close();
        System.out.println("Invoice PDF Created Successfully!");
    }

    private void addCompanyLogo(Document document) {
        try {
            String LOGO_PATH = "/logo.jpeg"; // Ensure it matches the resource path
            InputStream inputStream = getClass().getResourceAsStream(LOGO_PATH);

            if (inputStream == null) {
                throw new FileNotFoundException("Logo file not found at: " + LOGO_PATH);
            }

            byte[] imageBytes = inputStream.readAllBytes();
            ImageData imageData = ImageDataFactory.create(imageBytes);
            Image logo = new Image(imageData).scaleToFit(100, 100);
            document.add(logo);

        } catch (Exception e) {
            throw new RuntimeException("Failed to render image: " + e.getMessage(), e);
        }
    }

    private void createDocument(Document document, Invoice invoice){

        addHeader(document);
        addCustomerDetails(document, invoice);

        document.add(new Paragraph("\nInvoice Items:"));
        invoice.calculateTotal();
        addDocumentTable(invoice, document);

        // Total Amount
        document.add(new Paragraph("\nTotal Amount: $" + invoice.getTotalAmount()).setBold());
        document.add(new Paragraph("\nTotal Amount After Tax: $" + invoice.getTotalAmountAfterGst()).setBold());

    }
    private void addHeader(Document document){
        document.add(new Paragraph(INVOICE_HEADER)
                .setBold()
                .setFontSize(HEADER_FONT_SIZE)
                .setMarginBottom(HEADER_MARGIN_SIZE));
    }

    private Document getDocument(ByteArrayOutputStream outputStream){
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        return new Document(pdf);
    };

    private void addCustomerDetails(Document document, Invoice invoice){
        // Customer Details
        document.add(new Paragraph("Customer: " + invoice.getCustomerName()));
        document.add(new Paragraph("Email: " + invoice.getCustomerEmail()));
        document.add(new Paragraph("Date: " + invoice.getInvoiceDate()));
    }

    private void addDocumentTable(Invoice invoice, Document document) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{20, 15, 10, 10, 15, 15, 15}))
                .useAllAvailableWidth();


        table.addHeaderCell("HSN Code");
        table.addHeaderCell("Description");
        table.addHeaderCell("Quantity");
        table.addHeaderCell("Price");
        table.addHeaderCell("GST Rate (%)");
        table.addHeaderCell("GST Amount");
        table.addHeaderCell("Total (incl. GST)");

        List<InvoiceItem> items = invoice.getItems();
        for (InvoiceItem item : items) {
            table.addCell(item.getHsnCode());
            table.addCell(item.getDescription());
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell(String.valueOf(item.getPrice()));
            table.addCell(String.valueOf(item.getGstRate()));
            table.addCell(String.valueOf(item.getGstAmount()));
            table.addCell(String.valueOf(item.getTotalPriceAfterGst()));
        }

        document.add(table);
    }

}