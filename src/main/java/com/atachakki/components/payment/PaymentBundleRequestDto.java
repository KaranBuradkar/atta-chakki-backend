package com.atachakki.components.payment;

import com.atachakki.components.customerLedger.CustomerLedgerRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PaymentBundleRequestDto {

    @NotNull
    @NotEmpty
    private List<Long> orderIds;
    @NotNull
    @Valid
    private PaymentRequestDto paymentRequestDto;
    @Valid
    private CustomerLedgerRequestDto customerLedgerRequestDto;
    @Valid
    private CustomerLedgerRequestDto finalCustomerLedgerRequestDto;

    public PaymentBundleRequestDto() {
    }

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

    public CustomerLedgerRequestDto getFinalCustomerLedgerRequestDto() {
        return finalCustomerLedgerRequestDto;
    }

    public CustomerLedgerRequestDto getCustomerLedgerRequestDto() {
        return customerLedgerRequestDto;
    }

    public void setCustomerLedgerRequestDto(CustomerLedgerRequestDto customerLedgerRequestDto) {
        this.customerLedgerRequestDto = customerLedgerRequestDto;
    }

    public void setFinalCustomerLedgerRequestDto(CustomerLedgerRequestDto finalCustomerLedgerRequestDto) {
        this.finalCustomerLedgerRequestDto = finalCustomerLedgerRequestDto;
    }
}
