package com.attachakki.components.personalization;

import com.attachakki.controller.BaseController;
import com.attachakki.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "User Personalization Module",
        description = "APIs for managing user personalization preferences"
)
@RestController
@RequestMapping("/v1/personalizations/users")
public class PersonalizationController extends BaseController {

    private final PersonalizationService personalizationService;

    protected PersonalizationController(
            HttpServletRequest request,
            PersonalizationService personalizationService
    ) {
        super(request);
        this.personalizationService = personalizationService;
    }

    @Operation(
            summary = "Fetch user personalization",
            description = "Retrieve personalization settings for a specific user",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Personalization fetched successfully",
                            content = @Content(
                                    schema = @Schema(implementation = PersonalizationResponseDto.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Personalization not found"
                    )
            }
    )
    @GetMapping("/{userDetailId}")
    public ResponseEntity<ApiResponse<PersonalizationResponseDto>> fetchPersonalization(
            @Parameter(
                    description = "User Detail ID",
                    example = "1001",
                    required = true
            )
            @PathVariable Long userDetailId
    ) {
        PersonalizationResponseDto responseDto =
                personalizationService.findPersonalization(userDetailId);

        return apiResponse(HttpStatus.OK, "Personalization fetched successfully", responseDto);
    }

    @Operation(
            summary = "Update user personalization",
            description = "Update personalization preferences for a user",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Personalization updated successfully"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid personalization data"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Personalization not found"
                    )
            }
    )
    @PatchMapping("/{userDetailId}")
    public ResponseEntity<ApiResponse<PersonalizationResponseDto>> updatePersonalization(
            @Parameter(
                    description = "User Detail ID",
                    example = "1001",
                    required = true
            )
            @PathVariable Long userDetailId,

            @RequestBody PersonalizationRequestDto requestDto
    ) {
        PersonalizationResponseDto responseDto =
                personalizationService.update(userDetailId, requestDto);

        return apiResponse(HttpStatus.OK, "Personalization updated successfully", responseDto);
    }
}
