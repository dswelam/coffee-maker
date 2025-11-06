package edu.ncsu.csc326.wolfcafe.mapper;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.entity.Order;

/**
 * Converts between Order entity and OrderDto
 *
 * @author Brooke Wu (bwu25)
 */
public class OrderMapper {

    /**
     * Converts an Order entity to OrderDto
     *
     * @param order
     *            Order to convert
     * @return OrderDto object
     */
    public static OrderDto mapToOrderDto ( final Order order ) {
        return new OrderDto( order.getId(), order.getCustomer(), order.getOrderItems(), order.getStatus(), order.getPreparedBy() );

    }

    /**
     * Converts a OrderDto object to a Order entity.
     *
     * @param orderDto
     *            OrderDto to convert
     * @return Order entity
     */
    public static Order mapToOrder ( final OrderDto orderDto ) {
        return new Order( orderDto.getId(), orderDto.getCustomer(), orderDto.getOrderItems(), orderDto.getStatus(), orderDto.getPreparedBy() );
    }

}
