package com.attachakki.components.orderItem;

import com.attachakki.exception.businessLogic.OrderItemNameAlreadyExistException;
import com.attachakki.exception.entityNotFound.OrderItemNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private static final Logger log = LoggerFactory.getLogger(OrderItemServiceImpl.class);
    private final OrderItemRepository orderItemRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public Page<OrderItemResponseDto> findOrderItems(
            Integer page, Integer size,
            String direction, String sort
    ) {
        Sort.Direction dir = ("asc".equalsIgnoreCase(direction)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Page<OrderItem> orderItemPage = orderItemRepository
                .findAll(PageRequest.of(page, size, dir, sort));
        return orderItemPage.map(this::toResponseDto);
    }

    @Override
    @Transactional
    public OrderItemResponseDto create(OrderItemRequestDto requestDto) {
        OrderItem orderItem = orderItemRepository.findByName(requestDto.getName()).orElse(null);
        if (orderItem != null) {
            log.debug("orderItem already exist");
            throw new OrderItemNameAlreadyExistException(requestDto.getName());
        }
        orderItem = new OrderItem();
        orderItem.setName(requestDto.getName());
        OrderItem saveOrderItem = orderItemRepository.save(orderItem);
        return toResponseDto(saveOrderItem);
    }

    @Override
    @Transactional
    public OrderItemResponseDto update(Long orderItemId, OrderItemRequestDto requestDto) {
        OrderItem orderItem = fetchOrderItem(orderItemId);
        if (requestDto.getName() != null
                && !requestDto.getName().isBlank()
                && !requestDto.getName().equalsIgnoreCase(orderItem.getName())
        ) {
            orderItem.setName(requestDto.getName());
        }
        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
        return toResponseDto(updatedOrderItem);
    }

    @Override
    @Transactional
    public void deleteById(Long orderItemId) {
        OrderItem orderItem = fetchOrderItem(orderItemId);
        orderItemRepository.delete(orderItem);
    }

    private OrderItem fetchOrderItem(Long orderItemId) {
        return orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> {
                    log.warn("OrderItem not found");
                    return new OrderItemNotFoundException(orderItemId);
                });
    }

    private OrderItemResponseDto toResponseDto(OrderItem oi) {
        return new OrderItemResponseDto(oi.getId(), oi.getName());
    }
}
