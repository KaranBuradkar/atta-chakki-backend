package com.attachakki.components.permissions;

import com.attachakki.controller.BaseController;
import com.attachakki.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Staff Permission Module",
        description = "APIs for managing staff permissions"
)
@RestController
@RequestMapping("/v1/shops/{shopId}/permissions")
public class StaffPermissionController extends BaseController {

    private final StaffPermissionService staffPermissionService;

    protected StaffPermissionController(
            HttpServletRequest request,
            StaffPermissionService staffPermissionService
    ) {
        super(request);
        this.staffPermissionService = staffPermissionService;
    }

    @Operation(
            summary = "Fetch staff permissions",
            description = "Retrieve paginated permissions assigned to a staff member",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "302",
                            description = "Permissions fetched successfully",
                            content = @Content(
                                    schema = @Schema(implementation = StaffPermissionResponseDto.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Staff or Shop not found"
                    )
            }
    )
    @GetMapping("/staffs/{staffId}")
    public ResponseEntity<ApiResponse<Page<StaffPermissionResponseDto>>> fetchPermissions(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Staff ID", example = "20", required = true)
            @PathVariable Long staffId,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") Integer size,

            @Parameter(description = "Sort direction (asc / desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction,

            @Parameter(
                    description = "Sort fields",
                    example = "module"
            )
            @RequestParam(defaultValue = "module") String[] sort
    ) {
        Page<StaffPermissionResponseDto> responsePage =
                staffPermissionService.findStaffPermissions(
                        shopId, staffId, page, size, direction, sort
                );

        return apiResponse(HttpStatus.OK, "Permissions fetched successfully", responsePage);
    }

    @GetMapping("/staffs/{staffId}/bunch")
    public ResponseEntity<ApiResponse<Page<PermissionResponseDto>>> getPermissions(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Staff ID", example = "20", required = true)
            @PathVariable Long staffId,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") Integer size,

            @Parameter(description = "Sort direction (asc / desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction,

            @Parameter(
                    description = "Sort fields",
                    example = "module"
            )
            @RequestParam(defaultValue = "module") String[] sort
    ) {
        Page<PermissionResponseDto> permissions = staffPermissionService
                .findPermissions(shopId, staffId, page, size, direction, sort);
        return apiResponse(HttpStatus.OK, "Permissions fetched successfully", permissions);
    }

    @Operation(
            summary = "Grant staff permissions",
            description = "Assign one or more permissions to a staff member",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "Permissions granted successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid permission data"
                    )
            }
    )
    @PostMapping("/staffs/{staffId}")
    public ResponseEntity<ApiResponse<List<StaffPermissionResponseDto>>> createPermissions(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Staff ID", example = "20", required = true)
            @PathVariable Long staffId,

            @RequestBody List<@Valid StaffPermissionRequestDto> requestDto
    ) {
        List<StaffPermissionResponseDto> responseDto =
                staffPermissionService.create(shopId, staffId, requestDto);

        return apiResponse(HttpStatus.CREATED, "Permission granted successfully", responseDto);
    }

    @Operation(
            summary = "Revoke staff permission",
            description = "Delete a specific permission assigned to a staff member",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Permission deleted successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Permission not found"
                    )
            }
    )
    @DeleteMapping("/{permissionId}")
    public ResponseEntity<ApiResponse<Void>> deletePermission(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Permission ID", example = "50", required = true)
            @PathVariable Long permissionId
    ) {
        staffPermissionService.delete(shopId, List.of(permissionId));
        return apiResponse(HttpStatus.OK, "Permission deleted successfully", null);
    }

    @Operation(
            summary = "Revoke staff permissions",
            description = "Delete multiple permissions assigned to a staff member",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Permissions deleted successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Permission not found"
                    )
            }
    )
    @PutMapping("/permissions")
    public ResponseEntity<ApiResponse<Void>> deletePermissions(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Permission IDs", example = "[51, 24]", required = true)
            @RequestBody List<Long> permissionIds
    ) {
        staffPermissionService.delete(shopId, permissionIds);
        return apiResponse(HttpStatus.OK, "Permissions deleted successfully", null);
    }
}
