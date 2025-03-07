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
//        sendInvoiceViaEmail(invoice, outputStream);
        return new ByteArrayResource(outputStream.toByteArray());
    }

    void sendInvoiceViaEmail(Invoice invoice, ByteArrayOutputStream outputStream){
        MultipartFile multipartFile = convertToFile(outputStream);
        String email = "lakshay02singla@gmail.com";
        String text = "Dear " + invoice.getCustomerName() + "\n\n"
                + "Thank you for your purchase. Please find attached the invoice for your order #1234"  + "\n"
                + "\nInvoice Details:\n"
                + "📝 Order Number #1234"
                + "\n📅 Invoice Date: " + invoice.getInvoiceDate()
                + "\n💰 Total Amount: Rs " + invoice.getTotalAmount()
                + "\n\nFor any queries, feel free to contact us at support@devil.com\n\n"
                + "Best Regards,\n"
                + "Singla Company Limited";
        String subject = "📜 Invoice for Your Order #1234";
        emailFeignProvider.sendMail(email, text, subject, multipartFile);
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
