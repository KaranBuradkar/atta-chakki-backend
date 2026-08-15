package com.attachakki.security.auth;

import com.attachakki.entity.type.AuthProvider;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

public class LoginRequestDto {

    @Email
    @Length(min = 2, max = 100)
    @NotBlank(message = "Email can not blank")
    private String username;
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be 6 to 20 characters")
    private String password;
    private AuthProvider provider = AuthProvider.LOCAL;

    public LoginRequestDto() {}

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

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }
}
