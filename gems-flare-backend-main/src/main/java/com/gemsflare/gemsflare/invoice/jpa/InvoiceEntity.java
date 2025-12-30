package com.gemsflare.gemsflare.invoice.jpa;

import com.gemsflare.gemsflare.address.model.BillAddressDTO;
import com.gemsflare.gemsflare.address.model.DeliveryAddressDTO;
import com.gemsflare.gemsflare.item.model.ItemDTO;
import com.gemsflare.gemsflare.utils.BillAddressConverter;
import com.gemsflare.gemsflare.utils.ItemDTOListConverter;
import com.gemsflare.gemsflare.utils.ShippingAddressConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoice", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private LocalDate issuedate;

    @Column(nullable = false)
    private LocalDate orderdate;

    @Column(nullable = false)
    private String ordernumber;

    @Convert(converter = BillAddressConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private BillAddressDTO billaddress;

    @Convert(converter = ShippingAddressConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private DeliveryAddressDTO shippingaddress;

    @Convert(converter = ItemDTOListConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private List<ItemDTO> items;

    @Column(nullable = false)
    private BigDecimal totalamount;

    @Column(nullable = false)
    private BigDecimal totalamountwithouttax;

    @Column(nullable = false)
    private String tax;

    @Column(nullable = false)
    private String payment;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDate getIssuedate() {
        return issuedate;
    }

    public void setIssuedate(LocalDate issuedate) {
        this.issuedate = issuedate;
    }

    public LocalDate getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(LocalDate orderdate) {
        this.orderdate = orderdate;
    }

    public String getOrdernumber() {
        return ordernumber;
    }

    public void setOrdernumber(String ordernumber) {
        this.ordernumber = ordernumber;
    }

    public BillAddressDTO getBilladdress() {
        return billaddress;
    }

    public void setBilladdress(BillAddressDTO billaddress) {
        this.billaddress = billaddress;
    }

    public DeliveryAddressDTO getShippingaddress() {
        return shippingaddress;
    }

    public void setShippingaddress(DeliveryAddressDTO shippingaddress) {
        this.shippingaddress = shippingaddress;
    }

    public List<ItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }

    public BigDecimal getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(BigDecimal totalamount) {
        this.totalamount = totalamount;
    }

    public BigDecimal getTotalamountwithouttax() {
        return totalamountwithouttax;
    }

    public void setTotalamountwithouttax(BigDecimal totalamountwithouttax) {
        this.totalamountwithouttax = totalamountwithouttax;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }
}
