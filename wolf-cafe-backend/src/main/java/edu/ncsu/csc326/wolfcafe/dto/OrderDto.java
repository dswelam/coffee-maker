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

    /**
     * Constructor that takes all properties for an OrderDto, including id
     * @param id the order id
     * @param customer the order customer
     * @param orderItems the order items
     * @param status the order status
     * @param preparedBy the staff who prepared the order
     */
    public OrderDto ( final Long id, final User customer, final List<OrderLineDto> orderItems, final OrderStatus status,
            final User preparedBy ) {
        super();
        this.id = id;
        this.customer = customer;
        this.orderItems = orderItems;
        this.status = status;
        this.preparedBy = preparedBy;
    }

    /**
     * Constructor that takes all properties for an OrderDto EXCEPT for id
     * @param customer the order customer
     * @param orderItems the order items
     * @param status the order status
     * @param preparedBy the staff who prepared the order
     */
    public OrderDto ( final User customer, final List<OrderLineDto> orderItems, final OrderStatus status,
            final User preparedBy ) {
        super();
        this.customer = customer;
        this.orderItems = orderItems;
        this.status = status;
        this.preparedBy = preparedBy;
    }

    /**
     * Gets the id of the order
     * @return the order id
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the id of the order
     * @param id the order id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the customer who placed the order
     * @return the order customer
     */
    public User getCustomer () {
        return customer;
    }

    /**
     * Sets the customer who placed the order
     * @param customer the order customer
     */
    public void setCustomer ( final User customer ) {
        this.customer = customer;
    }

    /**
     * Gets the items in the order
     * @return the order items
     */
    public List<OrderLineDto> getOrderItems () {
        return orderItems;
    }

    /**
     * Sets the items in the order
     * @param orderItems the order items
     */
    public void setOrderItems ( final List<OrderLineDto> orderItems ) {
        this.orderItems = orderItems;
    }

    /**
     * Gets the status of the order
     * @return the order status
     */
    public OrderStatus getStatus () {
        return status;
    }

    /**
     * Sets the status of the order
     * @param status the order status
     */
    public void setStatus ( final OrderStatus status ) {
        this.status = status;
    }

    /**
     * Gets the staff who prepared the order
     * @return the staff who prepared the order
     */
    public User getPreparedBy () {
        return preparedBy;
    }

    /**
     * Sets the staff who prepared the order
     * @param preparedBy the staff who prepared the order
     */
    public void setPreparedBy ( final User preparedBy ) {
        this.preparedBy = preparedBy;
    }

}
