package com.cool.services.invoice_generator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice{
    @Id // Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String customerName;
    private String customerEmail;
    private String invoiceDate;
    private String orderNumber;

    // Seller Details
    private String sellerName; // Name of the seller
    private String sellerAddress; // Address of the seller
    private String sellerGstNumber; // Seller's GST number
    private String sellerPanNumber; // Seller's PAN number

    // Buyer Details
    private String buyerName; // Customer/buyer name
    private String buyerAddress; // Customer billing/shipping address
    private String buyerGstNumber; // Buyer's GST number (if applicable)

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
