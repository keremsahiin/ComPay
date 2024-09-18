package gateway.gateway.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "refund_orders")
public class RefundOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refundId;

    private Long orderId;

    private String amount;

    private String total;

    private String tranType = "CompayPayment";

    private String refundBankUrl;

    private String response = "";

    private String responseCode = "";
}
