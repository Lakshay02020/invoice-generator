package com.cool.services.invoice_generator.controller;

import com.cool.services.invoice_generator.entity.Invoice;
import com.cool.services.invoice_generator.services.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/getInvoice")
    public String getInvoice() {
        return "Successfully Up and running";
    }

    @PostMapping("/createInvoice")
    public ResponseEntity<Resource> createInvoice(@RequestBody Invoice invoice){
        Resource pdfResource = invoiceService.generateInvoice(invoice);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfResource);
    }
}
