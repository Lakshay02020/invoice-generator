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
        PdfPTable sellerTable = new PdfPTable(2);
        sellerTable.setWidthPercentage(100);
        sellerTable.setWidths(new float[]{70, 30}); // 70% for seller details, 30% empty

        // "Sold By" details
        PdfPCell sellerCell = new PdfPCell();
        sellerCell.setBorder(Rectangle.NO_BORDER);

        Phrase sellerInfo = new Phrase();
        sellerInfo.add(new Chunk("Sold By: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        sellerInfo.add(new Chunk(invoice.getSellerName(), FontFactory.getFont(FontFactory.HELVETICA, 12))); // Normal text

        sellerCell.addElement(new Paragraph(sellerInfo));

        sellerCell.addElement(new Paragraph(invoice.getSellerAddress()));

        Phrase panInfo = new Phrase();
        panInfo.add(new Chunk("PAN No: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        panInfo.add(new Chunk(invoice.getSellerPanNumber(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
        sellerCell.addElement(new Paragraph(panInfo));

        Phrase gstInfo = new Phrase();
        gstInfo.add(new Chunk("GST Registration No: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        gstInfo.add(new Chunk(invoice.getSellerGstNumber(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
        sellerCell.addElement(new Paragraph(gstInfo));

        sellerTable.addCell(sellerCell);

        // Add an empty cell for the second column
        PdfPCell emptyCell = new PdfPCell();
        emptyCell.setBorder(Rectangle.NO_BORDER);
        sellerTable.addCell(emptyCell);

        // Add table to document
        document.add(sellerTable);
    }

    private void addHeader(Document document, Invoice invoice) throws DocumentException {
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        // Adjusted such that left and right corners are maintained
        header.setWidths(new float[]{60, 40});

        PdfPCell logoCell = getLogoCell();
        PdfPCell invoiceCell = new PdfPCell();
        invoiceCell.addElement(new Paragraph(new Chunk("Tax Invoice/Bill of Supply",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12))));
        invoiceCell.addElement(new Paragraph("Invoice Number: " + invoice.getId()));
        invoiceCell.addElement(new Paragraph("Order Number: #" + invoice.getOrderNumber()));
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
        billingCell.setPaddingRight(40);

        PdfPCell shippingCell = new PdfPCell();
        shippingCell.addElement(new Paragraph("Shipping Address:"));
        shippingCell.addElement(new Paragraph(invoice.getBuyerName()));
        shippingCell.addElement(new Paragraph(invoice.getBuyerAddress()));
        shippingCell.addElement(new Paragraph("State/UT Code: PUNJAB"));
        shippingCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        shippingCell.setBorder(Rectangle.NO_BORDER);
        shippingCell.setPaddingLeft(85);
        addressTable.addCell(billingCell);
        addressTable.addCell(shippingCell);
        document.add(addressTable);
    }

    private void addInvoiceData(Document document, Invoice invoice) throws DocumentException {
        document.add(new Paragraph("\nInvoice Items:\n\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        invoice.calculateTotal();
        addDocumentTable(invoice, document);
        addTotalData(invoice, document);
    }

    private void addDocumentTable(Invoice invoice, Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{15, 15, 12, 13, 10, 15, 20});

        String[] headers = {"HSN Code", "Description", "Quantity", "Price", "Tax (%)", "Tax Amount", "Total (incl. tax)"};
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header));
            headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);  // Center horizontally
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);   // Center vertically
            headerCell.setPadding(5);
            table.addCell(headerCell);
        }

        List<InvoiceItem> items = invoice.getItems();
        for (InvoiceItem item : items) {
            table.addCell(getCenteredCell(item.getHsnCode()));
            table.addCell(getCenteredCell(item.getDescription()));
            table.addCell(getCenteredCell(String.valueOf(item.getQuantity())));
            table.addCell(getCenteredCell(String.valueOf(item.getPrice())));
            table.addCell(getCenteredCell(String.valueOf(item.getGstRate())));
            table.addCell(getCenteredCell(String.valueOf(item.getGstAmount())));
            table.addCell(getCenteredCell(String.valueOf(item.getTotalPriceAfterGst())));
        }

        document.add(table);
        document.add(new Paragraph("\n"));
        document.add(new LineSeparator());
    }

    // Helper method to create a centered cell
    private PdfPCell getCenteredCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); // Align text to center
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);  // Align text vertically
        cell.setPadding(5);
        return cell;
    }

    private void addTotalData(Invoice invoice, Document document) throws DocumentException {
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(100);
        totalTable.setWidths(new float[]{70, 30});

        totalTable.addCell(getCenteredCell("Subtotal:"));
        totalTable.addCell(getCenteredCell("Rs " + invoice.getTotalAmount()));

        totalTable.addCell(getCenteredCell("Total Tax:"));
        totalTable.addCell(getCenteredCell("Rs " + invoice.getTotalGST()));

        PdfPCell totalCell = getCenteredCell("Total Amount (incl. Tax):");
        totalTable.addCell(totalCell);
        totalTable.addCell(getCenteredCell("Rs " + invoice.getTotalAmountAfterGst()));

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
