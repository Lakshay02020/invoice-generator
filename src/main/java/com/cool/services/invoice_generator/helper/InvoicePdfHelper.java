package com.cool.services.invoice_generator.helper;


import com.cool.services.invoice_generator.entity.Invoice;
import com.cool.services.invoice_generator.entity.InvoiceItem;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
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
        createDocument(document, invoice);
        document.close();
        System.out.println("Invoice PDF Created Successfully!");
    }

    private Cell getLogoCell(Document document) {
        try {
            String LOGO_PATH = "/companyLogo.jpg"; // Ensure it matches the resource path
            InputStream inputStream = getClass().getResourceAsStream(LOGO_PATH);

            if (inputStream == null) {
                throw new FileNotFoundException("Logo file not found at: " + LOGO_PATH);
            }

            byte[] imageBytes = inputStream.readAllBytes();
            ImageData imageData = ImageDataFactory.create(imageBytes);
            Image logo = new Image(imageData).scaleToFit(100, 100);
            return new Cell().add(logo).setBorder(Border.NO_BORDER);

        } catch (Exception e) {
            throw new RuntimeException("Failed to render image: " + e.getMessage(), e);
        }

    }

    private void createDocument(Document document, Invoice invoice){
        addHeader(document, invoice);
        addSellerDetails(document, invoice);
        document.add(new Paragraph("\n").setBold());
        addCustomerDetails(document, invoice);
        addInvoiceData(document, invoice);
        addInvoiceFooter(document);
    }


    private void addInvoiceFooter(Document document) {
        // Create a divider line
        document.add(new Paragraph("\n").setMarginTop(30));
        document.add(new LineSeparator(new SolidLine()));

        // Add footer content
        document.add(new Paragraph("Thank you for your business!")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(10)
                .setMarginTop(5));

        document.add(new Paragraph("For any queries, contact us at support@devil.com")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(9));

        document.add(new Paragraph("This is a system-generated invoice. No signature required.")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(8)
                .setItalic());

        document.add(new LineSeparator(new SolidLine()));
    }

    private void addInvoiceData(Document document, Invoice invoice) {
        document.add(new Paragraph("\nInvoice Items:").setBold());
        invoice.calculateTotal();
        addDocumentTable(invoice, document);
        addTotalData(invoice, document);
    }

    private void addTotalData(Invoice invoice, Document document) {
        // Total Amount Table (Aligned Right)
        Table totalTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
                .useAllAvailableWidth()
                .setMarginTop(10);

        totalTable.addCell(new Cell().add(new Paragraph("Subtotal:")).setBorder(Border.NO_BORDER));
        totalTable.addCell(new Cell().add(new Paragraph("₹" + invoice.getTotalAmount()))
                .setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

        totalTable.addCell(new Cell().add(new Paragraph("Total Tax:")).setBorder(Border.NO_BORDER));
        totalTable.addCell(new Cell().add(new Paragraph("₹" + invoice.getTotalGST()))
                .setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

        totalTable.addCell(new Cell().add(new Paragraph("Total Amount (incl. Tax):").setBold()).setBorder(Border.NO_BORDER));
        totalTable.addCell(new Cell().add(new Paragraph("₹" + invoice.getTotalAmountAfterGst()).setBold())
                .setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

        document.add(totalTable);
    }

    private void addHeaderEx(Document document){
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
            Table addressTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .useAllAvailableWidth();

            // Billing Address
            Cell billingCell = new Cell().add(new Paragraph("Billing Address:"))
                    .add(new Paragraph(invoice.getBuyerName()))
                    .add(new Paragraph(invoice.getBuyerAddress()))
                    .add(new Paragraph("State/UT Code: " + "PUNJAB"))
                    .setBorder(Border.NO_BORDER);

            // Shipping Address
            Cell shippingCell = new Cell().add(new Paragraph("Shipping Address:"))
                    .add(new Paragraph(invoice.getBuyerName()))
                    .add(new Paragraph(invoice.getBuyerAddress()))
                    .add(new Paragraph("State/UT Code: " + "PUNJAB"))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT);

            addressTable.addCell(billingCell);
            addressTable.addCell(shippingCell);

            document.add(addressTable);
    }

    private void addHeader(Document document, Invoice invoice){
        Table header = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                .useAllAvailableWidth();

        Cell logoCell = getLogoCell(document);
        Cell invoiceCell = new Cell().add(new Paragraph("Tax Invoice/Bill of Supply"))
                .add(new Paragraph("Invoice Number: " + invoice.getId()))
                .add(new Paragraph("Order Number: " + "Xyz124"))
                .add(new Paragraph("Invoice Date: " + invoice.getInvoiceDate()))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);

        header.addCell(logoCell);
        header.addCell(invoiceCell);
        document.add(header);

    }
    private void addDocumentTable(Invoice invoice, Document document) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{15, 15, 10, 10, 15, 15, 20}))
                .useAllAvailableWidth();

        table.addHeaderCell("HSN Code");
        table.addHeaderCell("Description");
        table.addHeaderCell("Quantity");
        table.addHeaderCell("Price");
        table.addHeaderCell("Tax (%)");
        table.addHeaderCell("Tax Amount");
        table.addHeaderCell("Total (incl. tax)");

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



        // Add a separator before the total
        document.add(new Paragraph("\n").setBold());
        document.add(new LineSeparator(new SolidLine()));
    }

    private void addSellerDetails(Document document, Invoice invoice) {
        Table sellerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .useAllAvailableWidth();

        // Seller Details
        Cell sellerCell = new Cell()
                .add(new Paragraph("")
                        .add(new Text("Sold By: ").setBold())  // Bold "Sold By"
                        .add(new Text(invoice.getSellerName())))
                .add(new Paragraph(invoice.getSellerAddress()))
                .add(new Paragraph("")
                        .add(new Text("PAN No: ").setBold())  // Bold "PAN No"
                        .add(new Text(invoice.getSellerPanNumber())))
                .add(new Paragraph("")
                        .add(new Text("GST Registration No: ").setBold())  // Bold "GST Registration No"
                        .add(new Text(invoice.getSellerGstNumber())))
                .setBorder(Border.NO_BORDER);

        sellerTable.addCell(sellerCell);
        document.add(sellerTable);
    }


}