package bank.bankApp.mapper;


import bank.bankApp.dto.OrderDto;
import bank.bankApp.entity.Order;

public class OrderMapper {

    public static Order mapToOrder(OrderDto orderDto){
        Order order = new Order(

                orderDto.getId(),
                orderDto.getOrderId(),
                orderDto.getClientId(),
                orderDto.getAmount(),
                orderDto.getBankUrl(),
                orderDto.getResponse(),
                orderDto.getResponseCode()
        );
        return order;
    }

    public static OrderDto mapToOrderDto(Order order){
        OrderDto orderDto = new OrderDto(

                order.getId(),
                order.getOrderId(),
                order.getClientId(),
                order.getAmount(),
                order.getBankUrl(),
                order.getResponse(),
                order.getResponseCode()
        );
        return orderDto;
    }
}
