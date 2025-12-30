package com.gemsflare.gemsflare.checkout.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gemsflare.gemsflare.item.model.ItemDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class CheckoutDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("userid")
    private UUID userid;

    @JsonProperty("items")
    private List<ItemDTO> items;

    @JsonProperty("sum")
    private BigDecimal sum;

    @JsonProperty("paid")
    private boolean paid;

    @JsonProperty("date")
    private Date date;

    @JsonProperty("number")
    private String number;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserid() {
        return userid;
    }

    public void setUserid(UUID userid) {
        this.userid = userid;
    }

    public List<ItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public CheckoutDTO(UUID id, UUID userid, List<ItemDTO> items, BigDecimal sum, boolean paid, Date date, String number) {
        this.id = id;
        this.userid = userid;
        this.items = items;
        this.sum = sum;
        this.paid = paid;
        this.date = date;
        this.number = number;
    }

    public CheckoutDTO() {}
}
