package com.gemsflare.gemsflare.address.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillAddressDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("lastname")
    private String lastname;

    @JsonProperty("street")
    private String street;

    @JsonProperty("housenumber")
    private String housenumber;

    @JsonProperty("zipcode")
    private String zipcode;

    @JsonProperty("county")
    private String county;

    @JsonProperty("country")
    private String country;

    public BillAddressDTO(String name, String lastname, String street, String housenumber, String zipcode, String county,
                              String country) {
        this.name = name;
        this.lastname = lastname;
        this.street = street;
        this.housenumber = housenumber;
        this.zipcode = zipcode;
        this.county = county;
        this.country = country;
    }

    public BillAddressDTO(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHousenumber() {
        return housenumber;
    }

    public void setHousenumber(String housenumber) {
        this.housenumber = housenumber;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}


