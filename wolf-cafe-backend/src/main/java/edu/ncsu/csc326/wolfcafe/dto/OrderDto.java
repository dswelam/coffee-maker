package edu.ncsu.csc326.wolfcafe.dto;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.entity.Order.OrderStatus;
import edu.ncsu.csc326.wolfcafe.entity.User;

/**
 * Used to transfer Order data between the client and server. This class
 * will serve as the response in the REST API.
 *
 * @author Brooke Wu (bwu25)
 */
public class OrderDto {

    /** Order id */
    private Long               id;

    /** Order customer */
    private User               customer;

    /** Order items */
    private List<OrderLineDto> orderItems;

    /** Order status */
    private OrderStatus        status;

    /** Order staff */
    private User               preparedBy;

    /**
     * Default constructor for the OrderDto
     */
    public OrderDto () {

    }

    /** Constructor that takes all properties for an OrderDto, including id */
    public OrderDto ( final Long id, final User customer, final List<OrderLineDto> orderItems, final OrderStatus status,
            final User preparedBy ) {
        super();
        this.id = id;
        this.customer = customer;
        this.orderItems = orderItems;
        this.status = status;
        this.preparedBy = preparedBy;
    }

    /** Constructor that takes all properties for an OrderDto EXCEPT for id */
    public OrderDto ( final User customer, final List<OrderLineDto> orderItems, final OrderStatus status,
            final User preparedBy ) {
        super();
        this.customer = customer;
        this.orderItems = orderItems;
        this.status = status;
        this.preparedBy = preparedBy;
    }

    public Long getId () {
        return id;
    }

    public void setId ( final Long id ) {
        this.id = id;
    }

    public User getCustomer () {
        return customer;
    }

    public void setCustomer ( final User customer ) {
        this.customer = customer;
    }

    public List<OrderLineDto> getOrderItems () {
        return orderItems;
    }

    public void setOrderItems ( final List<OrderLineDto> orderItems ) {
        this.orderItems = orderItems;
    }

    public OrderStatus getStatus () {
        return status;
    }

    public void setStatus ( final OrderStatus status ) {
        this.status = status;
    }

    public User getPreparedBy () {
        return preparedBy;
    }

    public void setPreparedBy ( final User preparedBy ) {
        this.preparedBy = preparedBy;
    }

}
