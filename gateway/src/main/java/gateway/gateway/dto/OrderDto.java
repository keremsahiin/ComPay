package gateway.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderDto {

    private Long orderId;

    private String clientId;

    private String acquirerId;

    private String amount;

    private String currency;

    private String shopUrl;

    private String softDescriptor;

    private String okUrl;

    private String failUrl;

    private String tranType;

    private String rnd ;

    private String hash;

    private String hashAlgorithm;

    private String response;

    private String responseCode;

    private String bankUrl;

    private final String storeKey = "TRSP0001";


}
