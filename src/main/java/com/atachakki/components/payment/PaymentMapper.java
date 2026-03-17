package com.atachakki.components.payment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PaymentMapper {

    Payment toEntity(PaymentRequestDto requestDto);

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "receiverStaffId", source = "receiver.userDetail.id")
    @Mapping(target = "receiverName", source = "receiver.userDetail.name")
    PaymentResponseDto toResponseDto(Payment payment);
}
