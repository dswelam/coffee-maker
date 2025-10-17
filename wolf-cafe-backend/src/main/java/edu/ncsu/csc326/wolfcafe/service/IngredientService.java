package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;

/**
 * Interface defining Ingredient behaviors
 *
 * @author Nora Cam (nncam)
 */
public interface IngredientService {

    /**
     * Create and save a new ingredient
     *
     * @param ingredientDto
     *            ingredient to create
     * @return the DTO for the ingredient including an ID
     */
    IngredientDto createIngredient ( IngredientDto ingredientDto );

    /**
     * Get an ingredient by its ID
     *
     * @param ingredientId
     *            id of the ingredient to get
     * @return the DTO for the ingredient
     * @throws ResourceNotFoundException
     *             if the ingredient doesn't exist
     */
    IngredientDto getIngredientById ( Long ingredientId );

    /**
     * Get an ingredient by its name
     *
     * @param ingredientName
     *            name of the ingredient to get
     * @return the DTO for the ingredient
     * @throws ResourceNotFoundException
     *             if the ingredient doesn't exist
     */
    IngredientDto getIngredientByName ( String ingredientName );

    /**
     * Check if an ingredient name already exists
     *
     * @param ingredientName
     *            name of the ingredient to check
     * @return true if the name is a duplicate
     */
    boolean isDuplicateName ( String ingredientName );

    /**
     * Gets all existing ingredients
     *
     * @return a list of all ingredients
     */
    List<IngredientDto> getAllIngredients ();

    /**
     * Update an ingredient with all new fields
     *
     * @param ingredientId
     *            id of the ingredient to update
     * @param ingredientDto
     *            ingredient to replace old one with
     * @return the DTO for the ingredient
     * @throws ResourceNotFoundException
     *             if the ingredient doesn't exist
     */
    IngredientDto updateIngredient ( Long ingredientId, IngredientDto ingredientDto );

    /**
     * Deletes an ingredient
     *
     * @param ingredientId
     *            id of the ingredient to delete
     * @throws ResourceNotFoundException
     *             if the ingredient doesn't exist
     */
    void deleteIngredient ( Long ingredientId );

}
