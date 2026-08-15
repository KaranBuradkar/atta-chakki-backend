package com.attachakki.components.personalization;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PersonalizationMapper {

    PersonalizationResponseDto toResponseDto(Personalization personalization);
}