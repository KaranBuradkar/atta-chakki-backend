package com.attachakki.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    @Operation(
            summary = "Welcome To Atta Chakki Application",
            description = "First(or Default) page of application"
    )
    @GetMapping
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("Welcome To Ata Chakki");
    }
}
