package com.packleader.rapid.example.store.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.NonNull;

public class Customer extends UniqueEntity {

    private String firstName;
    private String lastName;
    private Address address;

    @JsonProperty(required = false)
    @ApiModelProperty(notes = "Optional first name", required = false)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty(required = true)
    @ApiModelProperty(notes = "Customer's last name", required = true)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@NonNull String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty(required = true)
    @ApiModelProperty(notes = "Customer's address", required = true)
    public Address getAddress() {
        return address;
    }

    public void setAddress(@NonNull Address address) {
        this.address = address;
    }
}
