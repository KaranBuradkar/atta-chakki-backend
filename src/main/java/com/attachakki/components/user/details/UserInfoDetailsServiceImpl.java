package com.attachakki.components.user.details;

import com.attachakki.entity.UserDetails;
import com.attachakki.exception.databaseException.UserDetailsCanNotDelete;
import com.attachakki.exception.entityNotFound.UserDetailNotFoundException;
import com.attachakki.repository.UserDetailsRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserInfoDetailsServiceImpl implements UserInfoDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserInfoDetailsServiceImpl.class);
    private final UserDetailsRepository userDetailsRepository;
    private final UserDetailsMapper userDetailsMapper;

    public UserInfoDetailsServiceImpl(
            UserDetailsRepository userDetailsRepository,
            UserDetailsMapper userDetailsMapper
    ) {
        this.userDetailsRepository = userDetailsRepository;
        this.userDetailsMapper = userDetailsMapper;
    }

    @Override
    public Page<UserInfoUpdate> findUserInfos(
            Integer page, Integer size,
            String direction, String sort
    ) {
        Sort.Direction dir = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Page<UserDetails> userDetailsPage = userDetailsRepository.findAll(PageRequest.of(page, size, dir, sort));
        return userDetailsPage.map(
                u -> new UserInfoUpdate(u.getName(), u.getPhoneNo(), u.getProfileUrl())
        );
    }

    @Override
    public UserDetailResponseDto findUserInfos() {
        UserDetails userDetails = getCurrentUserDetails();
        return userDetailsMapper.toResponseDto(userDetails);
    }

    @Override
    public UserDetailResponseDto update(UserInfoUpdate updateRequest) {
        UserDetails userDetails = getCurrentUserDetails();

        if (updateRequest.name() != null
                && !updateRequest.name().isBlank()
                && !updateRequest.name().equals(userDetails.getName())
        ) {
            userDetails.setName(updateRequest.name());
        }

        if (updateRequest.phoneNo() != null
                && !updateRequest.phoneNo().isBlank()
                && !updateRequest.phoneNo().equals(userDetails.getPhoneNo())
        ) {
            userDetails.setPhoneNo(updateRequest.phoneNo());
        }

        if (updateRequest.profileUrl() != null
                && !updateRequest.profileUrl().isBlank()
                && !updateRequest.profileUrl().equals(userDetails.getProfileUrl())
        ) {
            userDetails.setProfileUrl(updateRequest.profileUrl());
        }

        UserDetails updated = userDetailsRepository.save(userDetails);
        return userDetailsMapper.toResponseDto(updated);
    }

    @Override
    public void delete() {
        try {
            UserDetails currentUserDetails = getCurrentUserDetails();
            userDetailsRepository.delete(currentUserDetails);
        } catch (Exception e) {
            log.error("UserDetails can not delete");
            throw new UserDetailsCanNotDelete("UserDetails can not be deleted");
        }
    }

    // Utils
    private UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userDetailsRepository.findByUserUsername(authentication.getName())
                .orElseThrow(() -> {
                    log.error("UserDetails not found");
                    return new UserDetailNotFoundException(authentication.getName());
                });
    }
}
