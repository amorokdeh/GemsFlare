package com.gemsflare.gemsflare.invoice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class InvoiceCounterDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("counter")
    private Integer counter;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public InvoiceCounterDTO(UUID id, LocalDate date, Integer counter) {
        this.id = id;
        this.date = date;
        this.counter = counter;
    }

    public InvoiceCounterDTO(){};

}
