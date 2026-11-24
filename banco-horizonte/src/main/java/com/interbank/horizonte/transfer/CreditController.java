package com.interbank.horizonte.transfer;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/interbank/credits")
public class CreditController {
    private final CreditService credits;

    CreditController(CreditService credits) {
        this.credits = credits;
    }

    @PostMapping
    public ReceivedTransfer receive(@Valid @RequestBody CreditRequest request) {
        return credits.receive(request);
    }

    @GetMapping("/{id}")
    public ReceivedTransfer get(@PathVariable UUID id) {
        return credits.get(id);
    }
}
