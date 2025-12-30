package com.gemsflare.gemsflare.permission.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gemsflare.gemsflare.user.model.UserDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PermissionDTO {

    @JsonProperty("route")
    private String route;

    @JsonProperty("admins")
    private List<UserDTO> admins;

    @JsonProperty("users")
    private List<UserDTO> users;

    public PermissionDTO(String route, List<UserDTO> admins, List<UserDTO> users) {
        this.route = route;
        this.admins = admins;
        this.users = users;
    }

}

