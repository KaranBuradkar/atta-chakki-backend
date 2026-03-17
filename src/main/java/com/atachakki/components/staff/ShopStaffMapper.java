package com.atachakki.components.staff;

import com.atachakki.components.shop.ShopShortResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ShopStaffMapper {

    ShopStaff toEntity(StaffRequestDto requestDto);

    @Mapping(source = "shop.name", target = "shopName")
    @Mapping(source = "userDetail.user.username", target = "username")
    @Mapping(source = "userDetail.name", target = "staffName")
    @Mapping(source = "addedBy.name", target = "addedByName")
    StaffResponseDto toResponseDto(ShopStaff shopStaff);

    @Mapping(target = "id", source = "shop.id")
    @Mapping(target = "name", source = "shop.name")
    @Mapping(target = "owner", source = "shop.owner.name")
    ShopShortResponseDto toShortResponseDto(ShopStaff staff);
}
