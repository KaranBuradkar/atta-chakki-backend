package com.attachakki.components.staff;

import com.attachakki.components.operation.ShopOperationService;
import com.attachakki.components.shop.Shop;
import com.attachakki.components.shop.ShopRepository;
import com.attachakki.entity.User;
import com.attachakki.entity.UserDetails;
import com.attachakki.entity.type.Module;
import com.attachakki.entity.type.StaffRole;
import com.attachakki.exception.businessLogic.OwnerCanNotLeaveTheShopException;
import com.attachakki.exception.businessLogic.ShopStaffAlreadyExistException;
import com.attachakki.exception.entityNotFound.ShopIdNotFoundException;
import com.attachakki.exception.entityNotFound.ShopStaffNotFoundException;
import com.attachakki.exception.entityNotFound.StaffNotFoundException;
import com.attachakki.exception.entityNotFound.UserDetailNotFoundException;
import com.attachakki.repository.ShopStaffRepository;
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
public class ShopStaffServiceImpl implements ShopStaffService {

    private static final Logger log = LoggerFactory.getLogger(ShopStaffServiceImpl.class);
    private final ShopStaffRepository shopStaffRepository;
    private final ShopStaffMapper shopStaffMapper;
    private final ShopRepository shopRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final ShopOperationService shopOperationService;

    public ShopStaffServiceImpl(
            ShopStaffRepository shopStaffRepository,
            ShopStaffMapper shopStaffMapper,
            ShopRepository shopRepository,
            UserDetailsRepository userDetailsRepository,
            ShopOperationService shopOperationService
    ) {
        this.shopStaffRepository = shopStaffRepository;
        this.shopStaffMapper = shopStaffMapper;
        this.shopRepository = shopRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.shopOperationService = shopOperationService;
    }

    @Override
    public Page<StaffResponseDto> findShopStaffs(
            Long shopId, Integer page, Integer size,
            String direction, String sort
    ) {
        Sort.Direction dir = ("asc".equalsIgnoreCase(direction)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Page<ShopStaff> shopStaffPage = shopStaffRepository
                .findByShopIdAndActiveTrue(shopId, PageRequest.of(page, size, dir, sort));
        return shopStaffPage.map(shopStaffMapper::toResponseDto);
    }

    @Override
    public StaffResponseDto findShopStaff(Long shopId, Long shopStaffId) {
        ShopStaff staff = fetchStaffByIdAndShopId(shopStaffId, shopId);
        return shopStaffMapper.toResponseDto(staff);
    }

    @Override
    @Transactional
    public StaffResponseDto create(Long shopId, StaffRequestDto requestDto) {

        // check staff already exist by username
        isStaffAlreadyExist(shopId, requestDto.getUsername());

        // fetch required entity
        Shop shop = fetchShopByShopId(shopId);
        UserDetails addedByUserDetails = getCurrentUserDetails();
        UserDetails userDetails = fetchUserDetailsByUsername(requestDto.getUsername());

        // entity creation
        ShopStaff entity = shopStaffMapper.toEntity(requestDto);
        entity.setUserDetail(userDetails);
        entity.setShop(shop);
        entity.setAddedBy(addedByUserDetails);
        ShopStaff shopStaff = shopStaffRepository.save(entity);
        StaffResponseDto responseDto = shopStaffMapper.toResponseDto(shopStaff);

        shopOperationService.createModule(shopId, 99L, Module.STAFF, responseDto.id(), responseDto.toString());
        return responseDto;
    }

    @Override
    @Transactional
    public StaffResponseDto update(
            Long shopId, Long shopStaffId,
            StaffRequestDto requestDto
    ) {
        ShopStaff staff = fetchStaffByIdAndShopId(shopStaffId, shopId);

        StaffResponseDto before = shopStaffMapper.toResponseDto(staff);
        StringBuilder fields = new StringBuilder("[");

        if (!staff.getStaffRole().equals(requestDto.getStaffRole())) {
            staff.setStaffRole(requestDto.getStaffRole());
            fields.append("staffRole");
        }

        fields.append("]");
        ShopStaff updated = shopStaffRepository.save(staff);
        StaffResponseDto responseDto = shopStaffMapper.toResponseDto(updated);

        shopOperationService.updateModule(shopId, 99L, Module.STAFF, responseDto.id(),
                fields.toString(), before.toString(), responseDto.toString());
        return responseDto;
    }

    @Override
    @Transactional
    public void deleteById(Long shopId, Long shopStaffId) {
        ShopStaff staff = fetchStaffByIdAndShopId(shopStaffId, shopId);
        if (staff.getStaffRole().equals(StaffRole.OWNER)) {
            log.warn("SHOP_OWNER can not leave the shop");
            throw new OwnerCanNotLeaveTheShopException(shopId, shopStaffId);
        }
        StaffResponseDto before = shopStaffMapper.toResponseDto(staff);
        staff.setActive(false);
        shopStaffRepository.save(staff);
        shopOperationService.deleteModule(shopId, 99L, Module.STAFF, before.id(), before.toString());
    }

    // Util methods
    private UserDetails fetchUserDetailsByUsername(String username) {
        return userDetailsRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    log.debug("Username not found");
                    return new UserDetailNotFoundException(
                            "user details not found",
                            "provided email does not have any account");
                });
    }

    private UserDetails getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = ((User) auth.getPrincipal()); // username stored in token
        return userDetailsRepository.findByUser(user)
                .orElseThrow(() -> new StaffNotFoundException("ShopStaff not found for this shop"));
    }

    private Shop fetchShopByShopId(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> {
                    log.debug("shopId not found");
                    return new ShopIdNotFoundException(shopId);
                });
    }

    private ShopStaff fetchStaffByIdAndShopId(Long shopStaffId, Long shopId) {
        return shopStaffRepository.findByIdAndShopIdAndActiveTrue(shopStaffId, shopId)
                .orElseThrow(() -> {
                    log.debug("ShopStaff not found");
                    return new ShopStaffNotFoundException(shopStaffId);
                });
    }

    private void isStaffAlreadyExist(Long shopId, String username) {
        ShopStaff staff = shopStaffRepository
                .findByShopIdAndUserDetailUserUsernameAndActiveTrue(shopId, username)
                .orElse(null);
        if (staff != null) {
            log.warn("ShopStaff already exist");
            throw new ShopStaffAlreadyExistException(username);
        }
    }
}
