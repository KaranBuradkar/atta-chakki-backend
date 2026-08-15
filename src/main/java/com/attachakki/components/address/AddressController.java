package com.attachakki.components.address;

import com.attachakki.controller.BaseController;
import com.attachakki.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Tag(name = "Address Module", description = "Operations with shop's address details")
@RestController
@RequestMapping("/address")
public class AddressController extends BaseController {

    private final AddressService addressService;

    public AddressController(AddressService addressService, HttpServletRequest request) {
        super(request);
        this.addressService = addressService;
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = AddressDto.class))}
            )
    )
    @Operation(summary = "Get all addresses of all shops", description = "Here provide address details of many shops")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressDto>>> fetchAddresses (
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "by", defaultValue = "asc") String by,
            @RequestParam(name = "sort", defaultValue = "id") String... sort) {
        List<AddressDto> address = addressService.getAddressPage(page, size, sort, by);
        return apiResponse(HttpStatus.OK, "fetched all address", address);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = AddressDto.class))}
            )
    )
    @Operation(summary = "Get single address by provided addressId", description = "Here provide details of address")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressDto>> fetchAddress(@PathVariable("id") Long addressId) {
        AddressDto resAddress = addressService.getAddress(addressId);
        return apiResponse(HttpStatus.OK, "address fetched successfully", resAddress);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = AddressDto.class))}
            )
    )
    @Operation(summary = "create new address",
            description = "Create new address by provided address details")
    @PostMapping
    public ResponseEntity<ApiResponse<AddressDto>> addAddress(@Valid AddressDto addressDto) {
        AddressDto resAddress = addressService.createAddress(addressDto);
        return apiResponse(HttpStatus.CREATED, "address added successfully", resAddress);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = AddressDto.class))}
            )
    )
    @Operation(summary = "update address fields",
            description = "Update address by provided new address details")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressDto>> fetchAddress(
            @PathVariable("id") Long addressId,
            @RequestBody Map<String, Objects> map
            ) {
        AddressDto resAddress = addressService.updateAddress(addressId, map);
        return apiResponse(HttpStatus.OK, "address updated successfully", resAddress);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = AddressDto.class))}
            )
    )
    @Operation(summary = "delete existing address",
            description = "Delete address by provided addressId")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressDto>> deleteAddress(@PathVariable("id") Long addressId) {
        addressService.deleteAddress(addressId);
        return apiResponse(HttpStatus.OK, "address deleted successfully", null);
    }

}
