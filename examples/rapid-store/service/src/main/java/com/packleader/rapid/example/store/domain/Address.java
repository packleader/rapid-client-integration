package com.packleader.rapid.example.store.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.NonNull;

public class Address {

    private String streetAddress;
    private String city;
    private String state;
    private int zipCode;

    @JsonProperty(required = true)
    @ApiModelProperty(notes = "House number and street name", required = true)
    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(@NonNull String streetAddress) {
        this.streetAddress = streetAddress;
    }

    @JsonProperty(required = true)
    @ApiModelProperty(notes = "City", required = true)
    public String getCity() {
        return city;
    }

    public void setCity(@NonNull String city) {
        this.city = city;
    }

    @JsonProperty(required = true)
    @ApiModelProperty(notes = "State", required = true)
    public String getState() {
        return state;
    }

    public void setState(@NonNull String state) {
        this.state = state;
    }

    @JsonProperty(required = true)
    @ApiModelProperty(notes = "Zipcode", required = true)
    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }
}
