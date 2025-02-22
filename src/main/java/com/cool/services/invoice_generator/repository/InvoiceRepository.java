package com.cool.services.invoice_generator.repository;

import com.cool.services.invoice_generator.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
