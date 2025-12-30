package com.gemsflare.gemsflare.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gemsflare.gemsflare.address.model.BillAddressDTO;
import com.gemsflare.gemsflare.address.model.DeliveryAddressDTO;
import lombok.NoArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("name")
    private String name;

    @JsonProperty("lastname")
    private String lastname;

    @JsonProperty("role")
    private String role;

    @JsonProperty("email")
    private String email;

    @JsonProperty("telephone")
    private String telephone;

    @JsonProperty("deliveryAddresses")
    private DeliveryAddressDTO deliveryAddresses;

    @JsonProperty("billAddresses")
    private BillAddressDTO billAddresses;

    public UserDTO(UUID id, String username, String name, String lastname, String role, String email, String telephone, DeliveryAddressDTO deliveryAddresses, BillAddressDTO billAddresses) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.lastname = lastname;
        this.role = role;
        this.email = email;
        this.telephone = telephone;
        this.deliveryAddresses = deliveryAddresses;
        this.billAddresses = billAddresses;
    }

    public UserDTO(UUID id, String username) {
        this.id = id;
        this.username = username;
    }
}

