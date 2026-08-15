package com.attachakki.components.customer;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CustomerMapper {

    Customer toEntity(CustomerRequestDto requestDto);

    CustomerResponseDto toResponseDto(Customer customer);

    CustomerResponseShortDto toResponseShortDto(Customer customer);
}
