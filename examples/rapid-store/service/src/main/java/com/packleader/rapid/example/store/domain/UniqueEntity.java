package com.packleader.rapid.example.store.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.UUID;

public class UniqueEntity {

    private UUID id;

    @JsonProperty(required = true   )
    @ApiModelProperty(notes = "Unique ID", required = true)
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
