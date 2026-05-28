package com.atachakki.services;

import com.atachakki.components.customer.CustomerResponseDto;
import com.atachakki.components.order.OrderResponseDto;
import com.atachakki.components.payment.PaymentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExportService {

    public ByteArrayInputStream exportCustomersToCsv(Page<CustomerResponseDto> customers) {

        StringBuilder sb = new StringBuilder();
        sb.append("ID,Name,Email,Debt,CreatedAt\n");

        for (CustomerResponseDto c : customers) {
            sb.append(c.id()).append(",");
            sb.append(safe(c.name())).append(",");
            sb.append(safe(c.email())).append(",");
            sb.append("FAILED").append(",");
            sb.append(c.createdAt()).append("\n");
        }

        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    private String safe(String s) {
        return (s == null ? "NA" : s.replace(",", " "));
    }

    public ByteArrayInputStream exportOrdersToCsv(
            CustomerResponseDto c,
            Page<OrderResponseDto> orders
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID, Name, Email, Balance, CreatedAt\n");
        sb.append(c.id()).append(",");
        sb.append(safe(c.name())).append(",");
        sb.append(safe(c.email())).append(",");
        sb.append("FAILED").append(",");
        sb.append(c.createdAt()).append("\n\n");

        sb.append("ID,OrderDate,ItemName,Quantity,QuantityType,TotalAmount," +
                "PaymentStatus,addedByName,updatedByName,CreatedAt, UpdatedAt\n");

        for (OrderResponseDto o : orders) {
            sb.append(o.id()).append(",");
            sb.append(o.orderDate().toString()).append(",");
            sb.append(safe(o.orderItemName())).append(",");
            sb.append(safe(o.quantity().toString())).append(",");
            sb.append(safe(o.quantityType().toString())).append(",");
            sb.append(safe(o.totalAmount().toString())).append(",");
            sb.append(safe(o.paymentStatus().toString())).append(",");
            sb.append(safe(o.addedByName())).append(",");
            sb.append(safe(o.updatedByName())).append(",");
            sb.append(o.createdAt().toString()).append(",");
            sb.append(o.updatedAt()).append("\n");
        }

        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    public ByteArrayInputStream exportCustomersToPdf(Page<CustomerResponseDto> customers) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream exportCustomerDetailsToCsv(
            CustomerResponseDto customer, Page<OrderResponseDto> orders,
            Page<PaymentResponseDto> payments) {
        StringBuilder sb = new StringBuilder();

        sb.append("ID, Name, Email, Balance, CreatedAt\n");
        sb.append(customer.id()).append(", ");
        sb.append(safe(customer.name())).append(", ");
        sb.append(safe(customer.email())).append(", ");
        sb.append(customer.balance()).append(", ");
        sb.append(customer.createdAt()).append("\n\n");

        sb.append("ID,OrderDate,ItemName,quantity,quantityType,totalAmount," +
                "paymentStatus,addedByName,updatedByName,CreatedAt, UpdatedAt, ," +
                "ID, DATE, AMOUNT, MODE, STATUS, ReceiverName, CreatedAt, UpdatedAt\n");
        List<OrderResponseDto> oContent = orders.getContent();
        List<PaymentResponseDto> pContent = payments.getContent();
        int iterations = Math.max(oContent.size(), pContent.size());

        for (int i=0; i < iterations; i++) {

            if (i < oContent.size()) {
                OrderResponseDto o = oContent.get(i);
                sb.append(o.id()).append(",");
                sb.append(o.orderDate().toString()).append(",");
                sb.append(safe(o.orderItemName())).append(",");
                sb.append(safe(o.quantity().toString())).append(",");
                sb.append(safe(o.quantityType().toString())).append(",");
                sb.append(safe(o.totalAmount().toString())).append(",");
                sb.append(safe(o.paymentStatus().toString())).append(",");
                sb.append(safe(o.addedByName())).append(",");
                sb.append(safe(o.updatedByName())).append(",");
                sb.append(o.createdAt().toString()).append(",");
                sb.append(o.updatedAt()).append(", ,");
            }

            if (i < pContent.size()) {
                PaymentResponseDto p = pContent.get(i);
                sb.append(p.id()).append(",");
                sb.append(p.paymentDate().toString()).append(",");
                sb.append(safe(p.amount().toString())).append(",");
                sb.append(safe(p.mode().toString())).append(",");
                sb.append(safe(p.status().name())).append(",");
                sb.append(safe(p.receiverName())).append(",");
                sb.append(safe(p.createdAt().toString())).append(",");
                sb.append(safe(p.updatedAt().toString())).append("\n");
            }
        }

        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    public ByteArrayInputStream exportCustomerDetailsToPdf(
            CustomerResponseDto c, Page<OrderResponseDto> orders,
            Page<PaymentResponseDto> payments) {
        StringBuilder sb = new StringBuilder();
        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
