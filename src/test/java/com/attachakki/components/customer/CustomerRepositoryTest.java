package com.attachakki.components.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryTest {

    @Mock
    private CustomerRepository customerRepository;

    @Test
    void findIdsByShopIdAndDeletedFalse() {
    }

    @Test
    void findIdsByShopIdAndNameContainingAndDeletedFalse() {
    }

    @Test
    void findByIdAndShopId() {
    }
}