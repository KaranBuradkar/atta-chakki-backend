package com.atachakki.components.permissions;

import com.atachakki.components.staff.ShopStaff;
import com.atachakki.exception.businessLogic.BusinessLogicException;
import com.atachakki.exception.businessLogic.DuplicatePermissionInsertionException;
import com.atachakki.exception.entityNotFound.ShopStaffNotFoundException;
import com.atachakki.exception.entityNotFound.StaffPermissionNotFoundException;
import com.atachakki.repository.ShopStaffRepository;
import com.atachakki.repository.StaffPermissionRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StaffPermissionServiceImpl implements StaffPermissionService{

    private static final Logger log = LoggerFactory.getLogger(StaffPermissionServiceImpl.class);
    private final StaffPermissionRepository staffPermissionRepository;
    private final ShopStaffRepository shopStaffRepository;

    public StaffPermissionServiceImpl(
            StaffPermissionRepository staffPermissionRepository,
            ShopStaffRepository shopStaffRepository
    ) {
        this.staffPermissionRepository = staffPermissionRepository;
        this.shopStaffRepository = shopStaffRepository;
    }

    @Override
    public Page<StaffPermissionResponseDto> findStaffPermissions(
            Long shopId, Long staffId, Integer page,
            Integer size, String direction, String[] sort
    ) {
        Sort.Direction dir = ("asc".equalsIgnoreCase(direction)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Page<StaffPermission> permissionPage = staffPermissionRepository
                .findByShopStaffIdAndShopStaffShopId(staffId, shopId, PageRequest.of(page, size, dir, sort));
        return permissionPage.map(this::toResponseDto);
    }

    @Override
    public Page<PermissionResponseDto> findPermissions(Long shopId, Long staffId, Integer page,
                                                       Integer size, String direction, String[] sort) {
        Sort.Direction dir = ("asc".equalsIgnoreCase(direction)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, size, dir, sort);
//        return staffPermissionRepository.findPermissionsByStaffId(staffId, pageRequest);
        return null;
    }

    @Override
    @Transactional
    public List<StaffPermissionResponseDto> create(
            Long shopId, Long staffId,
            List<StaffPermissionRequestDto> requestDto
    ) {
        ShopStaff staff = fetchStaffByStaffIdAndShopId(staffId, shopId);
        List<StaffPermission> permissions = staffPermissionRepository
                .findByShopStaffIdAndShopStaffShopId(staffId, shopId);
        List<String> list = new ArrayList<>();
        requestDto.forEach(r -> {
                    if (isNotExist(r, permissions)) {
                        permissions.add(new StaffPermission(staff, r.getModule(), r.getLevel()));
                    } else {
                        list.add(String.format("Permission module=%s, level=%s", r.getModule(), r.getLevel()));
                    }
                }
        );
        if (!list.isEmpty()) {
            throw new DuplicatePermissionInsertionException("Duplicate permission insertion", list);
        }
        List<StaffPermission> staffPermissions = staffPermissionRepository.saveAll(permissions);
        return staffPermissions.stream().map(this::toResponseDto).toList();
    }

    @Override
    @Transactional
    public void delete(Long shopId, List<Long> permissionIds) {
        List<StaffPermission> permissions = staffPermissionRepository
                .findByIdInAndShopStaffShopId(permissionIds, shopId);

        if (permissionIds.size() != permissions.size()) {
            log.debug("User try to delete non-existing permissions");
            throw new BusinessLogicException("Staff permission ids mismatched",
                    "user try to delete non-existing permissions");
        }
        staffPermissionRepository.deleteAllById(permissionIds);
    }

    // Utils
    private StaffPermissionResponseDto toResponseDto(StaffPermission p) {
        return new StaffPermissionResponseDto(p.getId(), p.getModule(), p.getLevel());
    }

    private boolean isNotExist(StaffPermissionRequestDto r, List<StaffPermission> permissions) {
        return permissions
                .stream().noneMatch(
                        p -> (p.getModule().equals(r.getModule())
                                && p.getLevel().equals(r.getLevel())
                        )
                );
    }

    private ShopStaff fetchStaffByStaffIdAndShopId(Long staffId, Long shopId) {
        return shopStaffRepository.findByIdAndShopIdAndActiveTrue(staffId, shopId)
                .orElseThrow(() -> {
                    log.debug("shop staff not found");
                    return new ShopStaffNotFoundException(staffId);
                });
    }
}
