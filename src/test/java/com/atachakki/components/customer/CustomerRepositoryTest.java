package com.atachakki.components.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

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