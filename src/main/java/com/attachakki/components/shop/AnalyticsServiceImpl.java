package com.attachakki.components.shop;

import com.attachakki.components.customer.CustomerRepository;
import com.attachakki.components.order.OrderRepository;
import com.attachakki.components.payment.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AnalyticsServiceImpl implements AnalyticsService{

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;


    public AnalyticsServiceImpl(CustomerRepository customerRepository,
                                OrderRepository orderRepository,
                                PaymentRepository paymentRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public ShopOverviewResponseDto getShopOverview(Long shopId) {
        Integer customerCount = customerRepository.countByShopIdAndDeletedFalse(shopId).orElse(-1);
        Integer orderCount = orderRepository.countByCustomerShopId(shopId).orElse(-1);
        BigDecimal totalBalance = orderRepository.totalBalance(shopId).orElse(BigDecimal.ZERO);
        BigDecimal collectAmount = paymentRepository.totalCollectionAmount(shopId).orElse(BigDecimal.ZERO);
        return new ShopOverviewResponseDto(customerCount, orderCount, collectAmount.toString(), totalBalance.toString());
    }
}
