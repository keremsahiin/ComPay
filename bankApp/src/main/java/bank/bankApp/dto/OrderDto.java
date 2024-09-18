package bank.bankApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderDto {

    private Long id;

    private Long orderId;

    private String clientId;

    private String amount;

    private String bankUrl;

    private String response;

    private String responseCode;

}
