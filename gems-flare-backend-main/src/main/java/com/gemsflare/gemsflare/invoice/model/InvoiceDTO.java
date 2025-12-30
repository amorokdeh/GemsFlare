package com.gemsflare.gemsflare.invoice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gemsflare.gemsflare.address.model.BillAddressDTO;
import com.gemsflare.gemsflare.address.model.DeliveryAddressDTO;
import com.gemsflare.gemsflare.item.model.ItemDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class InvoiceDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("number")
    private String number;

    @JsonProperty("issuedate")
    private LocalDate issuedate;

    @JsonProperty("orderdate")
    private LocalDate orderdate;

    @JsonProperty("ordernumber")
    private String ordernumber;

    @JsonProperty("billaddress")
    private BillAddressDTO billaddress;

    @JsonProperty("shippingaddress")
    private DeliveryAddressDTO shippingaddress;

    @JsonProperty("items")
    private List<ItemDTO> items;

    @JsonProperty("totalamount")
    private BigDecimal totalamount;

    @JsonProperty("totalamountwithouttax")
    private BigDecimal totalamountwithouttax;

    @JsonProperty("tax")
    private String tax;

    @JsonProperty("payment")
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

    public InvoiceDTO(UUID id, String number, LocalDate issuedate, LocalDate orderdate, String ordernumber, BillAddressDTO billaddress,
                      DeliveryAddressDTO shippingaddress, List<ItemDTO> items, BigDecimal totalamount, BigDecimal totalamountwithouttax, String tax,
                      String payment) {
        this.id = id;
        this.number = number;
        this.issuedate = issuedate;
        this.orderdate = orderdate;
        this.ordernumber = ordernumber;
        this.billaddress = billaddress;
        this.shippingaddress = shippingaddress;
        this.items = items;
        this.totalamount = totalamount;
        this.totalamountwithouttax = totalamountwithouttax;
        this.tax = tax;
        this.payment = payment;
    }

    public InvoiceDTO(){};
}
