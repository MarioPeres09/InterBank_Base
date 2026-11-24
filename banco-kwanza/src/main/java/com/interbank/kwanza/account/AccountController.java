package com.interbank.kwanza.account;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/accounts")
@SecurityRequirement(name = "cliente")
public class AccountController {
    private final AccountRepository accounts;

    public AccountController(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @GetMapping
    public List<AccountResponse> list(JwtAuthenticationToken authentication) {
        return accounts.findByOwnerIdOrderByIdAsc(Long.parseLong(authentication.getName())).stream()
                .map(AccountResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> get(@PathVariable Long id, JwtAuthenticationToken authentication) {
        return ResponseEntity.of(accounts.findByIdAndOwnerId(id, Long.parseLong(authentication.getName()))
                .map(AccountResponse::from));
    }
}
