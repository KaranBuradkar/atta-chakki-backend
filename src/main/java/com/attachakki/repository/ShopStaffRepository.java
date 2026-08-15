package com.attachakki.repository;

import com.attachakki.components.staff.ShopStaff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopStaffRepository extends JpaRepository<ShopStaff, Long> {

    Optional<ShopStaff> findByShopIdAndUserDetailUserUsernameAndActiveTrue(Long shopId, String username);

    Page<ShopStaff> findByUserDetailIdAndActiveTrue(Long userDetailsId, PageRequest pageRequest);

    Optional<ShopStaff> findByShopIdAndUserDetailUserIdAndActiveTrue(Long shopId, Long userId);

    Page<ShopStaff> findByShopIdAndActiveTrue(Long shopId, PageRequest of);

    Optional<ShopStaff> findByIdAndShopIdAndActiveTrue(Long shopStaffId, Long shopId);
}