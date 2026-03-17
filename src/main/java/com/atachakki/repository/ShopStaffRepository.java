package com.atachakki.repository;

import com.atachakki.components.permissions.PermissionResponseDto;
import com.atachakki.components.staff.ShopStaff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShopStaffRepository extends JpaRepository<ShopStaff, Long> {

    Optional<ShopStaff> findByShopIdAndUserDetailUserUsernameAndActiveTrue(Long shopId, String username);

    Page<ShopStaff> findByUserDetailIdAndActiveTrue(Long userDetailsId, PageRequest pageRequest);

    Optional<ShopStaff> findByShopIdAndUserDetailUserIdAndActiveTrue(Long shopId, Long userId);

    Page<ShopStaff> findByShopIdAndActiveTrue(Long shopId, PageRequest of);

    Optional<ShopStaff> findByIdAndShopIdAndActiveTrue(Long shopStaffId, Long shopId);
}