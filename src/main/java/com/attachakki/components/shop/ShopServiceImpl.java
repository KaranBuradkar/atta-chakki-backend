package com.attachakki.components.shop;

import com.attachakki.components.address.Address;
import com.attachakki.components.address.AddressDto;
import com.attachakki.components.address.AddressRepository;
import com.attachakki.components.staff.ShopStaff;
import com.attachakki.components.staff.ShopStaffMapper;
import com.attachakki.components.permissions.StaffPermission;
import com.attachakki.entity.User;
import com.attachakki.entity.UserDetails;
import com.attachakki.entity.type.Module;
import com.attachakki.entity.type.PermissionLevel;
import com.attachakki.entity.type.StaffRole;
import com.attachakki.exception.entityNotFound.ShopIdNotFoundException;
import com.attachakki.exception.entityNotFound.UserDetailNotFoundException;
import com.attachakki.exception.entityNotFound.UserDetailsNotFoundException;
import com.attachakki.repository.ShopStaffRepository;
import com.attachakki.repository.StaffPermissionRepository;
import com.attachakki.repository.UserDetailsRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class ShopServiceImpl implements ShopService {

    private static final Logger log = LoggerFactory.getLogger(ShopServiceImpl.class);
    private final ShopRepository shopRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final AddressRepository addressRepository;
    private final ShopMapper shopMapper;
    private final ShopStaffRepository shopStaffRepository;
    private final StaffPermissionRepository staffPermissionRepository;
    private final ShopStaffMapper shopStaffMapper;

    public ShopServiceImpl(
            ShopMapper shopMapper,
            ShopRepository shopRepository,
            AddressRepository addressRepository,
            ShopStaffRepository shopStaffRepository,
            UserDetailsRepository userDetailsRepository,
            StaffPermissionRepository staffPermissionRepository,
            ShopStaffMapper shopStaffMapper
    ) {
        this.shopRepository = shopRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.addressRepository = addressRepository;
        this.shopMapper = shopMapper;
        this.shopStaffRepository = shopStaffRepository;
        this.staffPermissionRepository = staffPermissionRepository;
        this.shopStaffMapper = shopStaffMapper;
    }

    @Override
    public Page<ShopShortResponseDto> getAllShops(int page, int size, String direction, String sort) {
        Sort.Direction dir = ("asc".equalsIgnoreCase(direction)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Page<ShopStaff> shops = shopStaffRepository
                .findByUserDetailIdAndActiveTrue(currentUserDetailId(), PageRequest.of(page, size, dir, sort));
        return shops.map(shopStaffMapper::toShortResponseDto);
    }

    private Long currentUserDetailId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        UserDetails userDetails = userDetailsRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.warn("UserDetails not found");
                    return new UserDetailNotFoundException("UserDetails not found");
                });
        return userDetails.getId();
    }

    @Override
    @Transactional
    public ShopResponseDto create(ShopRequestDto requestDto) {

        // 1. Get UserDetails
        UserDetails userDetails = resolveUserDetails();
        if (userDetails == null) {
            log.error("no user detail found");
            throw new UserDetailsNotFoundException("UserDetails not found");
        }

        // 2. Resolve address
        Address address = resolveShopAddress(requestDto.getAddressDto());

        // 3. resolve shop
        Shop shop = new Shop(
                requestDto.getName(),
                requestDto.getPhoneNo(),
                requestDto.getEmail(),
                requestDto.getLocationUrl(),
                address,
                userDetails,
                ShopStatus.ACTIVE
        );
        Shop savedShop = shopRepository.save(shop);

        // 4. resolve shop staff
        ShopStaff owner = new ShopStaff(
                shop, userDetails, StaffRole.OWNER
        );

        owner.setAddedBy(userDetails);
        ShopStaff saveShopStaff = shopStaffRepository.save(owner);
        savedShop.addShopStaff(saveShopStaff);

        // 5. resolve staff permissions
        List<StaffPermission> savedStaffPermissions = staffPermissionRepository
                .saveAll(getShopkeeperPermissions(saveShopStaff));
        savedStaffPermissions.forEach(saveShopStaff::addPermission);

        // 6. return dto
        return shopMapper.toResponseDto(savedShop);
    }

    @Override
    public ShopResponseDto updateShopFields(Long shopId, ShopRequestDto requestDto) {
        Shop shop = fetchShopByShopId(shopId);
        if (validateString(requestDto.getName(), shop.getName())) {
            shop.setName(requestDto.getName());
        }
        if (validateString(requestDto.getEmail(), shop.getEmail())) {
            shop.setEmail(requestDto.getEmail());
        }
        if (validateString(requestDto.getPhoneNo(), shop.getPhoneNo())) {
            shop.setPhoneNo(requestDto.getPhoneNo());
        }
        if (validateString(requestDto.getLocationUrl(), shop.getLocationUrl())) {
            shop.setLocationUrl(requestDto.getLocationUrl());
        }
        if (requestDto.getAddressDto() != null) {
            shop.setAddress(updateAddress(requestDto.getAddressDto(), shop.getAddress()));
        }
        Shop updated = shopRepository.save(shop);
        return shopMapper.toResponseDto(updated);
    }

    private Address updateAddress(@Valid AddressDto req, Address address) {
        address.setLandmark(req.getLandmark());
        address.setCity(req.getCity());
        address.setDistrict(req.getDistrict());
        address.setState(req.getLandmark());
        address.setCountry(req.getCountry());
        address.setPostalCode(req.getPostalCode());
        return addressRepository.save(address);
    }

    private boolean validateString(String request, String existing) {
        return request != null && !request.isBlank() && !request.equals(existing);
    }

    @Override
    @Transactional
    public void deleteShop(Long shopId) {
        if (shopRepository.existsById(shopId)) {
            shopRepository.deleteById(shopId);
        } else throw new ShopIdNotFoundException(shopId);
    }

    @Override
    @Transactional
    public ShopResponseDto updateShopStatus(Long shopId, ShopStatus status) {
        Shop shop = fetchShopByShopId(shopId);
        shop.setStatus(status);
        return shopMapper.toResponseDto(shop);
    }

    @Override
    public ShopResponseDto getShopDetails(Long shopId) {
        Shop shop = fetchShopByShopId(shopId);
        return shopMapper.toResponseDto(shop);
    }

    private Shop fetchShopByShopId(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> {
                    log.warn("shop not found");
                    return new ShopIdNotFoundException(shopId);
                });
    }

    private List<StaffPermission> getShopkeeperPermissions(ShopStaff shopStaff) {
        return Stream.of(
                Module.SHOP, Module.ORDER_ITEM_PRICE,
                Module.CUSTOMER, Module.ORDER, Module.PAYMENT,
                Module.STAFF, Module.STAFF_PERMISSION,
                Module.OPERATION)
                .map(module -> new StaffPermission(
                        shopStaff, module, PermissionLevel.FULL))
                .toList();
    }

    private Address resolveShopAddress(AddressDto ad) {
        return addressRepository.save(new Address(
                ad.getLandmark(),
                ad.getCity(),
                ad.getDistrict(),
                ad.getState(),
                ad.getCountry(),
                ad.getPostalCode()
        ));
    }

    private UserDetails resolveUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return userDetailsRepository.findByUser(user)
                .orElse(null);
    }
}
