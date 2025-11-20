package edu.ncsu.csc326.wolfcafe.dto;

import edu.ncsu.csc326.wolfcafe.entity.Item;

/**
 * Used to transfer OrderLine data between the client and server. This class
 * will serve as part of the response in the REST API.
 *
 * @author Brooke Wu (bwu25)
 * @author Dania Swelam (dswelam)
 */
public class OrderLineDto {

    /** Order line id */
    private Long    id;

    /** Item in the order line */
    private Item    item;

    /** Quantity of the item in the order line */
    private Integer quantity;

    /**
     * Default constructor for the OrderLineDto
     */
    public OrderLineDto () {
    }

    /**
     * Constructor that takes all properties for an OrderLineDto
     *
     * @param id
     *            the order line id
     * @param item
     *            the item in the order line
     * @param quantity
     *            the quantity of the item in the order line
     */
    public OrderLineDto ( final Long id, final Item item, final Integer quantity ) {
        this.id = id;
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
