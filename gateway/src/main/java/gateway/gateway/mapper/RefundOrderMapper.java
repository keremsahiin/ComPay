package gateway.gateway.mapper;

import gateway.gateway.dto.RefundOrderDto;
import gateway.gateway.entity.RefundOrder;

public class RefundOrderMapper {

    public static RefundOrder mapToRefundOrder(RefundOrderDto refundOrderDto){
        RefundOrder refundOrder = new RefundOrder(
                refundOrderDto.getRefundId(),
                refundOrderDto.getOrderId(),
                refundOrderDto.getAmount(),
                refundOrderDto.getTotal(),
                refundOrderDto.getTranType(),
                refundOrderDto.getRefundBankUrl(),
                refundOrderDto.getResponse(),
                refundOrderDto.getResponseCode()
        );
        return refundOrder;
    }

    public static RefundOrderDto mapToRefundOrderDto(RefundOrder refundOrder){
        RefundOrderDto refundOrderDto = new RefundOrderDto(
                refundOrder.getRefundId(),
                refundOrder.getOrderId(),
                refundOrder.getAmount(),
                refundOrder.getTotal(),
                refundOrder.getTranType(),
                refundOrder.getRefundBankUrl(),
                refundOrder.getResponse(),
                refundOrder.getResponseCode()
        );
        return refundOrderDto;
    }
}
