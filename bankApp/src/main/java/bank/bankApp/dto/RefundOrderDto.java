package bank.bankApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefundOrderDto {

    private Long id;

    private Long orderId;

    private String amount;

    private String total;

    private String tranType = "CompayPayment";

    private String refundBankUrl;

    private String response;

    private String responseCode;
}
