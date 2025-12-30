package com.gemsflare.gemsflare.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ItemDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("number")
    private String number;

    @JsonProperty("description")
    private String description;

    @JsonProperty("category")
    private String category;

    @JsonProperty("color_groups")
    private List<String> color_groups;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("img_src")
    private String img_src;

    @JsonProperty("object_src")
    private String object_src;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getColor_groups() {
        return color_groups;
    }

    public void setColor_groups(List<String> color_groups) {
        this.color_groups = color_groups;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getImg_src() {
        return img_src;
    }

    public void setImg_src(String img_src) {
        this.img_src = img_src;
    }

    public String getObject_src() {
        return object_src;
    }

    public void setObject_src(String object_src) {
        this.object_src = object_src;
    }

    public ItemDTO(UUID id, String name, String description, String number, String category, List<String> color_groups, BigDecimal price, Integer amount, String img_src,
                   String object_src) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.number = number;
        this.category = category;
        this.color_groups = color_groups;
        this.price = price;
        this.amount = amount;
        this.img_src = img_src;
        this.object_src = object_src;
    }

    public ItemDTO(){}
}
