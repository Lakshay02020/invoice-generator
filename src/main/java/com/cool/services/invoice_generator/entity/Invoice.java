package com.cool.services.invoice_generator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice{
    @Id // Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    private String customerEmail;
    private String invoiceDate;

    private BigDecimal totalAmount;
    private BigDecimal totalGST; // Total GST Amount (CGST + SGST or IGST)
    private BigDecimal totalAmountAfterGst; // Final amount after tax

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvoiceItem> items;

    public void calculateTotal() {
        this.totalAmount = BigDecimal.ZERO;
        this.totalGST = BigDecimal.ZERO;
        this.totalAmountAfterGst = BigDecimal.ZERO;

        for (InvoiceItem item : items) {
            item.calculateTax();
            this.totalAmount = this.totalAmount.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            this.totalGST = this.totalGST.add(item.getGstAmount());
            this.totalAmountAfterGst = this.totalAmountAfterGst.add(item.getTotalPriceAfterGst());
        }
    }
}
