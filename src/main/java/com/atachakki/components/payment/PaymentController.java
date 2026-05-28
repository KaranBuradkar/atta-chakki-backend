package com.atachakki.components.payment;

import com.atachakki.controller.BaseController;
import com.atachakki.dto.ApiResponse;
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
        name = "Payment Module",
        description = "APIs for managing customer payments"
)
@RestController
@RequestMapping("/v1/shops/{shopId}/payments")
public class PaymentController extends BaseController {

    private final PaymentService paymentService;

    protected PaymentController(
            HttpServletRequest request,
            PaymentService paymentService
    ) {
        super(request);
        this.paymentService = paymentService;
    }

    @Operation(
            summary = "Fetch customer payments",
            description = "Retrieve paginated payment history for a customer",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "302",
                            description = "Payments fetched successfully",
                            content = @Content(
                                    schema = @Schema(implementation = PaymentResponseDto.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Customer or Shop not found"
                    )
            }
    )
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<ApiResponse<Page<PaymentResponseDto>>> fetchPayments(
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
        Page<PaymentResponseDto> responseDtoPage =
                paymentService.findPayments(shopId, customerId, page, size, direction, sort);

        return apiResponse(HttpStatus.OK, "Payments fetched successfully", responseDtoPage);
    }

    @Operation(
            summary = "Fetch payment details",
            description = "Retrieve details of a single payment by ID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Payment fetched successfully",
                            content = @Content(
                                    schema = @Schema(implementation = PaymentResponseDto.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Payment not found"
                    )
            }
    )
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> fetchPayment(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Payment ID", example = "200", required = true)
            @PathVariable Long paymentId
    ) {
        PaymentResponseDto responseDto =
                paymentService.findPayment(shopId, paymentId);

        return apiResponse(HttpStatus.OK, "Payment fetched successfully", responseDto);
    }

    @Operation(
            summary = "Fetch paymentIds",
            description = "Retrieve Ids of a single customer",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Payment Ids fetched successfully",
                            content = @Content(
                                    schema = @Schema(implementation = PaymentResponseDto.class)
                            )
                    )
            }
    )
    @GetMapping("/customers/{customerId}/ids")
    public ResponseEntity<ApiResponse<List<Long>>> fetchPaymentIds(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Customer ID", example = "5", required = true)
            @PathVariable Long customerId
    ) {
        List<Long> paymentIds = paymentService.findPaymentIds(shopId, customerId);
        return apiResponse(HttpStatus.OK, "PaymentIds fetched successfully", paymentIds);
    }

    @Operation(
            summary = "Create payment",
            description = "Create a new payment for a customer",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "Payment created successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body"
                    )
            }
    )
    @PostMapping("/customers/{customerId}")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> createPayment(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Customer ID", example = "10", required = true)
            @PathVariable Long customerId,

            @Valid @RequestBody PaymentBundleRequestDto requestDto
    ) {
        PaymentResponseDto responseDto =
                paymentService.payOrders(shopId, customerId, requestDto);

        return apiResponse(HttpStatus.CREATED, "Payment successful", responseDto);
    }

    @Operation(
            summary = "Create payment",
            description = "Create a new payment by amount for a customer",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "Payment created successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body"
                    )
            }
    )
    @PostMapping("/customers/{customerId}/by-amount")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> createPayment(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Customer ID", example = "10", required = true)
            @PathVariable Long customerId,

            @Valid @RequestBody PaymentRequestDto requestDto
    ) {
        PaymentResponseDto responseDto =
                paymentService.createByAmount(shopId, customerId, requestDto);
        return apiResponse(HttpStatus.CREATED, "Payment successful", responseDto);
    }

    @Operation(
            summary = "Update payment",
            description = "Update an existing payment",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Payment updated successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Payment not found"
                    )
            }
    )
    @PatchMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> updatePayment(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Payment ID", example = "200", required = true)
            @PathVariable Long paymentId,

            @RequestBody PaymentRequestDto updateRequest
    ) {
        PaymentResponseDto responseDto =
                paymentService.update(shopId, paymentId, updateRequest);

        return apiResponse(HttpStatus.OK, "Payment updated successfully", responseDto);
    }

    @Operation(
            summary = "Delete payment",
            description = "Delete a payment transaction",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Payment deleted successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Payment not found"
                    )
            }
    )
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<Void>> deletePayment(
            @Parameter(description = "Shop ID", example = "1", required = true)
            @PathVariable Long shopId,

            @Parameter(description = "Payment ID", example = "200", required = true)
            @PathVariable Long paymentId
    ) {
        paymentService.deleteById(shopId, paymentId);
        return apiResponse(HttpStatus.OK, "Payment transaction deleted successfully", null);
    }
}
