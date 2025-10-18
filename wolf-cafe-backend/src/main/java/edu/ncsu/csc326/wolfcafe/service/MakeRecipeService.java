package edu.ncsu.csc326.wolfcafe.service;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeDto;
import edu.ncsu.csc326.wolfcafe.exception.InvalidIngredientAmountException;

/**
 * Interface defining the make recipe behaviors.
 */
public interface MakeRecipeService {

    /**
     * Removes the ingredients used to make the specified recipe. Assumes that
     * the user has checked that there are enough ingredients to make
     *
     * @param inventoryDto
     *            current inventory
     * @param recipeDto
     *            recipe to make
     * @return updated inventory
     */
    boolean makeRecipe ( InventoryDto inventoryDto, RecipeDto recipeDto );

    /**
     * Attempts to make a beverage.
     *
     * @param recipeId
     *            The ID of the recipe to make.
     * @param payment
     *            The amount of money provided.
     * @return The change to return to the user.
     * @throws InvalidIngredientAmountException
     *             if not enough ingredients.
     * @throws IllegalArgumentException
     *             if payment is invalid or insufficient.
     */
    int makeRecipe ( Long recipeId, String payment ) throws InvalidIngredientAmountException, IllegalArgumentException;

}
