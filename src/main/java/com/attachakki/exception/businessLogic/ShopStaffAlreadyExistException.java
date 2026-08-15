package com.attachakki.exception.businessLogic;

public class ShopStaffAlreadyExistException extends BusinessLogicException {
    public ShopStaffAlreadyExistException(String username) {
        super("ShopStaff already exist with username "+username,
                "ShopStaff already exist with username "+username);
    }
}
