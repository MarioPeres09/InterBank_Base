package com.interbank.kwanza.transfer;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.interbank.kwanza.transfer.Transfer.Status;

@Component
class HorizonteClient {
    private static final Logger log = LoggerFactory.getLogger(HorizonteClient.class);
    private final RestClient client;

    HorizonteClient(@Value("${interbank.horizonte.base-url}") String baseUrl) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(3));
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    Status send(Transfer transfer) {
        try {
            var request = new CreditRequest(transfer.id(), transfer.destinationAccount(), transfer.amount());
            CreditReceipt receipt = client.post().uri("/api/interbank/credits")
                    .contentType(MediaType.APPLICATION_JSON).body(request)
                    .retrieve().body(CreditReceipt.class);
            if (receipt != null && transfer.id().equals(receipt.id())
                    && transfer.destinationAccount().equals(receipt.destinationAccount())
                    && transfer.currency().equals(receipt.currency())
                    && receipt.amount() != null && transfer.amount().compareTo(receipt.amount()) == 0) {
                if ("CREDITED".equals(receipt.status())) {
                    return Status.COMPLETED;
                }
                if ("REJECTED".equals(receipt.status())) {
                    return Status.FAILED;
                }
            }
            log.warn("Recibo inválido do Horizonte para a transferência {}", transfer.id());
        } catch (RestClientException exception) {
            log.warn("Resultado do Horizonte incerto para {} ({})",
                    transfer.id(), exception.getClass().getSimpleName());
        }
        return Status.PENDING;
    }

    private record CreditRequest(UUID id, String destinationAccount, BigDecimal amount) {}

    private record CreditReceipt(UUID id, String destinationAccount, BigDecimal amount,
                                 String currency, String status) {}
}
