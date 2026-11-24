package com.interbank.kwanza.transfer;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interbank.kwanza.transfer.Transfer.Status;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {
    private final TransferStore transfers;
    private final HorizonteClient horizonte;

    TransferController(TransferStore transfers, HorizonteClient horizonte) {
        this.transfers = transfers;
        this.horizonte = horizonte;
    }

    @PostMapping
    public ResponseEntity<Transfer> create(
            @RequestHeader("Idempotency-Key") UUID id, @RequestBody @Valid TransferRequest request) {
        Transfer transfer = transfers.reserve(id, request);
        if (transfer.status() == Status.PENDING) {
            // The reservation commits before the remote call.
            Status outcome = horizonte.send(transfer);
            transfer = outcome == Status.PENDING ? transfers.get(id) : transfers.finish(id, outcome);
        }
        HttpStatus status = transfer.status() == Status.PENDING ? HttpStatus.ACCEPTED : HttpStatus.OK;
        return ResponseEntity.status(status).location(URI.create("/api/transfers/" + id)).body(transfer);
    }

    @GetMapping
    public List<Transfer> list() {
        return transfers.list();
    }

    @GetMapping("/{id}")
    public Transfer get(@PathVariable UUID id) {
        return transfers.get(id);
    }
}
