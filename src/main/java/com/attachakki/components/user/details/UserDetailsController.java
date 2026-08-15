package com.attachakki.components.user.details;

import com.attachakki.controller.BaseController;
import com.attachakki.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "User Details Module",
        description = "Operations related to user profile and personal information"
)
@RestController
@RequestMapping("/v1/users")
public class UserDetailsController extends BaseController {

    private final UserInfoDetailsService userInfoDetailsService;

    protected UserDetailsController(
            HttpServletRequest request,
            UserInfoDetailsService userInfoDetailsService
    ) {
        super(request);
        this.userInfoDetailsService = userInfoDetailsService;
    }

    @Operation(
            summary = "Fetch all users",
            description = "Retrieve paginated list of all user details (admin/staff access)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Users fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserInfoUpdate>>> fetchUsers(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") Integer size,

            @Parameter(description = "Sort direction (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction,

            @Parameter(description = "Sort field", example = "name")
            @RequestParam(defaultValue = "name") String sort
    ) {
        Page<UserInfoUpdate> response =
                userInfoDetailsService.findUserInfos(page, size, direction, sort);

        return apiResponse(HttpStatus.OK, "User details fetched successfully", response);
    }

    @Operation(
            summary = "Fetch logged-in user's details",
            description = "Retrieve profile information of the currently authenticated user"
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDetailResponseDto>> fetchUserInfo() {
        UserDetailResponseDto responseDto =
                userInfoDetailsService.findUserInfos();

        return apiResponse(HttpStatus.OK, "User details fetched successfully", responseDto);
    }

    @Operation(
            summary = "Update logged-in user's details",
            description = "Update profile information of the currently authenticated user"
    )
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserDetailResponseDto>> updateUserInfo(
            @RequestBody UserInfoUpdate updateRequest
    ) {
        UserDetailResponseDto responseDto =
                userInfoDetailsService.update(updateRequest);
        return apiResponse(HttpStatus.OK, "User details updated successfully", responseDto);
    }

    @Operation(
            summary = "Delete logged-in user's account",
            description = "Permanently delete the logged-in user's account"
    )
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUserInfo() {
        userInfoDetailsService.delete();
        return apiResponse(HttpStatus.OK, "User details deleted successfully", null);
    }
}
