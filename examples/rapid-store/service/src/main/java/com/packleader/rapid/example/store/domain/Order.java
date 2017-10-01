package com.packleader.rapid.example.store.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.NonNull;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Order extends UniqueEntity {

    public static enum Status {
        NEW,
        PAID,
        SHIPPED,
        COMPLETE
    }

    private UUID customerId;
    private List<UUID> productIds;
    private Date date;
    private Status status;

    @JsonProperty(required = true   )
    @ApiModelProperty(notes = "ID of the Customer who made the order", required = true)
    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(@NonNull UUID customerId) {
        this.customerId = customerId;
    }

    @JsonProperty(required = true   )
    @ApiModelProperty(notes = "List of IDs for the products in this order", required = true)
    public List<UUID> getProductIds() {
        return productIds;
    }

    public void setProductIds(@NonNull List<UUID> productIds) {
        this.productIds = productIds;
    }

    @JsonProperty(required = true   )
    @ApiModelProperty(notes = "Date the order was placed", required = true)
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    @JsonProperty(required = true   )
    @ApiModelProperty(notes = "Status of the order", required = true)
    public Status getStatus() {
        return status;
    }

    public void setStatus(@NonNull Status status) {
        this.status = status;
    }
}
