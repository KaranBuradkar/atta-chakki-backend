package com.attachakki.exception.entityNotFound;

public class AddressNotFoundException extends EntityNotFoundException {

    public AddressNotFoundException(Long addressId) {
        super("Address not found with address id "+addressId, null);
    }
}
