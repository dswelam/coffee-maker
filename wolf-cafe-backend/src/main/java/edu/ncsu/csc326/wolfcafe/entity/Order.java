package edu.ncsu.csc326.wolfcafe.entity;

import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Order for the cafe. Order is a Data Access Object (DAO) is
 * tied to the database using Hibernate libraries. OrderRepository provides
 * the methods for database CRUD operations.
 *
 * @author Brooke Wu (bwu25)
 */
@Entity
@Table ( name = "orders" )
public class Order {

    /** Order id */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long   id;

    /** Order customer */
    private User customer;
    
    /** Order items
     * 	Key = Item
     * 	Value = Integer (quantity of Item) */
    private Map<Item, Integer> orderItems;
    
    /** Order status */
    private OrderStatus status;
    
    /** Order staff */
    private User preparedBy;
    
    /** 
     * Defines all order statuses in the system
     */
    public enum OrderStatus {
    		PLACED,
    		IN_PROGRESS,
    		READY,
    		FULFILLED,
    		CANCELLED
    }
    
    /**
     * Default constructor for Hibernate
     */
    public Order () {
        super();
    }

    /** Constructor that takes all properties for an Order, including id */
	public Order(Long id, User customer, Map<Item, Integer> orderItems, OrderStatus status, User preparedBy) {
		super();
		this.id = id;
		this.customer = customer;
		this.orderItems = orderItems;
		this.status = status;
		this.preparedBy = preparedBy;
	}

	/** Constructor that takes all properties for an Order EXCEPT for id */
	public Order(User customer, Map<Item, Integer> orderItems, OrderStatus status, User preparedBy) {
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
