package api.models.requests;

import api.models.dto.CreateTransferModelRequest;

public class TransferRequests {
    public static CreateTransferModelRequest createTransferBody(double amount,int senderAccId, int receiverAccId){
        return CreateTransferModelRequest
                .builder()
                .amount(amount)
                .senderAccountId(senderAccId)
                .receiverAccountId(receiverAccId)
                .build();
    }
}
