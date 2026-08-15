package com.attachakki.components.personalization;

import com.attachakki.exception.entityNotFound.PersonalizationNotFoundException;
import com.attachakki.repository.PersonalizationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PersonalizationServiceImpl implements PersonalizationService{

    private static final Logger log = LoggerFactory.getLogger(PersonalizationServiceImpl.class);
    private final PersonalizationRepository personalizationRepository;
    private final PersonalizationMapper personalizationMapper;

    public PersonalizationServiceImpl(
            PersonalizationRepository personalizationRepository,
            PersonalizationMapper personalizationMapper
    ) {
        this.personalizationRepository = personalizationRepository;
        this.personalizationMapper = personalizationMapper;
    }

    @Override
    public PersonalizationResponseDto findPersonalization(Long userDetailId) {
        Personalization personalization = fetchPersonalizationByUserDetailId(userDetailId);
        return personalizationMapper.toResponseDto(personalization);
    }

    @Override
    public PersonalizationResponseDto update(Long userDetailId, PersonalizationRequestDto requestDto) {
        Personalization personalization = fetchPersonalizationByUserDetailId(userDetailId);
        if (validateType(requestDto.getTheme(), personalization.getTheme())) {
            personalization.setTheme(requestDto.getTheme());
        }
        if (validateType(requestDto.getKeyboard(), personalization.getKeyboard())) {
            personalization.setKeyboard(requestDto.getKeyboard());
        }
        if (validateType(requestDto.getLanguage(), personalization.getLanguage())) {
            personalization.setLanguage(requestDto.getLanguage());
        }
        Personalization updated = personalizationRepository.save(personalization);
        return personalizationMapper.toResponseDto(updated);
    }

    private <T>boolean validateType(T req, T existing) {
        return req != null && req.equals(existing);
    }

    private Personalization fetchPersonalizationByUserDetailId(Long userDetailId) {
        return personalizationRepository
                .findByUserDetailId(userDetailId).orElseThrow(() -> {
                    log.warn("Personalization not found");
                    return new PersonalizationNotFoundException(userDetailId);
                });
    }
}
