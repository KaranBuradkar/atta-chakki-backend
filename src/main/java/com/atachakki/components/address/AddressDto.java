package com.atachakki.components.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class AddressDto {

    private Long id;

    @NotBlank(message = "landmark is never be null")
    private String landmark;

    @NotBlank(message = "city/town is never be null")
    private String city;

    @NotBlank(message = "district is never be null")
    private String district;

    @NotBlank(message = "state is never be null")
    private String state;

    @NotBlank(message = "country is never be null")
    private String country;

    @NotBlank(message = "postal code is never be null")
    private String postalCode;

    private LocalDateTime updatedAt;

    public AddressDto() {}

    public AddressDto(
            String landmark, String city, String district,
            String state, String country, String postalCode
    ) {
        this.landmark = landmark;
        this.city = city;
        this.district = district;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
