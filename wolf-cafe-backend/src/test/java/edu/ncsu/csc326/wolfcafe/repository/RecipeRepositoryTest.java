package edu.ncsu.csc326.wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import edu.ncsu.csc326.wolfcafe.entity.Recipe;

/**
 * Tests Recipe repository
 */
@DataJpaTest
@AutoConfigureTestDatabase ( replace = Replace.NONE )
class RecipeRepositoryTest {

    /** Reference to recipe repository */
    @Autowired
    private RecipeRepository recipeRepository;

    /** Coffee recipe */
    private Recipe           recipe1;
    /** Latte recipe */
    private Recipe           recipe2;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        recipeRepository.deleteAll();

        // Setup Coffee recipe
        final Map<String, Integer> coffeeIngredients = Map.of( "Coffee", 3 );
        recipe1 = new Recipe( "Coffee", 50, coffeeIngredients );

        // Setup Latte recipe
        final Map<String, Integer> latteIngredients = Map.of( "Coffee", 3, "Milk", 2, "Sugar", 1 );
        recipe2 = new Recipe( "Latte", 100, latteIngredients );

        recipeRepository.save( recipe1 );
        recipeRepository.save( recipe2 );
    }

    /**
     * Tests retrieving the Coffee recipe by name. Verifies that the correct
     * values are retrieved for the price and number of each ingredient.
     */
    @Test
    public void testGetCoffeeRecipeByName () {
        final Optional<Recipe> recipe = recipeRepository.findByName( "Coffee" );
        final Recipe actualRecipe = recipe.get();
        assertAll( "Recipe contents", () -> assertEquals( "Coffee", actualRecipe.getName() ),
                () -> assertEquals( 50, actualRecipe.getPrice() ),
                () -> assertEquals( 3, actualRecipe.getIngredients().get( "Coffee" ) ) );
    }

    /**
     * Tests retrieving the Latte recipe by name. Verifies that the correct
     * values are retrieved for the price and number of each ingredient.
     */
    @Test
    public void testGetLatteRecipeByName () {
        final Optional<Recipe> recipe = recipeRepository.findByName( "Latte" );
        final Recipe actualRecipe = recipe.get();
        assertAll( "Recipe contents", () -> assertEquals( "Latte", actualRecipe.getName() ),
                () -> assertEquals( 100, actualRecipe.getPrice() ),
                () -> assertEquals( 3, actualRecipe.getIngredients().get( "Coffee" ) ),
                () -> assertEquals( 2, actualRecipe.getIngredients().get( "Milk" ) ),
                () -> assertEquals( 1, actualRecipe.getIngredients().get( "Sugar" ) ) );
    }

    /**
     * Tests retrieving an invalid recipe by name. Verifies that an empty recipe
     * is returned.
     */
    @Test
    public void testGetRecipeByNameInvalid () {
        final Optional<Recipe> recipe = recipeRepository.findByName( "Unknown" );
        assertTrue( recipe.isEmpty() );
    }

}
