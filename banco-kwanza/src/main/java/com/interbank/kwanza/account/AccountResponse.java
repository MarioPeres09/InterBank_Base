package com.interbank.kwanza.account;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNumber,
        BigDecimal balance,
        BigDecimal availableBalance,
        AccountStatus status
) {
    static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getAvailableBalance(),
                account.getStatus()
        );
    }
}
