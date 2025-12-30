package com.gemsflare.gemsflare.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CategoryDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    public CategoryDTO(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
