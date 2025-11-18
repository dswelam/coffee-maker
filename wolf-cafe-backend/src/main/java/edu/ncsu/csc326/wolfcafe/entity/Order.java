package edu.ncsu.csc326.wolfcafe.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    private Long            id;

    /** Order customer */
    @ManyToOne ( fetch = FetchType.LAZY, optional = false )
    @JoinColumn ( name = "customer_id" )
    private User            customer;

    /** Order items
     * 	Key = Item
     * 	Value = Integer (quantity of Item) */
    @OneToMany ( mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<OrderLine> orderItems = new ArrayList<>();

    /** Order status */
    private OrderStatus     status;

    /** Order staff */
    @ManyToOne ( fetch = FetchType.LAZY, optional = true )
    // Nullable true to allow for orders that have not yet been prepared
    @JoinColumn ( name = "user_prepared", nullable = true )
    private User            preparedBy;

    /**
     * Defines all order statuses in the system
     */
    public enum OrderStatus {
        PLACED, IN_PROGRESS, READY, FULFILLED, CANCELLED
    }

    /**
     * Default constructor for Hibernate
     */
    public Order () {
        super();
    }

    /** Constructor that takes all properties for an Order, including id */
    public Order ( final Long id, final User customer, final List<OrderLine> orderItems, final OrderStatus status,
            final User preparedBy ) {
        super();
        this.id = id;
        this.customer = customer;
        this.orderItems = orderItems;
        this.status = status;
        this.preparedBy = preparedBy;
    }

    /** Constructor that takes all properties for an Order EXCEPT for id */
    public Order ( final User customer, final List<OrderLine> orderItems, final OrderStatus status,
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

    public List<OrderLine> getOrderItems () {
        return orderItems;
    }

    public void setOrderItems ( final List<OrderLine> orderItems ) {
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
