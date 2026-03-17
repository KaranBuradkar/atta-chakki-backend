package com.atachakki.components.order;

import com.atachakki.components.customer.CustomerResponseDto;
import com.atachakki.components.customer.CustomerService;
import com.atachakki.controller.BaseController;
import com.atachakki.dto.ApiResponse;
import com.atachakki.entity.type.ExportType;
import com.atachakki.services.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;

@Tag(
        name = "Order Module",
        description = "APIs for managing customer orders, totals, and exports"
)
@RestController
@RequestMapping("/v1/shops/{shopId}/orders")
public class OrderController extends BaseController {

    private final OrderService orderService;
    private final ExportService exportService;
    private final CustomerService customerService;

    protected OrderController(
            HttpServletRequest request,
            OrderService orderService,
            ExportService exportService,
            CustomerService customerService
    ) {
        super(request);
        this.orderService = orderService;
        this.exportService = exportService;
        this.customerService = customerService;
    }

    @Operation(
            summary = "Fetch customer orders",
            description = "Retrieve paginated order list for a specific customer",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "302", description = "Orders fetched successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer or Shop not found")
            }
    )
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<ApiResponse<Page<OrderResponseDto>>> fetchOrders(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Customer ID", example = "10", required = true)
            @PathVariable Long customerId,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") Integer size,

            @Parameter(description = "Sort direction (asc / desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String direction,

            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sort
    ) {
        Page<OrderResponseDto> orderPage =
                orderService.findOrders(shopId, customerId, page, size, direction, sort);

        return apiResponse(HttpStatus.OK, "Orders fetched successfully", orderPage);
    }

    @Operation(
            summary = "Get total outstanding amount",
            description = "Returns total pending debt for a shop",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Total debt fetched",
                            content = @Content(schema = @Schema(implementation = BigDecimal.class))
                    )
            }
    )
    @GetMapping("/customers/{customerId}/balance")
    public ResponseEntity<ApiResponse<String>> getTotal(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,
            @PathVariable Long customerId
    ) {
        String totalBalance = orderService.findTotalCustomerBalance(shopId, customerId);
        return apiResponse(HttpStatus.OK, "Total Balance of customer fetched", totalBalance);
    }

    @Operation(
            summary = "Get total outstanding amount",
            description = "Returns total pending debt for a shop",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Total debt fetched",
                            content = @Content(schema = @Schema(implementation = BigDecimal.class))
                    )
            }
    )
    @GetMapping("/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotal(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId
    ) {
        BigDecimal totalDebt = orderService.findTotalDebt(shopId);
        return apiResponse(HttpStatus.OK, "Total Debt fetched", totalDebt);
    }

    @Operation(
            summary = "Export customer orders",
            description = "Export customer orders as CSV or other supported formats",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Export generated successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid export type")
            }
    )
    @GetMapping("/customers/{customerId}/export")
    public ResponseEntity<InputStreamResource> exportExcel(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Customer ID", example = "10", required = true)
            @PathVariable Long customerId,

            @Parameter(
                    description = "Export format",
                    schema = @Schema(implementation = ExportType.class),
                    example = "CSV",
                    required = true
            )
            @RequestParam("type") ExportType exportType
    ) throws IOException {

        Page<OrderResponseDto> orders =
                orderService.findOrders(shopId, customerId, 0, 100, "asc", "createdAt");

        CustomerResponseDto customer =
                customerService.findCustomer(shopId, customerId);

        HttpHeaders headers = new HttpHeaders();
        ByteArrayInputStream in = null;

        if (ExportType.CSV.equals(exportType)) {
            in = exportService.exportOrdersToCsv(customer, orders);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders.csv");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    @Operation(
            summary = "Create new order",
            description = "Create a new order for a customer",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Order created successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body")
            }
    )
    @PostMapping("/customers/{customerId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Customer ID", example = "10", required = true)
            @PathVariable Long customerId,

            @Valid @RequestBody OrderRequestDto requestDto
    ) {
        OrderResponseDto responseDto =
                orderService.createOrder(shopId, customerId, requestDto);

        return apiResponse(HttpStatus.CREATED, "Order created successfully", responseDto);
    }

    @Operation(
            summary = "Update order",
            description = "Update specific fields of an existing order",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order updated successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    @PatchMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> updateOrder(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Order ID", example = "100", required = true)
            @PathVariable Long orderId,

            @RequestBody OrderRequestDto requestDto
    ) {
        OrderResponseDto responseDto =
                orderService.updateOrderFields(shopId, orderId, requestDto);

        return apiResponse(HttpStatus.OK, "Order updated successfully", responseDto);
    }

    @Operation(
            summary = "Delete order",
            description = "Delete an order by ID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order deleted successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> deleteOrder(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Order ID", example = "100", required = true)
            @PathVariable Long orderId
    ) {
        orderService.deleteOrder(shopId, orderId);
        return apiResponse(HttpStatus.OK, "Order deleted successfully", null);
    }
}
