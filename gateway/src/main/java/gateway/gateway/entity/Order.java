package gateway.gateway.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private String clientId;

    private String acquirerId;

    private String amount;

    private String currency;

    private String shopUrl;

    private String softDescriptor;

    private String okUrl;

    private String failUrl;

    private String tranType = "CompayPayment";

    private String rnd ;

    private String hash;

    private String hashAlgorithm = "ver3";

    private String response = "";

    private String responseCode = "";

    private String bankUrl;

    private final String storeKey = "TRSP0001";

}
