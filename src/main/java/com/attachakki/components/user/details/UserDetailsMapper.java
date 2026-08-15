package com.attachakki.components.user.details;

import com.attachakki.entity.UserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserDetailsMapper {

    UserDetails toEntity(UserInfoUpdate requestDto);

    @Mapping(source = "user.username", target = "username")
    UserDetailResponseDto toResponseDto(UserDetails userDetails);
}
