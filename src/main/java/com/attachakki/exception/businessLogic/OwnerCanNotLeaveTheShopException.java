package com.attachakki.exception.businessLogic;

public class OwnerCanNotLeaveTheShopException extends BusinessLogicException {
    public OwnerCanNotLeaveTheShopException(Long shopId, Long staffId) {
        super(String.format("SHOP_OWNER can not leave the shop-%s",shopId),
                String.format("Provided ShopStaffId-%s is the OWNER of the Shop-%s", staffId, shopId));
    }
}
