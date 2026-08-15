package com.attachakki.components.personalization;

import org.springframework.stereotype.Service;

@Service
public interface PersonalizationService {
    PersonalizationResponseDto findPersonalization(Long userDetailId);

    PersonalizationResponseDto update(Long userDetailId, PersonalizationRequestDto requestDto);
}
