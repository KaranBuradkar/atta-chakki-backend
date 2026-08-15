package com.attachakki.components.pricing;

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

@Tag(
        name = "Order Item Price Module",
        description = "APIs for managing shop-specific order item pricing"
)
@RestController
@RequestMapping("/v1/shops/{shopId}/prices")
public class ShopOrderItemPriceController extends BaseController {

    private final ShopOrderItemPriceService orderItemPriceService;

    protected ShopOrderItemPriceController(
            HttpServletRequest request,
            ShopOrderItemPriceService orderItemPriceService
    ) {
        super(request);
        this.orderItemPriceService = orderItemPriceService;
    }

    @Operation(
            summary = "Fetch shop order item prices",
            description = "Retrieve paginated list of order item prices configured for a shop",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Order item prices fetched successfully",
                            content = @Content(
                                    schema = @Schema(implementation = ShopOrderItemPriceResponseDto.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ShopOrderItemPriceResponseDto>>> fetchShopOrderItemPrice(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") Integer size,

            @Parameter(description = "Sort direction (asc / desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction,

            @Parameter(description = "Sort field", example = "orderItem.name")
            @RequestParam(defaultValue = "orderItem.name") String sort
    ) {
        Page<ShopOrderItemPriceResponseDto> responseDtoPage =
                orderItemPriceService.getShopOrderItemsPrice(
                        shopId, page, size, direction, sort
                );

        return apiResponse(HttpStatus.OK, "Shop order item prices fetched successfully", responseDtoPage);
    }

    @Operation(
            summary = "Create order item price",
            description = "Create pricing for an order item in a shop",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "Order item price created successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid pricing data"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ShopOrderItemPriceResponseDto>> createOrderItemPrice(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Valid @RequestBody ShopOrderItemPriceRequestDto requestDto
    ) {
        ShopOrderItemPriceResponseDto response =
                orderItemPriceService.create(shopId, requestDto);

        return apiResponse(HttpStatus.CREATED, "Order item price created successfully", response);
    }

    @Operation(
            summary = "Update order item price",
            description = "Update pricing of an existing order item",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Order item price updated successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Order item price not found"
                    )
            }
    )
    @PatchMapping("/{orderItemPriceId}")
    public ResponseEntity<ApiResponse<ShopOrderItemPriceResponseDto>> updateOrderItemPrice(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Order Item Price ID", example = "300", required = true)
            @PathVariable Long orderItemPriceId,

            @RequestBody ShopOrderItemPriceRequestDto updateRequest
    ) {
        ShopOrderItemPriceResponseDto response =
                orderItemPriceService.update(shopId, orderItemPriceId, updateRequest);

        return apiResponse(HttpStatus.OK, "Order item price updated successfully", response);
    }

    @Operation(
            summary = "Delete order item price",
            description = "Delete an order item price configuration",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Order item price deleted successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Order item price not found"
                    )
            }
    )
    @DeleteMapping("/{orderItemPriceId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrderItemPrice(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Order Item Price ID", example = "300", required = true)
            @PathVariable Long orderItemPriceId
    ) {
        orderItemPriceService.delete(shopId, orderItemPriceId);
        return apiResponse(HttpStatus.OK, "Order item price deleted successfully", null);
    }
}
