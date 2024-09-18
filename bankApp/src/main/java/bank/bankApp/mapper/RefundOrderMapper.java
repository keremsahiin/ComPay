package bank.bankApp.mapper;

import bank.bankApp.dto.RefundOrderDto;
import bank.bankApp.entity.RefundOrder;

public class RefundOrderMapper {

    public static RefundOrder maptoRefundOrder(RefundOrderDto refundOrderDto) {
        RefundOrder refundOrder = new RefundOrder(
                refundOrderDto.getId(),
                refundOrderDto.getOrderId(),
                refundOrderDto.getAmount(),
                refundOrderDto.getTranType(),
                refundOrderDto.getTotal(),
                refundOrderDto.getRefundBankUrl(),
                refundOrderDto.getResponse(),
                refundOrderDto.getResponseCode()
        );
        return refundOrder;
    }

    public static RefundOrderDto mapToRefundOrderDto(RefundOrder refundOrder) {
        RefundOrderDto refundOrderDto = new RefundOrderDto(
                refundOrder.getId(),
                refundOrder.getOrderId(),
                refundOrder.getAmount(),
                refundOrder.getTranType(),
                refundOrder.getTotal(),
                refundOrder.getRefundBankUrl(),
                refundOrder.getResponse(),
                refundOrder.getResponseCode()
        );
          return refundOrderDto;
    }
}
