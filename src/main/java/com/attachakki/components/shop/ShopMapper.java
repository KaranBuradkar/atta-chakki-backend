package com.attachakki.components.shop;

import com.attachakki.components.address.AddressMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {AddressMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ShopMapper {

    Shop toEntity(ShopRequestDto shopRequestDto);

    @Mapping(target = "owner", source = "owner.name")
    @Mapping(target = "addressDto", source = "address")
    ShopResponseDto toResponseDto(Shop shop);
}
