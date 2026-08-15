package com.attachakki.exception.entityNotFound;

public class ShopStaffNotFoundException extends EntityNotFoundException{
    public ShopStaffNotFoundException(Long shopStaffId) {
        super("shopStaff not found with shopId-"+shopStaffId, null);
    }
}
