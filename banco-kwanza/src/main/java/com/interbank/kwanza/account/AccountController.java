package com.interbank.kwanza.account;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountRepository accounts;

    public AccountController(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @GetMapping
    public List<AccountResponse> list() {
        return accounts.findAll(Sort.by("id")).stream()
                .map(AccountResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> get(@PathVariable Long id) {
        return ResponseEntity.of(accounts.findById(id).map(AccountResponse::from));
    }
}
