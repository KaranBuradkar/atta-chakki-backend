package com.attachakki.components.orderItem;

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
        name = "Order Item Module",
        description = "APIs for managing order items"
)
@RestController
@RequestMapping("/v1/order-items")
public class OrderItemController extends BaseController {

    private final OrderItemService orderItemService;

    protected OrderItemController(
            HttpServletRequest request,
            OrderItemService orderItemService
    ) {
        super(request);
        this.orderItemService = orderItemService;
    }

    @Operation(
            summary = "Fetch order items",
            description = "Retrieve paginated list of order items",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "302",
                            description = "Order items fetched successfully",
                            content = @Content(
                                    schema = @Schema(implementation = OrderItemResponseDto.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderItemResponseDto>>> fetchOrderItems(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") Integer size,

            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction,

            @Parameter(description = "Sort field", example = "name")
            @RequestParam(defaultValue = "name") String sort
    ) {
        Page<OrderItemResponseDto> responsePage =
                orderItemService.findOrderItems(page, size, direction, sort);

        return apiResponse(HttpStatus.OK, "OrderItems fetched successfully", responsePage);
    }

    @Operation(
            summary = "Create order item",
            description = "Create a new order item",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "Order item created successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<ApiResponse<OrderItemResponseDto>> createOrderItem(
            @Valid @RequestBody OrderItemRequestDto requestDto
    ) {
        OrderItemResponseDto responseDto = orderItemService.create(requestDto);
        return apiResponse(HttpStatus.CREATED, "OrderItem created successfully", responseDto);
    }

    @Operation(
            summary = "Update order item",
            description = "Update an existing order item",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Order item updated successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Order item not found"
                    )
            }
    )
    @PatchMapping("/{orderItemId}")
    public ResponseEntity<ApiResponse<OrderItemResponseDto>> updateOrderItem(
            @Parameter(description = "Order Item ID", example = "101", required = true)
            @PathVariable Long orderItemId,

            @RequestBody OrderItemRequestDto requestDto
    ) {
        OrderItemResponseDto responseDto =
                orderItemService.update(orderItemId, requestDto);

        return apiResponse(HttpStatus.OK, "OrderItem updated successfully", responseDto);
    }

    @Operation(
            summary = "Delete order item",
            description = "Delete an order item by ID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Order item deleted successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Order item not found"
                    )
            }
    )
    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<ApiResponse<OrderItemResponseDto>> deleteOrderItem(
            @Parameter(description = "Order Item ID", example = "101", required = true)
            @PathVariable Long orderItemId
    ) {
        orderItemService.deleteById(orderItemId);
        return apiResponse(HttpStatus.OK, "OrderItem deleted successfully", null);
    }
}
