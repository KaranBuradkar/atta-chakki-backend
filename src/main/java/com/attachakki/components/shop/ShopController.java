package com.attachakki.components.shop;

import com.attachakki.controller.BaseController;
import com.attachakki.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Shop Module", description = "Operations related to shop management")
@RestController
@RequestMapping("/v1/shops")
public class ShopController extends BaseController {

    private final ShopService shopService;
    private final AnalyticsService analyticsService;

    public ShopController(ShopService shopService,
                          HttpServletRequest request, AnalyticsService analyticsService) {
        super(request);
        this.shopService = shopService;
        this.analyticsService = analyticsService;
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Fetch shop analytical details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ShopOverviewResponseDto.class)
                    )
            )
    )
    @Operation(
            summary = "Fetch shop details",
            description = "Fetch shop analytics details by shopId"
    )
    @GetMapping("/{shopId}/overview")
    public ResponseEntity<ApiResponse<ShopOverviewResponseDto>> getOverview(
            @PathVariable Long shopId
    ) {
        ShopOverviewResponseDto response = analyticsService.getShopOverview(shopId);
        return apiResponse(HttpStatus.OK, "Overview Received", response);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Fetched all shops",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ShopShortResponseDto.class)
                    )
            )
    )
    @Operation(
            summary = "Get all shops",
            description = "Fetch paginated list of shops where user has staff role"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ShopShortResponseDto>>> fetchAllShops(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(name = "page", defaultValue = "0") Integer page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(name = "size", defaultValue = "10") Integer size,

            @Parameter(description = "Sort direction", example = "asc")
            @RequestParam(name = "direction", defaultValue = "asc") String direction,

            @Parameter(description = "Sort by field", example = "staffRole")
            @RequestParam(name = "sort", defaultValue = "staffRole") String sort
    ) {
        Page<ShopShortResponseDto> allShops =
                shopService.getAllShops(page, size, direction, sort);
        return apiResponse(HttpStatus.OK, "shops fetched successfully", allShops);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Shop details fetched",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ShopResponseDto.class)
                    )
            )
    )
    @Operation(
            summary = "Get shop details",
            description = "Fetch complete details of a shop by shopId"
    )
    @GetMapping("/{shopId}")
    public ResponseEntity<ApiResponse<ShopResponseDto>> fetchShopDetails(
            @Parameter(description = "ID of the shop", required = true)
            @PathVariable("shopId") Long shopId
    ) {
        ShopResponseDto shopResponse = shopService.getShopDetails(shopId);
        return apiResponse(HttpStatus.OK, "shop details fetched successfully", shopResponse);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Shop created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ShopResponseDto.class)
                    )
            )
    )
    @Operation(
            summary = "Create new shop",
            description = "Create a new shop using provided shop details"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ShopResponseDto>> createShop(
            @RequestBody @Valid ShopRequestDto shopRequestDto
    ) {
        ShopResponseDto newShop = shopService.create(shopRequestDto);
        return apiResponse(HttpStatus.CREATED, "New shop successfully created", newShop);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Shop status updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ShopResponseDto.class)
                    )
            )
    )
    @Operation(
            summary = "Update shop status",
            description = "Update status of an existing shop"
    )
    @PatchMapping("/{shopId}/status")
    public ResponseEntity<ApiResponse<ShopResponseDto>> updateShopStatus(
            @Parameter(description = "ID of the shop", required = true)
            @PathVariable("shopId") Long shopId,

            @Parameter(description = "New shop status", required = true)
            @RequestParam("status") ShopStatus status
    ) {
        ShopResponseDto responseDto =
                shopService.updateShopStatus(shopId, status);
        return apiResponse(HttpStatus.OK, "shop status updated", responseDto);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Shop details updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ShopResponseDto.class)
                    )
            )
    )
    @Operation(
            summary = "Update shop details",
            description = "Update fields of an existing shop"
    )
    @PatchMapping("/{shopId}")
    public ResponseEntity<ApiResponse<ShopResponseDto>> updateShop(
            @Parameter(description = "ID of the shop", required = true)
            @PathVariable("shopId") Long shopId,
            @RequestBody ShopRequestDto requestDto
    ) {
        ShopResponseDto responseDto =
                shopService.updateShopFields(shopId, requestDto);
        return apiResponse(HttpStatus.OK, "shop details updated", responseDto);
    }

    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Shop deleted successfully"
            )
    )
    @Operation(
            summary = "Delete shop",
            description = "Delete shop by shopId"
    )
    @DeleteMapping("/{shopId}")
    public ResponseEntity<ApiResponse<Void>> deleteShop(
            @Parameter(description = "ID of the shop", required = true)
            @PathVariable("shopId") Long shopId
    ) {
        shopService.deleteShop(shopId);
        return apiResponse(HttpStatus.OK, "shop deleted successfully", null);
    }
}
