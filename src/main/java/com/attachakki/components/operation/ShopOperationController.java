package com.attachakki.components.operation;

import com.attachakki.controller.BaseController;
import com.attachakki.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Shop Operations",
        description = "APIs related to shop operation history and management"
)
@RestController
@RequestMapping("/v1/shops/{shopId}/shop-operations")
public class ShopOperationController extends BaseController {

    private final ShopOperationService shopOperationService;

    protected ShopOperationController(
            HttpServletRequest request,
            ShopOperationService shopOperationService
    ) {
        super(request);
        this.shopOperationService = shopOperationService;
    }

    @Operation(
            summary = "Fetch shop operations",
            description = "Retrieve paginated shop operation history for a specific shop",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Shop operations fetched successfully",
                            content = @Content(
                                    schema = @Schema(implementation = ShopOperationResponseDto.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Shop not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ShopOperationResponseDto>>> fetchNotifications(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable(value = "shopId") Long shopId,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(value = "size", defaultValue = "10") Integer size,

            @Parameter(description = "Sort direction (asc or desc)", example = "desc")
            @RequestParam(value = "dir", defaultValue = "desc") String direction,

            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort
    ) {
        Page<ShopOperationResponseDto> responseDto =
                shopOperationService.findShopOperation(shopId, page, size, direction, sort);

        return apiResponse(HttpStatus.OK, "Shop operations fetched successfully", responseDto);
    }

    @Operation(
            summary = "Delete shop operation",
            description = "Delete a specific shop operation by operation ID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shop operation deleted successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Operation not found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @DeleteMapping("/{operationId}")
    public ResponseEntity<ApiResponse<Void>> deleteOperationId(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Operation ID", example = "100", required = true)
            @PathVariable Long operationId
    ) {
        shopOperationService.delete(shopId, operationId);
        return apiResponse(HttpStatus.OK, "Shop operation deleted successfully", null);
    }
}
