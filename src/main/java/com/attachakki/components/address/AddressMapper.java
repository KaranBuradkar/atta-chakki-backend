package com.attachakki.components.address;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {

    Address toEntity(AddressDto addressDto);
    AddressDto toDto(Address address);
}
