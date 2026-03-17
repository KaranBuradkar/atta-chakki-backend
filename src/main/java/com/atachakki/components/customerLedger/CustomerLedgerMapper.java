package com.atachakki.components.customerLedger;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerLedgerMapper {

    CustomerLedger toEntity(CustomerLedgerRequestDto requestDto);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "addedBy.id", target = "addedById")
    @Mapping(source = "addedBy.userDetail.name", target = "addedByName")
    @Mapping(source = "updatedBy.id", target = "updatedById")
    @Mapping(source = "updatedBy.userDetail.name", target = "updatedByName")
    CustomerLedgerResponseDto toResponseDto(CustomerLedger requestDto);
}
