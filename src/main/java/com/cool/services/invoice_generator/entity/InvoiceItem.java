package com.cool.services.invoice_generator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public  class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private int quantity;
    private BigDecimal price;
    private String hsnCode;  // HSN Code for GST Classification
    private BigDecimal gstRate;
    private BigDecimal gstAmount; // Calculated GST Amount
    private BigDecimal totalPriceAfterGst;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    // TODO add hsn gst rate table in db
    public void calculateTax() {
        BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(quantity));
        this.gstAmount = itemTotal.multiply(gstRate).divide(BigDecimal.valueOf(100));
        this.totalPriceAfterGst = itemTotal.add(gstAmount);
    }
}