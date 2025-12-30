package com.gemsflare.gemsflare.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class LoginResponseDTO {
    @JsonProperty("token")
    private String token;

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("name")
    private String name;

    @JsonProperty("lastname")
    private String lastname;

    public LoginResponseDTO(String token, UUID id, String username, String name, String lastname) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.name = name;
        this.lastname = lastname;
    }
}
