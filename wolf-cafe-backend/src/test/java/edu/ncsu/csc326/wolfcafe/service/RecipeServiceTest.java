package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.RecipeDto;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.RecipeRepository;

/**
 * Test class for the RecipeService and its implementation
 */
@SpringBootTest
public class RecipeServiceTest {

    /** Reference to RecipeService (and RecipeServiceImpl). */
    @Autowired
    private RecipeService    recipeService;

    /** Reference to RecipeRepository */
    @Autowired
    private RecipeRepository recipeRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    void setUp () throws Exception {
        recipeRepository.deleteAll();
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.RecipeService#createRecipe(edu.ncsu.csc326.coffee_maker.dto.RecipeDto)}.
     */
    @Test
    @Transactional
    void testCreateRecipe () {
        final RecipeDto recipeDto = new RecipeDto( "Coffee", 50,
                new java.util.HashMap<>( Map.of( "Coffee", 2, "Milk", 1, "Sugar", 1, "Chocolate", 0 ) ) );
        final RecipeDto savedRecipe = recipeService.createRecipe( recipeDto );
        assertAll( "Recipe contents", () -> assertTrue( savedRecipe.getId() > 1L ),
                () -> assertEquals( "Coffee", savedRecipe.getName() ), () -> assertEquals( 50, savedRecipe.getPrice() ),
                () -> assertEquals( 2, savedRecipe.getIngredients().get( "Coffee" ) ),
                () -> assertEquals( 1, savedRecipe.getIngredients().get( "Milk" ) ),
                () -> assertEquals( 1, savedRecipe.getIngredients().get( "Sugar" ) ),
                () -> assertEquals( 0, savedRecipe.getIngredients().get( "Chocolate" ) ) );

        final RecipeDto retrievedRecipe = recipeService.getRecipeById( savedRecipe.getId() );
        assertAll( "Recipe contents", () -> assertEquals( savedRecipe.getId(), retrievedRecipe.getId() ),
                () -> assertEquals( "Coffee", retrievedRecipe.getName() ),
                () -> assertEquals( 50, retrievedRecipe.getPrice() ),
                () -> assertEquals( 2, retrievedRecipe.getIngredients().get( "Coffee" ) ),
                () -> assertEquals( 1, retrievedRecipe.getIngredients().get( "Milk" ) ),
                () -> assertEquals( 1, retrievedRecipe.getIngredients().get( "Sugar" ) ),
                () -> assertEquals( 0, retrievedRecipe.getIngredients().get( "Chocolate" ) ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.RecipeService#isDuplicateName(java.lang.String)}.
     */
    @Test
    @Transactional
    void testIsDuplicateName () {
        // create coffee recipe
        final RecipeDto recipeDto = new RecipeDto( "Coffee", 50,
                new java.util.HashMap<>( Map.of( "Coffee", 2, "Milk", 1, "Sugar", 1, "Chocolate", 0 ) ) );
        recipeService.createRecipe( recipeDto );

        // check for duplicates
        assertTrue( recipeService.isDuplicateName( "Coffee" ) );
        assertFalse( recipeService.isDuplicateName( "Latte" ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.RecipeService#getAllRecipes()}.
     */
    @Test
    @Transactional
    void testGetAllRecipes () {
        // create recipes
        RecipeDto newRecipeDto = new RecipeDto( "Coffee", 50,
                new java.util.HashMap<>( Map.of( "Coffee", 2, "Milk", 1, "Sugar", 1, "Chocolate", 0 ) ) );
        final RecipeDto coffeeRecipe = recipeService.createRecipe( newRecipeDto );

        newRecipeDto = new RecipeDto( "Mocha", 150,
                new java.util.HashMap<>( Map.of( "Coffee", 2, "Milk", 3, "Sugar", 0, "Chocolate", 1 ) ) );
        final RecipeDto mochaRecipe = recipeService.createRecipe( newRecipeDto );

        newRecipeDto = new RecipeDto( "Latte", 100,
                new java.util.HashMap<>( Map.of( "Coffee", 2, "Milk", 3, "Sugar", 1, "Chocolate", 0 ) ) );
        final RecipeDto latteRecipe = recipeService.createRecipe( newRecipeDto );

        // get all recipes
        final List<RecipeDto> recipes = recipeService.getAllRecipes();

        // validate recipes
        assertEquals( 3, recipes.size() );

        assertAll( "Coffee recipe contents", () -> assertEquals( coffeeRecipe.getId(), recipes.get( 0 ).getId() ),
                () -> assertEquals( "Coffee", recipes.get( 0 ).getName() ),
                () -> assertEquals( 50, recipes.get( 0 ).getPrice() ),
                () -> assertEquals( 2, recipes.get( 0 ).getIngredients().get( "Coffee" ) ),
                () -> assertEquals( 1, recipes.get( 0 ).getIngredients().get( "Milk" ) ),
                () -> assertEquals( 1, recipes.get( 0 ).getIngredients().get( "Sugar" ) ),
                () -> assertEquals( 0, recipes.get( 0 ).getIngredients().get( "Chocolate" ) ) );

        assertAll( "Mocha recipe contents", () -> assertEquals( mochaRecipe.getId(), recipes.get( 1 ).getId() ),
                () -> assertEquals( "Mocha", recipes.get( 1 ).getName() ),
                () -> assertEquals( 150, recipes.get( 1 ).getPrice() ),
                () -> assertEquals( 2, recipes.get( 1 ).getIngredients().get( "Coffee" ) ),
                () -> assertEquals( 3, recipes.get( 1 ).getIngredients().get( "Milk" ) ),
                () -> assertEquals( 0, recipes.get( 1 ).getIngredients().get( "Sugar" ) ),
                () -> assertEquals( 1, recipes.get( 1 ).getIngredients().get( "Chocolate" ) ) );

        assertAll( "Latte recipe contents", () -> assertEquals( latteRecipe.getId(), recipes.get( 2 ).getId() ),
                () -> assertEquals( "Latte", recipes.get( 2 ).getName() ),
                () -> assertEquals( 100, recipes.get( 2 ).getPrice() ),
                () -> assertEquals( 2, recipes.get( 2 ).getIngredients().get( "Coffee" ) ),
                () -> assertEquals( 3, recipes.get( 2 ).getIngredients().get( "Milk" ) ),
                () -> assertEquals( 1, recipes.get( 2 ).getIngredients().get( "Sugar" ) ),
                () -> assertEquals( 0, recipes.get( 2 ).getIngredients().get( "Chocolate" ) ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.RecipeService#updateRecipe(java.lang.Long, edu.ncsu.csc326.coffee_maker.dto.RecipeDto)}.
     */
    @Test
    @Transactional
    void testUpdateRecipe () {
        // create recipe
        final RecipeDto recipeDto = new RecipeDto( "Coffee", 50,
                new HashMap<>( Map.of( "Coffee", 2, "Milk", 1, "Sugar", 1, "Chocolate", 0 ) ) );
        final RecipeDto savedRecipe = recipeService.createRecipe( recipeDto );

        // update recipe
        recipeService.updateRecipe( savedRecipe.getId(), new RecipeDto( "A Mess", 200,
                new HashMap<>( Map.of( "Coffee", 4, "Milk", 4, "Sugar", 4, "Chocolate", 4 ) ) ) );

        // check
        final RecipeDto newRecipe = recipeService.getRecipeById( savedRecipe.getId() );
        assertAll( "Recipe contents", () -> assertEquals( savedRecipe.getId(), newRecipe.getId() ),
                () -> assertEquals( "A Mess", newRecipe.getName() ), () -> assertEquals( 200, newRecipe.getPrice() ),
                () -> assertEquals( 4, newRecipe.getIngredients().get( "Coffee" ) ),
                () -> assertEquals( 4, newRecipe.getIngredients().get( "Milk" ) ),
                () -> assertEquals( 4, newRecipe.getIngredients().get( "Sugar" ) ),
                () -> assertEquals( 4, newRecipe.getIngredients().get( "Chocolate" ) ) );

        final Long invalidId = savedRecipe.getId() + 1;
        assertThrows( ResourceNotFoundException.class,
                () -> recipeService.updateRecipe( invalidId, new RecipeDto( "Coffee", 50,
                        new HashMap<>( Map.of( "Coffee", 2, "Milk", 1, "Sugar", 1, "Chocolate", 0 ) ) ) ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.RecipeService#deleteRecipe(java.lang.Long)}.
     */
    @Test
    @Transactional
    void testDeleteRecipe () {
        // create recipe
        final RecipeDto recipeDto = new RecipeDto( "Coffee", 50,
                new HashMap<>( Map.of( "Coffee", 2, "Milk", 1, "Sugar", 1, "Chocolate", 0 ) ) );
        final RecipeDto savedRecipe = recipeService.createRecipe( recipeDto );

        // delete recipe
        recipeService.deleteRecipe( savedRecipe.getId() );

        // validate deletion
        assertThrows( ResourceNotFoundException.class, () -> recipeService.getRecipeById( savedRecipe.getId() ) );
        assertThrows( ResourceNotFoundException.class, () -> recipeService.deleteRecipe( savedRecipe.getId() ) );
    }

}
