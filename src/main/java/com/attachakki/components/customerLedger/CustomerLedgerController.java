package com.attachakki.components.customerLedger;

import com.attachakki.controller.BaseController;
import com.attachakki.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/shops/{shopId}/ledger")
public class CustomerLedgerController extends BaseController {

    private final CustomerLedgerService customerLedgerService;


    public CustomerLedgerController(
            HttpServletRequest request,
            CustomerLedgerService customerLedgerService
    ) {
        super(request);
        this.customerLedgerService = customerLedgerService;
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<ApiResponse<CustomerLedgerResponseDto>> getCustomerLedger(
            @PathVariable Long shopId,
            @PathVariable Long customerId
    ) {
        CustomerLedgerResponseDto customerLedger = customerLedgerService.findCustomerLedger(shopId, customerId);
        return apiResponse(HttpStatus.OK, "CustomerLedger fetched successfully", customerLedger);
    }
}
