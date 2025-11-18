package edu.ncsu.csc326.wolfcafe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Order line for an order in the cafe. OrderLine is a Data Access Object (DAO)
 * tied to the database using Hibernate libraries. OrderRepository provides
 * the methods for database CRUD operations.
 *
 * @author Brooke Wu (bwu25)
 * @author Dania Swelam (dswelam)
 */
@Entity
@Table ( name = "order_line" )
public class OrderLine {

    /** Order line id */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long    id;

    /** Order associated with this order line */
    @ManyToOne ( optional = true, fetch = FetchType.LAZY )
    @JoinColumn ( name = "order_id" )
    private Order   order;

    /** Item in the order line */
    @ManyToOne ( optional = false, fetch = FetchType.LAZY )
    @JoinColumn ( name = "item_id" )
    private Item    item;

    /** Quantity of the item in the order line */
    @Column ( nullable = false )
    private Integer quantity;

    /**
     * Default constructor for the OrderLine
     */
    public OrderLine () {
        super();
    }

    /**
     * Constructor that takes all properties for an OrderLine
     *
     * @param id
     *            the order line id
     * @param order
     *            the order associated with this order line
     * @param item
     *            the item in the order line
     * @param quantity
     *            the quantity of the item in the order line
     */
    public OrderLine ( final Long id, final Order order, final Item item, final Integer quantity ) {
        this.id = id;
        this.order = order;
        this.item = item;
        this.quantity = quantity;
    }

    /**
     * Constructor without id for the OrderLine
     * @param order the order associated with this order line
     * @param item the item in the order line
     * @param quantity the quantity of the item in the order line
     */
    public OrderLine ( final Order order, final Item item, final Integer quantity ) {
        this.order = order;
        this.item = item;
        this.quantity = quantity;
    }

    /**
     * Gets the id of the order line
     * @return the order line id
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the id of the order line
     * @param id the order line id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the order associated with this order line
     * @return the order
     */
    public Order getOrder () {
        return order;
    }

    /**
     * Sets the order associated with this order line
     * @param order the order
     */
    public void setOrder ( final Order order ) {
        this.order = order;
    }

    /**
     * Gets the item in the order line
     * @return the item
     */
    public Item getItem () {
        return item;
    }

    /**
     * Sets the item in the order line
     * @param item the item
     */
    public void setItem ( final Item item ) {
        this.item = item;
    }

    /**
     * Gets the quantity of the item in the order line
     * @return the quantity
     */
    public Integer getQuantity () {
        return quantity;
    }

    /**
     * Sets the quantity of the item in the order line
     * @param quantity the quantity
     */
    public void setQuantity ( final Integer quantity ) {
        this.quantity = quantity;
    }
}
