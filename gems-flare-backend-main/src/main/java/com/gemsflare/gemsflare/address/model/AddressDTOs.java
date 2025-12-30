package com.gemsflare.gemsflare.address.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDTOs {

    @JsonProperty("deliveryAddress")
    private DeliveryAddressDTO deliveryAddress;

    @JsonProperty("billAddress")
    private BillAddressDTO billAddress;

    public AddressDTOs(DeliveryAddressDTO deliveryAddress, BillAddressDTO billAddress) {
        this.deliveryAddress = deliveryAddress;
        this.billAddress = billAddress;
    }

    public DeliveryAddressDTO getDeliveryAddress() {
        return deliveryAddress;
    }

    public BillAddressDTO getBillAddress() {
        return billAddress;
    }
}