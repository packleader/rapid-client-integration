package com.packleader.rapid.example.store.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.NonNull;

public class Product extends UniqueEntity {

    private String name;
    private String description;
    private boolean inStock;

    @JsonProperty(required = true)
    @ApiModelProperty(notes = "The name of the product", required = true)
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @JsonProperty(required = false)
    @ApiModelProperty(notes = "Detailed description of the product", required = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty(required = true)
    @ApiModelProperty(notes = "Whether the product is in stock", required = true)
    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
}
