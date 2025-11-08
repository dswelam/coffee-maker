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

@Entity
@Table ( name = "order_line" )
public class OrderLine {

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long    id;

    @ManyToOne ( optional = false, fetch = FetchType.LAZY )
    @JoinColumn ( name = "order_id" )
    private Order   order;

    @ManyToOne ( optional = false, fetch = FetchType.LAZY )
    @JoinColumn ( name = "item_id" )
    private Item    item;

    @Column ( nullable = false )
    private Integer quantity;

    public Long getId () {
        return id;
    }

    public void setId ( final Long id ) {
        this.id = id;
    }

    public Order getOrder () {
        return order;
    }

    public void setOrder ( final Order order ) {
        this.order = order;
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
