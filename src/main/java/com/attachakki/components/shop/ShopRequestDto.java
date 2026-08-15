package com.attachakki.components.shop;

import com.attachakki.components.address.AddressDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ShopRequestDto {

    @NotBlank(message = "shop must have a name")
    private String name;
    @NotBlank(message = "shop must have a email")
    private String email;

    @NotBlank(message = "shop must have public contact number")
    @Pattern(
            regexp = "^(?:(?:\\+91[\\s-]?)|(?:0)|(?:91))?(?:[6-9]\\d{9}|[1-9]\\d{2,4}[\\s-]?\\d{6,8})$",
            message = "Invalid Indian phone number format"
    )
    private String phoneNo;

    private String locationUrl;

    @NotNull(message = "Shop must have address")
    private @Valid AddressDto addressDto;

    public ShopRequestDto() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public AddressDto getAddressDto() {
        return addressDto;
    }

    public void setAddressDto(AddressDto addressDto) {
        this.addressDto = addressDto;
    }

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }
}
