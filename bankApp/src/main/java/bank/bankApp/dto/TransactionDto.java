package bank.bankApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionDto {

    private Long transactionId;

    private Long orderId;

    private String transaction_status;

    private String transaction_type;

}
