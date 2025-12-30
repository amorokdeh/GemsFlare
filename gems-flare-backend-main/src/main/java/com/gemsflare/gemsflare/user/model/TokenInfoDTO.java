package com.gemsflare.gemsflare.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenInfoDTO {

    @Setter
    @Getter
    @JsonProperty("token")
    private String token;

    @JsonProperty("isValid")
    private boolean isValid;

    @Setter
    @Getter
    @JsonProperty("expirationDate")
    private String expirationDate;

    public TokenInfoDTO(String token, boolean isValid, String expirationDate) {
        this.token = token;
        this.isValid = isValid;
        this.expirationDate = expirationDate;
    }


    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

}
