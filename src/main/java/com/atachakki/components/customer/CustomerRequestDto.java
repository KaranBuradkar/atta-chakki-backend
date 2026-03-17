package com.atachakki.components.customer;

import com.atachakki.components.customerLedger.CustomerLedgerType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CustomerRequestDto {

    @Size(min = 2, max = 150, message = "Name must be 2 to 150 characters")
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", message = "Debt must be positive")
    private BigDecimal balance;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 2, max = 150, message = "Specification must be 2 to 150 characters")
    private String specification;

    @NotNull
    private CustomerLedgerType customerLedgerType = CustomerLedgerType.DUE;

    @NotNull
    private Long date = System.currentTimeMillis();

    public CustomerRequestDto() {}

    public CustomerRequestDto(
            String name,
            BigDecimal balance,
            String email,
            String specification
    ) {
        this.name = name;
        this.balance = balance;
        this.email = email;
        this.specification = specification;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }

    public CustomerLedgerType getDueRefundType() {
        return customerLedgerType;
    }
    public void setDueRefundType(CustomerLedgerType customerLedgerType) {
        this.customerLedgerType = customerLedgerType;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "CustomerRequestDto{" +
                "name='" + name + '\'' +
                ", debt=" + balance +
                ", email='" + email + '\'' +
                ", specification='" + specification + '\'' +
                '}';
    }
}
