package com.attachakki.security.auth;

import com.attachakki.controller.BaseController;
import com.attachakki.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "User register or login or refresh access token")
@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController extends BaseController {

    private final AuthService authService;

    protected AuthController(HttpServletRequest request, AuthService authService) {
        super(request);
        this.authService = authService;
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = RegisterResponseDto.class))}
            )
    )
    @Operation(summary = "Create new account", description = "register with credentials to create new account")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDto>> register(
            @RequestBody @Valid RegisterRequestDto requestDto
    ) {
        RegisterResponseDto responseDto = authService.register(requestDto);
        return apiResponse(HttpStatus.CREATED, "New account created successfully", responseDto);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = RegisterResponseDto.class))}
            )
    )
    @Operation(summary = "Login user", description = "Login with required credentials")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<RegisterResponseDto>> login(
            @RequestBody @Valid LoginRequestDto requestDto
    ) {
        RegisterResponseDto responseDto = authService.login(requestDto);
        return apiResponse(HttpStatus.OK, "Login successfully", responseDto);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccessTokenDto.class))}
            )
    )
    @Operation(summary = "Refresh Access Token", description = "Provide refreshToken to get new access token")
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AccessTokenDto>> refreshToken() {
        AccessTokenDto accessTokenDto = authService.refreshToken(request);
        return apiResponse(HttpStatus.OK, "Access token successfully refresh", accessTokenDto);
    }
}
