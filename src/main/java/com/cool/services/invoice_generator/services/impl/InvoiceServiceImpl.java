package com.cool.services.invoice_generator.services.impl;

import com.cool.services.invoice_generator.entity.Invoice;
import com.cool.services.invoice_generator.services.InvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {
    @Override
    public String generateInvoice(Invoice invoice) {
        log.info("Inside Generate Invoice");
        return "";
    }
}
