package com.gemsflare.gemsflare.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gemsflare.gemsflare.item.model.ItemDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class OrderDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("userid")
    private UUID userid;

    @JsonProperty("items")
    private List<ItemDTO> items;

    @JsonProperty("sum")
    private BigDecimal sum;

    @JsonProperty("date")
    private Date date;

    @JsonProperty("number")
    private String number;

    @JsonProperty("state")
    private String state;

    @JsonProperty("transaction")
    private String transaction;

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public OrderDTO(UUID id, UUID userid, List<ItemDTO> items, BigDecimal sum, Date date, String number, String state, String transaction) {
        this.id = id;
        this.userid = userid;
        this.items = items;
        this.sum = sum;
        this.date = date;
        this.number = number;
        this.state = state;
        this.transaction = transaction;
    }

    public OrderDTO() {}
}
