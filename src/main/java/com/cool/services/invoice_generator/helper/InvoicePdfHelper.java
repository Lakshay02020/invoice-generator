package com.cool.services.invoice_generator.helper;

import com.cool.services.invoice_generator.entity.Invoice;
import com.cool.services.invoice_generator.entity.InvoiceItem;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class InvoicePdfHelper {

    public void generateInvoicePdf(ByteArrayOutputStream outputStream, Invoice invoice) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            addHeader(document, invoice);
            addSellerDetails(document, invoice);
            document.add(new Paragraph("\n"));
            addCustomerDetails(document, invoice);
            addInvoiceData(document, invoice);
            addInvoiceFooter(document);

            document.close();
            System.out.println("Invoice PDF Created Successfully!");
        } catch (DocumentException e) {
            throw new RuntimeException("Error creating PDF: " + e.getMessage(), e);
        }
    }

    private void addSellerDetails(Document document, Invoice invoice) throws DocumentException {
        PdfPTable sellerTable = new PdfPTable(2); // 2 columns for 50-50 width
        sellerTable.setWidthPercentage(100); // Table fills the page

        // "Sold By" row
        PdfPCell sellerCell = new PdfPCell();
        sellerCell.setBorder(Rectangle.NO_BORDER);

        Phrase sellerInfo = new Phrase();
        sellerInfo.add(new Chunk("Sold By: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD))); // Bold label
        sellerInfo.add(new Chunk(invoice.getSellerName())); // Seller name
        sellerCell.addElement(new Paragraph(sellerInfo));

        // Seller address
        sellerCell.addElement(new Paragraph(invoice.getSellerAddress()));

        // PAN Number
        Phrase panInfo = new Phrase();
        panInfo.add(new Chunk("PAN No: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD))); // Bold label
        panInfo.add(new Chunk(invoice.getSellerPanNumber())); // PAN number
        sellerCell.addElement(new Paragraph(panInfo));

        // GST Number
        Phrase gstInfo = new Phrase();
        gstInfo.add(new Chunk("GST Registration No: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD))); // Bold label
        gstInfo.add(new Chunk(invoice.getSellerGstNumber())); // GST number
        sellerCell.addElement(new Paragraph(gstInfo));

        sellerTable.addCell(sellerCell);

        // Add table to document
        document.add(sellerTable);
    }

    private void addHeader(Document document, Invoice invoice) throws DocumentException {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{30, 70});

        PdfPCell logoCell = getLogoCell();
        PdfPCell invoiceCell = new PdfPCell();
        invoiceCell.addElement(new Paragraph("Tax Invoice/Bill of Supply"));
        invoiceCell.addElement(new Paragraph("Invoice Number: " + invoice.getId()));
        invoiceCell.addElement(new Paragraph("Order Number: Xyz124"));
        invoiceCell.addElement(new Paragraph("Invoice Date: " + invoice.getInvoiceDate()));
        invoiceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        invoiceCell.setBorder(Rectangle.NO_BORDER);

        header.addCell(logoCell);
        header.addCell(invoiceCell);
        document.add(header);
    }

    private PdfPCell getLogoCell() {
        try {
            String LOGO_PATH = "/companyLogo.jpg";
            InputStream inputStream = getClass().getResourceAsStream(LOGO_PATH);
            if (inputStream == null) {
                throw new FileNotFoundException("Logo file not found at: " + LOGO_PATH);
            }
            Image logo = Image.getInstance(inputStream.readAllBytes());
            logo.scaleToFit(100, 100);
            PdfPCell cell = new PdfPCell(logo);
            cell.setBorder(Rectangle.NO_BORDER);
            return cell;
        } catch (Exception e) {
            throw new RuntimeException("Failed to render image: " + e.getMessage(), e);
        }
    }

    private void addCustomerDetails(Document document, Invoice invoice) throws DocumentException {
        PdfPTable addressTable = new PdfPTable(2);
        addressTable.setWidthPercentage(100);

        PdfPCell billingCell = new PdfPCell();
        billingCell.addElement(new Paragraph("Billing Address:"));
        billingCell.addElement(new Paragraph(invoice.getBuyerName()));
        billingCell.addElement(new Paragraph(invoice.getBuyerAddress()));
        billingCell.addElement(new Paragraph("State/UT Code: PUNJAB"));
        billingCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell shippingCell = new PdfPCell();
        shippingCell.addElement(new Paragraph("Shipping Address:"));
        shippingCell.addElement(new Paragraph(invoice.getBuyerName()));
        shippingCell.addElement(new Paragraph(invoice.getBuyerAddress()));
        shippingCell.addElement(new Paragraph("State/UT Code: PUNJAB"));
        shippingCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        shippingCell.setBorder(Rectangle.NO_BORDER);

        addressTable.addCell(billingCell);
        addressTable.addCell(shippingCell);
        document.add(addressTable);
    }

    private void addInvoiceData(Document document, Invoice invoice) throws DocumentException {
        document.add(new Paragraph("\nInvoice Items:"));
        invoice.calculateTotal();
        addDocumentTable(invoice, document);
        addTotalData(invoice, document);
    }

    private void addDocumentTable(Invoice invoice, Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{15, 15, 10, 10, 15, 15, 20});

        String[] headers = {"HSN Code", "Description", "Quantity", "Price", "Tax (%)", "Tax Amount", "Total (incl. tax)"};
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header));
            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(headerCell);
        }

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
        document.add(new Paragraph("\n"));
        document.add(new LineSeparator());
    }

    private void addTotalData(Invoice invoice, Document document) throws DocumentException {
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(100);
        totalTable.setWidths(new float[]{70, 30});

        totalTable.addCell(new PdfPCell(new Phrase("Subtotal:")));
        totalTable.addCell(new PdfPCell(new Phrase("₹" + invoice.getTotalAmount())));

        totalTable.addCell(new PdfPCell(new Phrase("Total Tax:")));
        totalTable.addCell(new PdfPCell(new Phrase("₹" + invoice.getTotalGST())));

        PdfPCell totalCell = new PdfPCell(new Phrase("Total Amount (incl. Tax):"));
        totalCell.setColspan(1);
        totalTable.addCell(totalCell);
        totalTable.addCell(new PdfPCell(new Phrase("₹" + invoice.getTotalAmountAfterGst())));

        document.add(totalTable);
    }

    private void addInvoiceFooter(Document document) throws DocumentException {
        Paragraph footer1 = new Paragraph("\nThank you for your business!");
        footer1.setAlignment(Element.ALIGN_CENTER);

        Paragraph footer2 = new Paragraph("For any queries, contact us at support@devil.com");
        footer2.setAlignment(Element.ALIGN_CENTER);

        Paragraph footer3 = new Paragraph("This is a system-generated invoice. No signature required.");
        footer3.setAlignment(Element.ALIGN_CENTER);

        document.add(footer1);
        document.add(footer2);
        document.add(footer3);

        // Add a separator line
        document.add(new Chunk(new LineSeparator()));
    }
}
