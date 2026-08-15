package com.attachakki.components.address;

import com.attachakki.exception.entityNotFound.AddressNotFoundException;
import com.attachakki.exception.AppException;
import com.attachakki.exception.validation.EntityIdProvidedException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class AddressServiceImpl implements AddressService{

    private static final Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public AddressServiceImpl(AddressRepository addressRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    @Override
    public AddressDto getAddress(Long addressId) {
        Address address = resolveAddress(addressId);
        return addressMapper.toDto(address);
    }

    private Address resolveAddress(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> {
                    log.warn("Invalid address id {} provided", addressId);
                    return new AddressNotFoundException(addressId);
                });
    }

    @Override
    @Transactional
    public List<AddressDto> getAddressPage(int page, int size, String[] sort, String by) {
        Sort.Direction dir = by.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Page<Address> addresses = addressRepository.findAll(PageRequest.of(page, size, dir, sort));
        return addresses.stream().map(addressMapper::toDto).toList();
    }

    @Override
    @Transactional
    public AddressDto createAddress(AddressDto addressDto) {
        if (addressDto.getId() != null) {
            log.warn("creating address id auto generate");
            throw new EntityIdProvidedException("address");
        }
        Address entity = addressMapper.toEntity(addressDto);
        Address saveAddress = addressRepository.save(entity);
        return addressMapper.toDto(saveAddress);
    }

    @Override
    @Transactional
    public AddressDto updateAddress(Long addressId, Map<String, Objects> map) {
        Address address = resolveAddress(addressId);
        map.forEach(
                (key, value) -> {
                    switch (key) {
                        case "landmark" -> address.setLandmark(value.toString());
                        case "city" -> address.setCity(value.toString());
                        case "state" -> address.setState(value.toString());
                        case "country" -> address.setCountry(value.toString());
                        case "postalCode" -> address.setPostalCode(value.toString());
                        default -> {
                            log.warn("unknown update field provided");
                            throw new AppException("Unknown field provided", null);
                        }
                    }
                }
        );
        return addressMapper.toDto(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId) {
        if (addressRepository.existsById(addressId)) {
            addressRepository.deleteById(addressId);
            return;
        }
        throw new AddressNotFoundException(addressId);
    }
}
