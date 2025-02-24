package com.cool.services.invoice_generator.services.impl;

import com.cool.services.invoice_generator.entity.Invoice;
import com.cool.services.invoice_generator.helper.InvoicePdfHelper;
import com.cool.services.invoice_generator.services.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {
    @Override
    public Resource generateInvoice(Invoice invoice) {
        // Define the PDF file path
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String filePath = "invoice.pdf";

        // Call the helper class to generate PDF
        InvoicePdfHelper.generateInvoicePdf(outputStream, filePath, invoice);
        return new ByteArrayResource(outputStream.toByteArray());

    }
}
