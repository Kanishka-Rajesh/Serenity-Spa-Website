package com.serenityspa.controller;

import com.serenityspa.dto.ContactRequest;
import com.serenityspa.dto.ContactResponse;
import com.serenityspa.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Contact", description = "Endpoints for submitting guest contact enquiries")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/contact")
    @Operation(summary = "Submit a contact enquiry (Public)")
    public ResponseEntity<ContactResponse> submitContactEnquiry(@Valid @RequestBody ContactRequest request) {
        ContactResponse response = contactService.saveEnquiry(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
