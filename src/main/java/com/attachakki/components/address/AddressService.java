package com.attachakki.components.address;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public interface AddressService {

    AddressDto getAddress(Long addressId);

    List<AddressDto> getAddressPage(int page, int size, String[] sort, String by);

    AddressDto createAddress(@Valid AddressDto addressDto);

    AddressDto updateAddress(Long addressId, Map<String, Objects> map);

    void deleteAddress(Long addressId);
}
