package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeDto;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import edu.ncsu.csc326.wolfcafe.entity.Recipe;
import edu.ncsu.csc326.wolfcafe.exception.InvalidIngredientAmountException;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.InventoryMapper;
import edu.ncsu.csc326.wolfcafe.mapper.RecipeMapper;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.repository.RecipeRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.MakeRecipeService;

/**
 * Implementation of the MakeRecipeService interface.
 */
@Service
public class MakeRecipeServiceImpl implements MakeRecipeService {

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private InventoryRepository inventoryRepository;

    /** Connection to the inventory service to manage inventory */
    @Autowired
    private InventoryService    inventoryService;

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private RecipeRepository    recipeRepository;

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
    @Override
    public boolean makeRecipe ( final InventoryDto inventoryDto, final RecipeDto recipeDto ) {
        final Inventory inventory = InventoryMapper.mapToInventory( inventoryDto );
        final Recipe recipe = RecipeMapper.mapToRecipe( recipeDto );

        if ( enoughIngredients( inventory, recipe ) ) {
            final Map<String, Integer> invMap = inventory.getIngredients();
            final Map<String, Integer> recMap = recipe.getIngredients();

            for ( final Map.Entry<String, Integer> entry : recMap.entrySet() ) {
                final String name = entry.getKey();
                final int amount = entry.getValue();
                invMap.put( name, invMap.get( name ) - amount );
            }

            inventory.setIngredients( invMap );
            inventoryRepository.save( inventory );
            return true;
        }
        return false;
    }

    /**
     * Returns true if there are enough ingredients to make the beverage.
     *
     * @param inventory
     *            coffee maker inventory
     * @param recipe
     *            recipe to check if there are enough ingredients
     * @return true if enough ingredients to make the beverage
     */
    private boolean enoughIngredients ( final Inventory inventory, final Recipe recipe ) {
        final Map<String, Integer> invMap = inventory.getIngredients();
        final Map<String, Integer> recMap = recipe.getIngredients();

        for ( final Map.Entry<String, Integer> entry : recMap.entrySet() ) {
            final String name = entry.getKey();
            final int required = entry.getValue();
            final int available = invMap.getOrDefault( name, 0 );
            if ( available < required ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Makes the recipe with the given ID, deducting the ingredients from
     * inventory and returning any change from the payment.
     *
     * @param recipeId
     *            ID of the recipe to make
     * @param payment
     *            amount paid by the user
     * @return change to return to the user
     * @throws InvalidIngredientAmountException
     *             if there are not enough ingredients to make the recipe
     * @throws IllegalArgumentException
     *             if the payment is invalid or insufficient
     * @throws ResourceNotFoundException
     *             if the recipe with the given ID does not exist
     */
    @Override
    public int makeRecipe ( final Long recipeId, final String payment )
            throws InvalidIngredientAmountException, IllegalArgumentException {
        final Recipe recipe = recipeRepository.findById( recipeId )
                .orElseThrow( () -> new ResourceNotFoundException( "Recipe not found" ) );

        int paymentInt;
        try {
            paymentInt = Integer.parseInt( payment );
        }
        catch ( final NumberFormatException e ) {
            throw new IllegalArgumentException( "Invalid Payment" );
        }
        if ( paymentInt < recipe.getPrice() ) {
            throw new IllegalArgumentException( "Insufficient Payment" );
        }
        if ( !inventoryService.hasEnoughIngredients( recipe ) ) {
            throw new InvalidIngredientAmountException( "Insufficient Ingredients" );
        }
        inventoryService.useIngredients( recipe );
        return paymentInt - recipe.getPrice();
    }

}
