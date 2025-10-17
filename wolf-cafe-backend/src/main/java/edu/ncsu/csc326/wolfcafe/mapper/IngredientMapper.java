package edu.ncsu.csc326.wolfcafe.mapper;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;

/**
 * Converts between Ingredient entity and IngredientDto
 *
 * @author Nora Cam (nncam)
 */
public class IngredientMapper {

    /**
     * Converts a Ingredient entity to IngredientDto
     *
     * @param ingredient
     *            Ingredient to convert
     * @return IngredientDto object
     */
    public static IngredientDto mapToIngredientDto ( final Ingredient ingredient ) {
        return new IngredientDto( ingredient.getId(), ingredient.getName() );

    }

    /**
     * Converts a IngredientDto object to a Ingredient entity.
     *
     * @param ingredientDto
     *            IngredientDto to convert
     * @return Ingredient entity
     */
    public static Ingredient mapToIngredient ( final IngredientDto ingredientDto ) {
        return new Ingredient( ingredientDto.getId(), ingredientDto.getName() );
    }

}
