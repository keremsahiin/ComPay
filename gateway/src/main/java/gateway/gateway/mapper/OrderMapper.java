package gateway.gateway.mapper;

import gateway.gateway.dto.OrderDto;
import gateway.gateway.entity.Order;

public class OrderMapper {
        public static Order mapToOrder(OrderDto orderDto){
            Order order = new Order (
                    orderDto.getOrderId(),
                    orderDto.getClientId(),
                    orderDto.getAcquirerId(),
                    orderDto.getAmount(),
                    orderDto.getCurrency(),
                    orderDto.getShopUrl(),
                    orderDto.getSoftDescriptor(),
                    orderDto.getOkUrl(),
                    orderDto.getFailUrl(),
                    orderDto.getTranType(),
                    orderDto.getRnd(),
                    orderDto.getHash(),
                    orderDto.getHashAlgorithm(),
                    orderDto.getResponse(),
                    orderDto.getResponseCode(),
                    orderDto.getBankUrl()
        );
        return order;
    }

    public static OrderDto mapToOrderDto(Order order){
        OrderDto orderDto = new OrderDto (
                order.getOrderId(),
                order.getClientId(),
                order.getAcquirerId(),
                order.getAmount(),
                order.getCurrency(),
                order.getShopUrl(),
                order.getSoftDescriptor(),
                order.getOkUrl(),
                order.getFailUrl(),
                order.getTranType(),
                order.getRnd(),
                order.getHash(),
                order.getHashAlgorithm(),
                order.getResponse(),
                order.getResponseCode(),
                order.getBankUrl()
        );
        return orderDto;
    }
}
