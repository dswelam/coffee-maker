package edu.ncsu.csc326.wolfcafe.mapper;

import java.util.List;
import java.util.stream.Collectors;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderLineDto;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.OrderLine;

/**
 * Converts between Order entity and OrderDto
 */
public class OrderMapper {

    public static OrderDto mapToOrderDto ( final Order order ) {
        final List<OrderLineDto> orderLineDtos = order.getOrderItems().stream().map( OrderMapper::mapToOrderLineDto )
                .collect( Collectors.toList() );
        return new OrderDto( order.getId(), order.getCustomer(), orderLineDtos, order.getStatus(),
                order.getPreparedBy() );
    }

    public static Order mapToOrder ( final OrderDto orderDto ) {
        final List<OrderLine> orderLines = orderDto.getOrderItems().stream().map( OrderMapper::mapToOrderLine )
                .collect( Collectors.toList() );
        return new Order( orderDto.getId(), orderDto.getCustomer(), orderLines, orderDto.getStatus(),
                orderDto.getPreparedBy() );
    }

    private static OrderLineDto mapToOrderLineDto ( final OrderLine orderLine ) {
        return new OrderLineDto( orderLine.getId(), orderLine.getItem(), orderLine.getQuantity() );
    }

    private static OrderLine mapToOrderLine ( final OrderLineDto orderLineDto ) {
        final OrderLine orderLine = new OrderLine();
        orderLine.setId( orderLineDto.getId() );
        orderLine.setItem( orderLineDto.getItem() );
        orderLine.setQuantity( orderLineDto.getQuantity() );
        return orderLine;
    }
}
