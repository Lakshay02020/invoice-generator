package com.cool.services.invoice_generator.helper;


import com.cool.services.invoice_generator.entity.Invoice;
import com.cool.services.invoice_generator.entity.InvoiceItem;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.cool.services.invoice_generator.constant.ApplicationConstant.*;
import static com.cool.services.invoice_generator.constant.FileExtensionConstant.PDF_EXTENSION;

public class InvoicePdfHelper {
    public void generateInvoicePdf(ByteArrayOutputStream outputStream, Invoice invoice) {
        // Ensure it's a PDF file, for future use to save in db
//        String filePath = INVOICE + "_" + invoice.getId() + "_" + PDF_EXTENSION;
        Document document = getDocument(outputStream);
        createDocument(document, invoice);
        document.close();
        System.out.println("Invoice PDF Created Successfully!");
    }

    private void createDocument(Document document, Invoice invoice){

        addHeader(document);
        addCustomerDetails(document, invoice);

        document.add(new Paragraph("\nInvoice Items:"));
        addDocumentTable(invoice, document);

        // Total Amount
        document.add(new Paragraph("\nTotal Amount: $" + invoice.getTotalAmount()).setBold());

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

    private void addDocumentTable(Invoice invoice, Document document){
        Table table = new Table(UnitValue.createPercentArray(new float[]{40, 20, 20, 20})) // Column widths
                .useAllAvailableWidth();

        // Table Header
        table.addHeaderCell("Description");
        table.addHeaderCell("Quantity");
        table.addHeaderCell("Price");
        table.addHeaderCell("Total");

        // Add items to the table
        List<InvoiceItem> items = invoice.getItems();
        for (InvoiceItem item : items) {
            table.addCell(item.getDescription());
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell(String.valueOf(item.getPrice()));
            table.addCell(String.valueOf(
                    item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))  // Correct BigDecimal multiplication
            ));
        }

        document.add(table);
    }
}