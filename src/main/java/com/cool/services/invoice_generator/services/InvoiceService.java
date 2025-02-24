package com.cool.services.invoice_generator.services;

import com.cool.services.invoice_generator.entity.Invoice;
import org.springframework.core.io.Resource;

public interface InvoiceService {
    Resource generateInvoice(Invoice invoice);
}
