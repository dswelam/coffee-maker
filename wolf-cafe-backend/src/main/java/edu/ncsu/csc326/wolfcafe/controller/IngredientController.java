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

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.service.IngredientService;
import lombok.AllArgsConstructor;

/**
 * Controller for Ingredients. Contains CRUD operations.
 *
 * @author Nora Cam (nncam)
 */
@CrossOrigin ( "*" )
@RestController
@AllArgsConstructor
@RequestMapping ( "/api/ingredients" )
public class IngredientController {

    /** Link to IngredientService */
    @Autowired
    private final IngredientService ingredientService;

    /**
     * REST API method to provide GET access to all ingredients in the system
     *
     * @return JSON representation of all ingredients
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @GetMapping
    public List<IngredientDto> getIngredients () {
        return ingredientService.getAllIngredients();
    }

    /**
     * REST API method to provide GET access to a specific ingredient, as
     * indicated by the path variable provided (the name of the ingredient
     * desired)
     *
     * @param name
     *            ingredient name
     * @return response to the request
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @GetMapping ( "{name}" )
    public ResponseEntity<IngredientDto> getIngredient ( @PathVariable ( "name" ) final String name ) {
        final IngredientDto ingredientDto = ingredientService.getIngredientByName( name );
        return ResponseEntity.ok( ingredientDto );
    }

    /**
     * REST API method to provide POST access to the Ingredient model.
     *
     * @param ingredientDto
     *            The valid Ingredient to be saved.
     * @return ResponseEntity indicating success if the Ingredient could be
     *         saved to the inventory, or an error if it could not be
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @PostMapping
    public ResponseEntity<IngredientDto> createIngredient ( @RequestBody final IngredientDto ingredientDto ) {
        if ( ingredientService.isDuplicateName( ingredientDto.getName() ) ) {
            return new ResponseEntity<>( ingredientDto, HttpStatus.CONFLICT );
        }

        final IngredientDto savedIngredientDto = ingredientService.createIngredient( ingredientDto );
        return ResponseEntity.ok( savedIngredientDto );

    }

    /**
     * REST API method to provide PUT access to the Ingredient model.
     *
     * @param ingredientId
     *            the id of the ingredient to update
     * @param ingredientDto
     *            The valid Ingredient to be updated.
     * @return ResponseEntity indicating success if the Ingredient could be
     *         updated, or an error if it could not be.
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @PutMapping ( "{id}" )
    public ResponseEntity<IngredientDto> updateIngredient ( @PathVariable ( "id" ) final Long ingredientId,
            @RequestBody final IngredientDto ingredientDto ) {
        if ( ingredientService.getIngredientById( ingredientId ) == null ) {
            return new ResponseEntity<>( ingredientDto, HttpStatus.NOT_FOUND );
        }
        final IngredientDto savedIngredientDto = ingredientService.updateIngredient( ingredientId, ingredientDto );
        return ResponseEntity.ok( savedIngredientDto );
    }

    /**
     * REST API method to allow deleting a Ingredient from the CoffeeMaker's
     * Inventory, by making a DELETE request to the API endpoint and indicating
     * the ingredient to delete (as a path variable)
     *
     * @param ingredientId
     *            The id of the Ingredient to delete
     * @return Success if the ingredient could be deleted; an error if the
     *         ingredient does not exist
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @DeleteMapping ( "{id}" )
    public ResponseEntity<String> deleteIngredient ( @PathVariable ( "id" ) final Long ingredientId ) {
        ingredientService.deleteIngredient( ingredientId );
        return ResponseEntity.ok( "Ingredient deleted successfully." );
    }
}
