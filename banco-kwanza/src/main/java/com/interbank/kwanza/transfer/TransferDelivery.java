package com.interbank.kwanza.transfer;

import org.springframework.stereotype.Service;

import com.interbank.kwanza.transfer.Transfer.Status;

@Service
class TransferDelivery {
    private final TransferStore transfers;
    private final HorizonteClient horizonte;

    TransferDelivery(TransferStore transfers, HorizonteClient horizonte) {
        this.transfers = transfers;
        this.horizonte = horizonte;
    }

    Transfer deliver(Transfer transfer) {
        if (transfer.status() != Status.PENDING) {
            return transfer;
        }
        // The reservation is already committed. Never hold a database transaction during HTTP.
        Status outcome = horizonte.send(transfer);
        return outcome == Status.PENDING ? transfers.get(transfer.id()) : transfers.finish(transfer.id(), outcome);
    }
}
