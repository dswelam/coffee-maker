package edu.ncsu.csc326.wolfcafe.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to transfer Inventory data between the client and server. This class
 * will serve as the response in the REST API.
 *
 * @author Dania Swelam
 */
public class InventoryDto {

    /** id for inventory entry */
    private Long                 id;

    /**
     * The ingredients in an inventory where each ingredient name maps to an
     * amount
     */
    private Map<String, Integer> ingredients = new HashMap<>();

    /**
     * Default InventoryDto constructor.
     */
    public InventoryDto () {
        // intentionally empty
    }

    /**
     * Creates an Inventory with all fields
     *
     * @param id
     *            inventory's id
     * @param ingredients
     *            the inventory's ingredients
     */
    public InventoryDto ( final Long id, final Map<String, Integer> ingredients ) {
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
    public InventoryDto ( final Map<String, Integer> ingredients ) {
        super();
        this.ingredients = ingredients;
    }

    /**
     * Gets the inventory id.
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * Inventory id to set.
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
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
