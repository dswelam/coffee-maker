package edu.ncsu.csc326.wolfcafe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeDto;
import edu.ncsu.csc326.wolfcafe.exception.InvalidIngredientAmountException;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.MakeRecipeService;
import edu.ncsu.csc326.wolfcafe.service.RecipeService;

/**
 * MakeRecipeController provides the endpoint for making a recipe.
 *
 * @author Dania Swelam
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/makerecipe" )
public class MakeRecipeController {

    /** Connection to InventoryService */
    @Autowired
    private InventoryService  inventoryService;

    /** Connection to RecipeService */
    @Autowired
    private RecipeService     recipeService;

    /** Connection to MakeRecipeService */
    @Autowired
    private MakeRecipeService makeRecipeService;

    /**
     * REST API method to make coffee by completing a POST request with the ID
     * of the recipe as the path variable and the amount that has been paid as
     * the body of the response
     *
     * @param recipeName
     *            recipe name to make
     * @param amtPaid
     *            amount paid
     * @return The change the customer is due if successful
     */
    @PreAuthorize ( "hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')" )
    @PostMapping ( "{name}" )
    public ResponseEntity< ? > makeRecipe ( @PathVariable ( "name" ) final String recipeName,
            @RequestBody final Integer amtPaid ) {
        RecipeDto recipeDto = null;
        try {
            recipeDto = recipeService.getRecipeByName( recipeName );
            final int change = makeRecipe( recipeDto, amtPaid );
            return ResponseEntity.ok( change );
        }
        catch ( final ResourceNotFoundException e ) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( "Recipe not found." );
        }
        catch ( final InvalidIngredientAmountException e ) {
            return ResponseEntity.status( HttpStatus.BAD_REQUEST )
                    .body( "Insufficient inventory to make beverage. Your money has been returned." );
        }
        catch ( final IllegalArgumentException e ) {
            if ( e.getMessage().contains( "Insufficient Payment" ) ) {
                return ResponseEntity.status( HttpStatus.CONFLICT )
                        .body( "Insufficient funds to pay. Your money has been returned." );
            }
            else if ( e.getMessage().contains( "Invalid Payment" ) ) {
                return ResponseEntity.status( HttpStatus.UNPROCESSABLE_ENTITY )
                        .body( "Invalid payment. Please enter a valid integer." );
            }
            else {
                return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( e.getMessage() );
            }
        }
    }

    /**
     * Helper method to make coffee
     *
     * @param toPurchase
     *            recipe that we want to make
     * @param amtPaid
     *            money that the user has given the machine
     * @return change if there was enough money to make the coffee, throws
     *         exceptions if not
     */
    private int makeRecipe ( final RecipeDto toPurchase, final int amtPaid ) {
        final InventoryDto inventoryDto = inventoryService.getInventory();
        if ( amtPaid < toPurchase.getPrice() ) {
            throw new IllegalArgumentException( "Insufficient Payment" );
        }
        if ( !makeRecipeService.makeRecipe( inventoryDto, toPurchase ) ) {
            throw new InvalidIngredientAmountException( "Insufficient Ingredients" );
        }
        return amtPaid - toPurchase.getPrice();
    }

}
