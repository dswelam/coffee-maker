package edu.ncsu.csc326.wolfcafe.dto;

import edu.ncsu.csc326.wolfcafe.entity.Item;

public class OrderLineDto {
    private Long    id;
    private Item    item;
    private Integer quantity;

    public OrderLineDto () {
    }

    public OrderLineDto ( final Long id, final Item item, final Integer quantity ) {
        this.id = id;
        this.item = item;
        this.quantity = quantity;
    }

    public Long getId () {
        return id;
    }

    public void setId ( final Long id ) {
        this.id = id;
    }

    public Item getItem () {
        return item;
    }

    public void setItem ( final Item item ) {
        this.item = item;
    }

    public Integer getQuantity () {
        return quantity;
    }

    public void setQuantity ( final Integer quantity ) {
        this.quantity = quantity;
    }
}
