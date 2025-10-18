package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.RecipeDto;
import edu.ncsu.csc326.wolfcafe.service.RecipeService;
import lombok.AllArgsConstructor;

/**
 * Controller for Recipes.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/recipes" )
@AllArgsConstructor
public class RecipeController {

    /** Service for managing recipes */
    @Autowired
    private final RecipeService recipeService;

    /**
     * REST API method to provide GET access to all recipes in the system
     *
     * @return JSON representation of all recipes
     */
    @PreAuthorize ( "hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')" )
    @GetMapping
    public List<RecipeDto> getRecipes () {
        return recipeService.getAllRecipes();
    }

    /**
     * REST API method to provide GET access to a specific recipe, as indicated
     * by the path variable provided (the name of the recipe desired)
     *
     * @param name
     *            recipe name
     * @return response to the request
     */
    @PreAuthorize ( "hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')" )
    @GetMapping ( "{name}" )
    public ResponseEntity<RecipeDto> getRecipe ( @PathVariable ( "name" ) final String name ) {
        final RecipeDto recipeDto = recipeService.getRecipeByName( name );
        return ResponseEntity.ok( recipeDto );
    }

    /**
     * REST API method to provide POST access to the Recipe model.
     *
     * @param recipeDto
     *            The valid Recipe to be saved.
     * @return ResponseEntity indicating success if the Recipe could be saved to
     *         the inventory, or an error if it could not be
     */
    @PreAuthorize ( "hasAnyRole('ADMIN', 'STAFF')" )
    @PostMapping
    public ResponseEntity<RecipeDto> createRecipe ( @RequestBody final RecipeDto recipeDto ) {
        if ( recipeService.isDuplicateName( recipeDto.getName() ) ) {
            return new ResponseEntity<>( recipeDto, HttpStatus.CONFLICT );
        }
        if ( recipeService.getAllRecipes().size() < 3 ) {
            final RecipeDto savedRecipeDto = recipeService.createRecipe( recipeDto );
            return ResponseEntity.ok( savedRecipeDto );
        }
        else {
            return new ResponseEntity<>( recipeDto, HttpStatus.INSUFFICIENT_STORAGE );
        }
    }

    /**
     * REST API method to allow updating an existing Recipe
     *
     * @param recipeId
     *            the id of the recipe to update
     * @param recipeDto
     *            the updated recipe information
     * @return the updated recipe, or an error if the update could not be
     *         performed
     */
    @PreAuthorize ( "hasAnyRole('ADMIN', 'STAFF')" )
    @PutMapping ( "{id}" )
    public ResponseEntity<RecipeDto> updateRecipe ( @PathVariable ( "id" ) final Long recipeId,
            @RequestBody final RecipeDto recipeDto ) {
        // Check if recipe exists
        final RecipeDto existingRecipe = recipeService.getRecipeById( recipeId );
        if ( existingRecipe == null ) {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }

        // Validate price
        if ( recipeDto.getPrice() == null || recipeDto.getPrice() <= 0 ) {
            return new ResponseEntity<>( recipeDto, HttpStatus.BAD_REQUEST );
        }

        // Validate ingredients
        if ( recipeDto.getIngredients() == null || recipeDto.getIngredients().isEmpty() ) {
            return new ResponseEntity<>( recipeDto, HttpStatus.BAD_REQUEST );
        }
        for ( final Integer unit : recipeDto.getIngredients().values() ) {
            if ( unit == null || unit <= 0 ) {
                return new ResponseEntity<>( recipeDto, HttpStatus.BAD_REQUEST );
            }
        }

        // Check for duplicate name (if name is changed)
        if ( !existingRecipe.getName().equals( recipeDto.getName() )
                && recipeService.isDuplicateName( recipeDto.getName() ) ) {
            return new ResponseEntity<>( recipeDto, HttpStatus.CONFLICT );
        }

        // Perform update
        final RecipeDto updatedRecipeDto = recipeService.updateRecipe( recipeId, recipeDto );
        return ResponseEntity.ok( updatedRecipeDto );
    }

    /**
     * REST API method to allow deleting a Recipe from the CoffeeMaker's
     * Inventory, by making a DELETE request to the API endpoint and indicating
     * the recipe to delete (as a path variable)
     *
     * @param recipeId
     *            The id of the Recipe to delete
     * @return Success if the recipe could be deleted; an error if the recipe
     *         does not exist
     */
    @PreAuthorize ( "hasAnyRole('ADMIN', 'STAFF')" )
    @DeleteMapping ( "{id}" )
    public ResponseEntity<String> deleteRecipe ( @PathVariable ( "id" ) final Long recipeId ) {
        recipeService.deleteRecipe( recipeId );
        return ResponseEntity.ok( "Recipe deleted successfully." );
    }
}
