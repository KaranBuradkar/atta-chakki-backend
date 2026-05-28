package com.atachakki.components.staff;

import com.atachakki.controller.BaseController;
import com.atachakki.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Shop Staff Module",
        description = "Operations related to shop staff management"
)
@RestController
@RequestMapping("/v1/shops/{shopId}/staffs")
public class ShopStaffController extends BaseController {

    private final ShopStaffService shopStaffService;

    protected ShopStaffController(
            HttpServletRequest request,
            ShopStaffService shopStaffService
    ) {
        super(request);
        this.shopStaffService = shopStaffService;
    }

    @Operation(
            summary = "Fetch shop staffs",
            description = "Retrieve paginated list of staffs belonging to a shop"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<StaffResponseDto>>> fetchShopStaffs(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") Integer size,

            @Parameter(description = "Sort direction (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction,

            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sort
    ) {
        Page<StaffResponseDto> responsePage =
                shopStaffService.findShopStaffs(shopId, page, size, direction, sort);

        return apiResponse(HttpStatus.OK, "Shop staffs fetched successfully", responsePage);
    }

    @Operation(
            summary = "Fetch shop staff by ID",
            description = "Retrieve a single shop staff by staff ID"
    )
    @GetMapping("/{staffId}")
    public ResponseEntity<ApiResponse<StaffResponseDto>> fetchShopStaff(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Shop Staff ID", required = true)
            @PathVariable Long staffId
    ) {
        StaffResponseDto responseDto =
                shopStaffService.findShopStaff(shopId, staffId);

        return apiResponse(HttpStatus.OK, "Shop staff fetched successfully", responseDto);
    }

    @Operation(
            summary = "Create shop staff",
            description = "Register a new staff member for a shop"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<StaffResponseDto>> createShopStaff(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable Long shopId,

            @Valid @RequestBody StaffRequestDto requestDto
    ) {
        StaffResponseDto responseDto =
                shopStaffService.create(shopId, requestDto);

        return apiResponse(HttpStatus.CREATED, "New shop staff registered successfully", responseDto);
    }

    @Operation(
            summary = "Update shop staff",
            description = "Update details of an existing shop staff"
    )
    @PatchMapping("/{shopStaffId}")
    public ResponseEntity<ApiResponse<StaffResponseDto>> updateShopStaff(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Shop Staff ID", required = true)
            @PathVariable Long shopStaffId,

            @RequestBody StaffRequestDto requestDto
    ) {
        StaffResponseDto responseDto =
                shopStaffService.update(shopId, shopStaffId, requestDto);

        return apiResponse(HttpStatus.OK, "Shop staff updated successfully", responseDto);
    }

    @Operation(
            summary = "Delete shop staff",
            description = "Remove a staff member from the shop"
    )
    @DeleteMapping("/{shopStaffId}")
    public ResponseEntity<ApiResponse<Void>> deleteShopStaff(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Shop Staff ID", required = true)
            @PathVariable Long shopStaffId
    ) {
        shopStaffService.deleteById(shopId, shopStaffId);
        return apiResponse(HttpStatus.OK, "Shop staff deleted successfully", null);
    }
}
