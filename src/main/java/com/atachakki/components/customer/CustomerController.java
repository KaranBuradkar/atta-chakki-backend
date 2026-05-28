package com.atachakki.components.customer;

import com.atachakki.components.order.OrderResponseDto;
import com.atachakki.components.order.OrderService;
import com.atachakki.components.payment.PaymentResponseDto;
import com.atachakki.components.payment.PaymentService;
import com.atachakki.controller.BaseController;
import com.atachakki.dto.ApiResponse;
import com.atachakki.entity.type.ExportType;
import com.atachakki.services.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@Tag(name = "Customer Module", description = "Operations with customer data")
@RestController
@RequestMapping("/v1/shops/{shopId}/customers")
public class CustomerController extends BaseController {

    public static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ExportService exportService;

    protected CustomerController(
            HttpServletRequest request,
            CustomerService customerService,
            OrderService orderService,
            PaymentService paymentService,
            ExportService exportService
    ) {
        super(request);
        this.customerService = customerService;
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.exportService = exportService;
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseShortDto.class)
                    )
            )
    )
    @Operation(summary = "Get customers", description = "Fetch paginated list of customers for a shop")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CustomerResponseShortDto>>> fetchCustomers(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable(value = "shopId") Long shopId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "dir", defaultValue = "asc") String direction,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(required = false) String name
    ) {
        log.info("Received request: {} at {}",request.getMethod(), request.getRequestURI());
        Page<CustomerResponseShortDto> response = customerService
                .findCustomers(shopId, page, size, direction, sort, name);
        log.info("Complete Request: {} at {}", request.getMethod(), request.getRequestURI());
        return apiResponse(HttpStatus.OK, "Customers fetched successfully", response);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Long.class)
                    )
            )
    )
    @Operation(summary = "Get customerIds", description = "Fetch customerIds details by shopId")
    @GetMapping(value = "/ids")
    public ResponseEntity<ApiResponse<List<Long>>> getCustomerIds(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable(value = "shopId") Long shopId
    ) {
        List<Long> customerIds = customerService.findCustomerIds(shopId);
        return apiResponse(HttpStatus.OK, "", customerIds);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDto.class)
                    )
            )
    )
    @Operation(summary = "Get customer", description = "Fetch customer details by customerId")
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> fetchCustomer(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable(value = "shopId") Long shopId,
            @Parameter(description = "Customer ID", required = true)
            @PathVariable(value = "customerId") Long customerId
    ) {
        CustomerResponseDto responseDto = customerService.findCustomer(shopId, customerId);
        return apiResponse(HttpStatus.OK, "Customer fetched successfully", responseDto);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDto.class)
                    )
            )
    )
    @Operation(summary = "Create customer", description = "Register a new customer for a shop")
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponseDto>> createCustomer(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable("shopId") Long shopId,
            @Valid @RequestBody CustomerRequestDto requestDto
    ) {
        CustomerResponseDto response = customerService.create(shopId, requestDto);
        return apiResponse(HttpStatus.CREATED, "New customer register successfully", response);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDto.class)
                    )
            )
    )
    @Operation(summary = "Create customer", description = "Register a new customer for a shop")
    @PostMapping("/all")
    public ResponseEntity<ApiResponse<List<CustomerResponseDto>>> createAll(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable("shopId") Long shopId,
            @Valid @RequestBody List<CustomerRequestDto> requestDto
    ) {
        List<CustomerResponseDto> response = customerService.createAll(shopId, requestDto);
        return apiResponse(HttpStatus.CREATED, "New customers register successfully", response);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDto.class)
                    )
            )
    )
    @Operation(summary = "Update customer", description = "Update customer details")
    @PatchMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> updateCustomer(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable(value = "shopId") Long shopId,
            @Parameter(description = "Customer ID", required = true)
            @PathVariable(value = "customerId") Long customerId,
            @RequestBody CustomerRequestDto requestDto
    ) {
        CustomerResponseDto responseDto = customerService
                .updateCustomerFields(shopId, customerId, requestDto);
        return apiResponse(HttpStatus.OK, "Customer updated successfully", responseDto);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDto.class)
                    )
            )
    )
    @Operation(summary = "Block or unblock customer", description = "Update customer block status")
    @PatchMapping("/{customerId}/block")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> blockCustomer(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable(value = "shopId") Long shopId,
            @Parameter(description = "Customer ID", required = true)
            @PathVariable(value = "customerId") Long customerId,
            @RequestBody Boolean block
    ) {
        CustomerResponseDto responseDto = customerService
                .updateCustomerBlockStatus(shopId, customerId, block);
        return apiResponse(HttpStatus.OK, block ? "Customer blocked" : "Customer unblocked", responseDto);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200")
    )
    @Operation(summary = "Delete customer", description = "Delete customer by customerId")
    @DeleteMapping("/{customerId}")
    public ResponseEntity<ApiResponse<Void>> deleteById(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable(value = "shopId") Long shopId,
            @Parameter(description = "Customer ID", required = true)
            @PathVariable(value = "customerId") Long customerId
    ) {
        customerService.deleteById(shopId, customerId);
        return apiResponse(HttpStatus.OK, "Customer deleted successfully", null);
    }

    @Operation(summary = "Export customers", description = "Export customers as CSV or PDF")
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportExcel(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable Long shopId,
            @Parameter(description = "Export type", required = true)
            @RequestParam("type") ExportType exportType
    ) {

        Page<CustomerResponseDto> customers = customerService
                .findAllCustomers(shopId, 0, 200, "asc", "name");
        HttpHeaders headers = new HttpHeaders();
        ByteArrayInputStream in = null;
        String fileName = "customers." + exportType.name().toLowerCase();

        if (ExportType.CSV.equals(exportType)) {
            in = exportService.exportCustomersToCsv(customers);
            headers.add("Content-Disposition", "attachment; filename="+fileName);
            headers.add("Content-Type", "text/csv");
        }

        if (ExportType.PDF.equals(exportType)) {
            headers.add("Content-Disposition", "attachment; filename="+fileName);
            headers.add("Content-Type", "application/pdf");
            in = exportService.exportCustomersToPdf(customers);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    @Operation(summary = "Export customer details", description = "Export customer details as CSV or PDF")
    @GetMapping("/{customerId}/export")
    public ResponseEntity<InputStreamResource> exportCustomerDetails(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable Long shopId,
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId,
            @Parameter(description = "Export type", required = true)
            @RequestParam("exportType") ExportType exportType
    ) {

        CustomerResponseDto customer = customerService.findCustomer(shopId, customerId);
        Page<OrderResponseDto> orders = orderService.findOrders(shopId, customerId,
                0, 200, "asc", "orderDate");
        Page<PaymentResponseDto> payments = paymentService.findPayments(shopId, customerId,
                0, 200, "ASC", "paymentDate");


        HttpHeaders headers = new HttpHeaders();
        ByteArrayInputStream in = null;
        String fileName = "customers." + exportType.name().toLowerCase();

        if (ExportType.CSV.equals(exportType)) {
            in = exportService.exportCustomerDetailsToCsv(customer, orders, payments);
            headers.add("Content-Disposition", "attachment; filename="+fileName);
            headers.add("Content-Type", "text/csv");
        }

        if (ExportType.PDF.equals(exportType)) {
            headers.add("Content-Disposition", "attachment; filename="+fileName);
            headers.add("Content-Type", "application/pdf");
            in = exportService.exportCustomerDetailsToPdf(customer, orders, payments);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(new InputStreamResource(in));
    }
}
