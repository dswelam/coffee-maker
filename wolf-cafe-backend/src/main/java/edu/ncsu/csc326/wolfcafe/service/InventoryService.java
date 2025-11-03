package edu.ncsu.csc326.wolfcafe.service;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;

/**
 * Interface defining the inventory behaviors.
 *
 * @author Dania Swelam
 */
public interface InventoryService {

    /**
     * Creates the inventory.
     *
     * @param inventoryDto
     *            inventory to create
     * @return updated inventory after creation
     */
    InventoryDto createInventory ( InventoryDto inventoryDto );

    /**
     * Returns the single inventory.
     *
     * @return the single inventory
     */
    InventoryDto getInventory ();

    /**
     * Updates the contents of the inventory.
     *
     * @param inventoryDto
     *            values to update
     * @return updated inventory
     */
    InventoryDto updateInventory ( InventoryDto inventoryDto );

    /**
     * Checks if there are enough ingredients in inventory for the given recipe.
     *
     * @param recipe
     *            recipe to check
     * @return true if enough ingredients, false otherwise
     */
    boolean hasEnoughIngredients ( Item item );

    /**
     * Deducts the ingredients for the given recipe from inventory.
     *
     * @param recipe
     *            recipe to use ingredients from
     */
    void useIngredients ( Item item );

}
