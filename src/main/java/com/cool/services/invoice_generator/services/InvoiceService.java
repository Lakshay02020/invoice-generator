package com.cool.services.invoice_generator.services;

import com.cool.services.invoice_generator.entity.Invoice;

public interface InvoiceService {
    String generateInvoice(Invoice invoice);
}
