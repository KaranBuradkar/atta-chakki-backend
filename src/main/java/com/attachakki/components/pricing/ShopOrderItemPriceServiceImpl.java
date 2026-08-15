package com.attachakki.components.pricing;

import com.attachakki.components.operation.ShopOperationService;
import com.attachakki.components.orderItem.OrderItem;
import com.attachakki.components.orderItem.OrderItemRepository;
import com.attachakki.components.shop.Shop;
import com.attachakki.components.shop.ShopRepository;
import com.attachakki.entity.type.Module;
import com.attachakki.exception.businessLogic.BusinessLogicException;
import com.attachakki.exception.entityNotFound.OrderItemNameNotFoundException;
import com.attachakki.exception.entityNotFound.ShopIdNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Transactional
public class ShopOrderItemPriceServiceImpl implements ShopOrderItemPriceService {

    private static final Logger log = LoggerFactory.getLogger(ShopOrderItemPriceServiceImpl.class);
    private final ShopOrderItemPriceRepository shopOrderItemPriceRepository;
    private final ShopOrderItemPriceMapper shopOrderItemPriceMapper;
    private final OrderItemRepository orderItemRepository;
    private final ShopRepository shopRepository;
    private final ShopOperationService shopOperationService;

    public ShopOrderItemPriceServiceImpl(
            ShopOrderItemPriceRepository shopOrderItemPriceRepository,
            ShopOrderItemPriceMapper shopOrderItemPriceMapper,
            OrderItemRepository orderItemRepository,
            ShopOperationService shopOperationService,
            ShopRepository shopRepository
    ) {
        this.shopOrderItemPriceRepository = shopOrderItemPriceRepository;
        this.shopOrderItemPriceMapper = shopOrderItemPriceMapper;
        this.orderItemRepository = orderItemRepository;
        this.shopOperationService = shopOperationService;
        this.shopRepository = shopRepository;
    }

    @Override
    public Page<ShopOrderItemPriceResponseDto> getShopOrderItemsPrice(
            Long shopId, Integer page, Integer size,
            String direction, String sort
    ) {
        Sort.Direction dir = ("asc".equalsIgnoreCase(direction)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Page<ShopOrderItemPrice> orderItemPricePage = shopOrderItemPriceRepository
                .findByShopId(shopId, PageRequest.of(page, size, dir, sort));
        return orderItemPricePage.map(shopOrderItemPriceMapper::toResponseDto);
    }

    @Override
    @Transactional
    public ShopOrderItemPriceResponseDto create(Long shopId, ShopOrderItemPriceRequestDto requestDto) {

        requestDto.setUnitPrice(validatePriceFormat(requestDto.getUnitPrice()));
        OrderItem orderItem = resolveOrderItem(requestDto.getOrderItemName());
        Shop shop = fetchShop(shopId);

        ShopOrderItemPrice entity = shopOrderItemPriceMapper.toEntity(requestDto);
        entity.setShop(shop);
        entity.setOrderItem(orderItem);
        ShopOrderItemPrice orderItemPrice = shopOrderItemPriceRepository.save(entity);
        ShopOrderItemPriceResponseDto responseDto = shopOrderItemPriceMapper.toResponseDto(orderItemPrice);

        shopOperationService.createModule(shopId, 1L, Module.ORDER_ITEM_PRICE, responseDto.id(), responseDto.toString());
        return responseDto;
    }

    @Override
    @Transactional
    public ShopOrderItemPriceResponseDto update(
            Long shopId, Long orderItemPriceId,
            ShopOrderItemPriceRequestDto updateRequest
    ) {
        ShopOrderItemPrice orderItemPrice = fetchOrderItemPrice(orderItemPriceId, shopId);
        ShopOrderItemPriceResponseDto before = shopOrderItemPriceMapper.toResponseDto(orderItemPrice);
        StringBuilder fields = new StringBuilder("[");

        BigDecimal requestedPrice = validatePriceFormat(updateRequest.getUnitPrice());
        if (requestedPrice != null
                && orderItemPrice.getUnitPrice().compareTo(requestedPrice) != 0
        ) {
            orderItemPrice.setUnitPrice(requestedPrice);
            fields.append(", unitPrice");
        }

        if (updateRequest.getOrderItemName() != null
                && !updateRequest.getOrderItemName().isBlank()
                && !updateRequest.getOrderItemName().equals(orderItemPrice.getOrderItem().getName())
        ) {
            OrderItem orderItem = resolveOrderItem(updateRequest.getOrderItemName());
            orderItemPrice.setOrderItem(orderItem);
            fields.append(", orderItemName");
        }

        if (!orderItemPrice.getQuantityType().equals(updateRequest.getQuantityType())) {
            orderItemPrice.setQuantityType(updateRequest.getQuantityType());
            fields.append(", quantityType");
        }

        fields.append("]");
        ShopOrderItemPrice updated = shopOrderItemPriceRepository.save(orderItemPrice);
        ShopOrderItemPriceResponseDto responseDto = shopOrderItemPriceMapper.toResponseDto(updated);

        shopOperationService.updateModule(shopId, 1L, Module.ORDER_ITEM_PRICE, responseDto.id(),
                fields.toString(), before.toString(), responseDto.toString());
        return responseDto;
    }

    @Override
    @Transactional
    public void delete(Long shopId, Long orderItemPriceId) {
        ShopOrderItemPrice shopOrderItemPrice = fetchOrderItemPrice(orderItemPriceId, shopId);
        ShopOrderItemPriceResponseDto before = shopOrderItemPriceMapper.toResponseDto(shopOrderItemPrice);
        shopOrderItemPrice.setAvailable(false);
        shopOperationService.deleteModule(shopId, 1L, Module.ORDER_ITEM_PRICE,
                before.id(), before.toString());
    }

    private ShopOrderItemPrice fetchOrderItemPrice(Long orderItemPriceId, Long shopId) {
        return shopOrderItemPriceRepository.findByIdAndShopIdAndAvailableTrue(orderItemPriceId, shopId)
                .orElseThrow(() -> {
                    log.warn("orderItemPrice not found");
                    return new OrderItemNameNotFoundException(orderItemPriceId);
                });
    }

    // util methods
    private BigDecimal validatePriceFormat(BigDecimal price) {
        if (price == null) return null;
        // Must be positive
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessLogicException("Price cannot be negative", null);
        }

        // Must not have more than 2 decimal places
        if (price.scale() > 2) {
            throw new BusinessLogicException("Price must have at most 2 decimal places", null);
        }

        // Normalize price to exactly 2 decimals for storage and calculations
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    private OrderItem resolveOrderItem(String orderItemName) {
        OrderItem orderItem = orderItemRepository
                .findByName(orderItemName).orElse(null);

        if (orderItem == null) {
            orderItem = orderItemRepository
                    .save(new OrderItem(orderItemName));
        }
        return orderItem;
    }

    private Shop fetchShop(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> {
                    log.debug("Shop not found");
                    return new ShopIdNotFoundException(shopId);
                });
    }
}
