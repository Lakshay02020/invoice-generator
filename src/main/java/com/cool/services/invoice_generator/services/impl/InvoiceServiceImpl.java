package com.cool.services.invoice_generator.services.impl;

import com.cool.services.invoice_generator.entity.Invoice;
import com.cool.services.invoice_generator.feingClient.EmailFeignProvider;
import com.cool.services.invoice_generator.helper.CustomMultipartFile;
import com.cool.services.invoice_generator.helper.InvoicePdfHelper;
import com.cool.services.invoice_generator.services.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

@Service
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {
    @Autowired
    EmailFeignProvider emailFeignProvider;

    @Override
    public Resource generateInvoice(Invoice invoice) {
        // Define the PDF file path
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Call the helper class to generate PDF
        InvoicePdfHelper invoicePdfHelper = new InvoicePdfHelper();
        invoicePdfHelper.generateInvoicePdf(outputStream, invoice);

        return new ByteArrayResource(outputStream.toByteArray());
    }

    void sendInvoiceViaEmail(ByteArrayOutputStream outputStream){
        MultipartFile multipartFile = convertToFile(outputStream);
        emailFeignProvider.sendMail("lakshay02singla@gmail.com", "Invoice for the order is attached", "Invoice order: aA242f", multipartFile);
    }

    MultipartFile convertToFile(ByteArrayOutputStream outputStream){
        return new CustomMultipartFile(
                outputStream.toByteArray(),
                "invoice",
                "invoice.pdf",
                "application/pdf"
        );
    }
}
