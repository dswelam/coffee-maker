package edu.ncsu.csc326.wolfcafe.dto;

import java.util.Map;

import edu.ncsu.csc326.wolfcafe.entity.Item;
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
    private Long   id;

    /** Order customer */
    private User customer;
    
    /** Order items */
    private Map<Item, Integer> orderItems;
    
    /** Order status */
    private OrderStatus status;
    
    /** Order staff */
    private User preparedBy;

    /**
     * Default constructor for the OrderDto
     */
    public OrderDto () {

    }

    /** Constructor that takes all properties for an OrderDto, including id */
	public OrderDto(Long id, User customer, Map<Item, Integer> orderItems, OrderStatus status, User preparedBy) {
		super();
		this.id = id;
		this.customer = customer;
		this.orderItems = orderItems;
		this.status = status;
		this.preparedBy = preparedBy;
	}

	/** Constructor that takes all properties for an OrderDto EXCEPT for id */
	public OrderDto(User customer, Map<Item, Integer> orderItems, OrderStatus status, User preparedBy) {
		super();
		this.customer = customer;
		this.orderItems = orderItems;
		this.status = status;
		this.preparedBy = preparedBy;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getCustomer() {
		return customer;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}

	public Map<Item, Integer> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(Map<Item, Integer> orderItems) {
		this.orderItems = orderItems;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public User getPreparedBy() {
		return preparedBy;
	}

	public void setPreparedBy(User preparedBy) {
		this.preparedBy = preparedBy;
	}

}
