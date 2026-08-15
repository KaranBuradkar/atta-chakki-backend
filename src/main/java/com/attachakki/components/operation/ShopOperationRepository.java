package com.attachakki.components.operation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopOperationRepository extends JpaRepository<ShopOperation, Long> {
    Page<ShopOperation> findByShopId(Long shopId, PageRequest of);

    boolean existsByIdAndShopId(Long operationId, Long shopId);
}