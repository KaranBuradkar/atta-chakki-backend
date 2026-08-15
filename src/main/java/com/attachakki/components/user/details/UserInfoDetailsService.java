package com.attachakki.components.user.details;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public interface UserInfoDetailsService {

    @PreAuthorize(value = "hasRole('ADMIN')")
    Page<UserInfoUpdate> findUserInfos(Integer page, Integer size, String direction, String sort);

    UserDetailResponseDto findUserInfos();

    UserDetailResponseDto update(UserInfoUpdate updateRequest);

    void delete();
}
