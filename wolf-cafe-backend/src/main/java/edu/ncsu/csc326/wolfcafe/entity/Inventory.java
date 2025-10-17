package edu.ncsu.csc326.wolfcafe.entity;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;

/**
 * Inventory for the coffee maker. Inventory is a Data Access Object (DAO) is
 * tied to the database using Hibernate libraries. InventoryRepository provides
 * the methods for database CRUD operations.
 */
@Entity
public class Inventory {

    /** id for inventory entry */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long                 id;

    /**
     * The ingredients in an inventory. Each ingredient name maps to an amount
     * stored in inventory.
     *
     * Source: https://www.baeldung.com/hibernate-persisting-maps
     */
    @ElementCollection
    @CollectionTable ( name = "inventory_ingredients", joinColumns = @JoinColumn ( name = "inventory_id" ) )
    @MapKeyColumn ( name = "ingredient_name" )
    @Column ( name = "ingredient_amount" )
    private Map<String, Integer> ingredients = new HashMap<>();

    /**
     * Empty constructor for Hibernate
     */
    public Inventory () {
        // Intentionally empty so that Hibernate can instantiate
        // Inventory object.
    }

    /**
     * Creates an Inventory with all fields
     *
     * @param id
     *            inventory's id
     * @param ingredients
     *            the inventory's ingredients
     */
    public Inventory ( final Long id, final Map<String, Integer> ingredients ) {
        super();
        this.id = id;
        this.ingredients = ingredients;
    }

    /**
     * Creates an Inventory with all fields
     *
     * @param ingredients
     *            the inventory's ingredients
     */
    public Inventory ( final Map<String, Integer> ingredients ) {
        super();
        this.ingredients = ingredients;
    }

    /**
     * Returns the ID of the entry in the DB
     *
     * @return long
     */
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the ingredients in the Inventory
     *
     * @return the ingredients
     */
    public Map<String, Integer> getIngredients () {
        return ingredients;
    }

    /**
     * Sets the ingredients in the Inventory
     *
     * @param ingredients
     *            the ingredients to set
     */
    public void setIngredients ( final Map<String, Integer> ingredients ) {
        this.ingredients = ingredients;
    }

}
