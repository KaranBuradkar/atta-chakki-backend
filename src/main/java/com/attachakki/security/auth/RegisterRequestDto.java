package com.attachakki.security.auth;

import com.attachakki.entity.type.AuthProvider;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public class RegisterRequestDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be 2 to 50 characters")
    private String name;

    @Email
    @Length(min = 2, max = 100)
    @NotBlank(message = "Email can not blank")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be 6 to 20 characters")
    private String password;

    @Pattern(
            regexp = "^(?:(?:\\+91[\\s-]?)|(?:0)|(?:91))?(?:[6-9]\\d{9}|[1-9]\\d{2,4}[\\s-]?\\d{6,8})$",
            message = "Invalid Indian phone number format"
    )
    private String phoneNo;

    @NotNull(message = "Provider required")
    private AuthProvider provider = AuthProvider.LOCAL;

    public RegisterRequestDto() {}

    public RegisterRequestDto(
            String username,
            String password
    ) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }
}
