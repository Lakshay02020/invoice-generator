package com.cool.services.invoice_generator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvoiceController {
    @Autowired
//    private InvoiceService invoiceService;

    @GetMapping("/getInvoice")
    public String getInvoice() {
        return "Successfully Up and running";
    }
}
