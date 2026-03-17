package com.atachakki.components.payment;

import com.atachakki.components.customerLedger.CustomerLedgerRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PaymentBundleRequestDto {

    @NotNull @NotEmpty
    private List<Long> orderIds;
    @NotNull
    @Valid private PaymentRequestDto paymentRequestDto;
    @Valid private CustomerLedgerRequestDto customerLedgerRequestDto;
    @Valid private CustomerLedgerRequestDto nowNewCustomerLedgerRequestDto;

    public PaymentBundleRequestDto() {}

    public List<Long> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }

    public PaymentRequestDto getPaymentRequestDto() {
        return paymentRequestDto;
    }

    public void setPaymentRequestDto(PaymentRequestDto paymentRequestDto) {
        this.paymentRequestDto = paymentRequestDto;
    }

    public CustomerLedgerRequestDto getDueOrRefundRequestDto() {
        return customerLedgerRequestDto;
    }

    public void setDueOrRefundRequestDto(CustomerLedgerRequestDto customerLedgerRequestDto) {
        this.customerLedgerRequestDto = customerLedgerRequestDto;
    }

    public CustomerLedgerRequestDto getNowNewDueOrRefundRequestDto() {
        return nowNewCustomerLedgerRequestDto;
    }

    public void setNowNewDueOrRefundRequestDto(CustomerLedgerRequestDto nowNewCustomerLedgerRequestDto) {
        this.nowNewCustomerLedgerRequestDto = nowNewCustomerLedgerRequestDto;
    }
}
