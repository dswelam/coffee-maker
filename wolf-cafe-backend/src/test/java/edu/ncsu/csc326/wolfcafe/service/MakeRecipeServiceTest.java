package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeDto;
import edu.ncsu.csc326.wolfcafe.exception.InvalidIngredientAmountException;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;

/**
 * Tests for MakeRecipeService implementation covering UC6 Order Beverage
 * functionality.
 */
@SpringBootTest
class MakeRecipeServiceTest {

    /** Reference to MakeRecipeService */
    @Autowired
    private MakeRecipeService makeRecipeService;

    /** Reference to InventoryService for setup */
    @Autowired
    private InventoryService  inventoryService;

    /** Reference to RecipeService for setup */
    @Autowired
    private RecipeService     recipeService;

    /** Reference to EntityManager for cleanup */
    @Autowired
    private EntityManager     entityManager;

    /**
     * Sets up the test case by cleaning database tables.
     */
    @BeforeEach
    public void setUp () throws Exception {
        // Clean up all tables in proper order due to foreign key constraints
        // Use IF EXISTS to avoid errors when tables don't exist yet
        try {
            entityManager.createNativeQuery( "DELETE FROM inventory_ingredients" ).executeUpdate();
        }
        catch ( Exception e ) {
            // Table might not exist yet, ignore
        }
        try {
            entityManager.createNativeQuery( "DELETE FROM recipe_ingredients" ).executeUpdate();
        }
        catch ( Exception e ) {
            // Table might not exist yet, ignore
        }
        try {
            entityManager.createNativeQuery( "DELETE FROM inventory" ).executeUpdate();
        }
        catch ( Exception e ) {
            // Table might not exist yet, ignore
        }
        try {
            entityManager.createNativeQuery( "DELETE FROM recipe" ).executeUpdate();
        }
        catch ( Exception e ) {
            // Table might not exist yet, ignore
        }
        try {
            entityManager.createNativeQuery( "ALTER TABLE inventory AUTO_INCREMENT = 1" ).executeUpdate();
        }
        catch ( Exception e ) {
            // Table might not exist yet, ignore
        }
        try {
            entityManager.createNativeQuery( "ALTER TABLE recipe AUTO_INCREMENT = 1" ).executeUpdate();
        }
        catch ( Exception e ) {
            // Table might not exist yet, ignore
        }
    }

    /**
     * Test method for successful recipe making with InventoryDto and RecipeDto
     * - enough ingredients.
     */
    @Test
    @Transactional
    void testMakeRecipeInventorySuccess () {
        // Set up inventory with sufficient ingredients
        final Map<String, Integer> inventoryIngredients = new HashMap<>();
        inventoryIngredients.put( "Coffee", 10 );
        inventoryIngredients.put( "Milk", 8 );
        inventoryIngredients.put( "Sugar", 6 );
        inventoryIngredients.put( "Chocolate", 4 );
        final InventoryDto inventoryDto = new InventoryDto( inventoryIngredients );
        inventoryService.createInventory( inventoryDto );

        // Set up recipe
        final Map<String, Integer> recipeIngredients = new HashMap<>();
        recipeIngredients.put( "Coffee", 3 );
        recipeIngredients.put( "Milk", 2 );
        recipeIngredients.put( "Sugar", 1 );
        final RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName( "Test Coffee" );
        recipeDto.setPrice( 50 );
        recipeDto.setIngredients( recipeIngredients );

        // Test successful recipe making
        final boolean result = makeRecipeService.makeRecipe( inventoryDto, recipeDto );

        assertTrue( result, "Recipe should be made successfully when enough ingredients are available" );
    }

    /**
     * Test method for recipe making with insufficient ingredients.
     */
    @Test
    @Transactional
    void testMakeRecipeInventoryInsufficientIngredients () {
        // Set up inventory with insufficient ingredients
        final Map<String, Integer> inventoryIngredients = new HashMap<>();
        inventoryIngredients.put( "Coffee", 1 );
        inventoryIngredients.put( "Milk", 1 );
        inventoryIngredients.put( "Sugar", 1 );
        inventoryIngredients.put( "Chocolate", 1 );
        final InventoryDto inventoryDto = new InventoryDto( inventoryIngredients );

        // Set up recipe requiring more ingredients than available
        final Map<String, Integer> recipeIngredients = new HashMap<>();
        recipeIngredients.put( "Coffee", 5 );
        recipeIngredients.put( "Milk", 3 );
        recipeIngredients.put( "Sugar", 2 );
        final RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName( "Expensive Coffee" );
        recipeDto.setPrice( 100 );
        recipeDto.setIngredients( recipeIngredients );

        // Test insufficient ingredients
        final boolean result = makeRecipeService.makeRecipe( inventoryDto, recipeDto );

        assertFalse( result, "Recipe should not be made when insufficient ingredients are available" );
    }

    /**
     * Test method for successful order beverage with Long recipeId and String
     * payment.
     */
    @Test
    @Transactional
    void testMakeRecipeSuccess () {
        // Set up inventory
        final Map<String, Integer> inventoryIngredients = new HashMap<>();
        inventoryIngredients.put( "Coffee", 15 );
        inventoryIngredients.put( "Milk", 10 );
        inventoryIngredients.put( "Sugar", 8 );
        inventoryIngredients.put( "Chocolate", 5 );
        final InventoryDto inventoryDto = new InventoryDto( inventoryIngredients );
        inventoryService.createInventory( inventoryDto );

        // Set up recipe
        final Map<String, Integer> recipeIngredients = new HashMap<>();
        recipeIngredients.put( "Coffee", 4 );
        recipeIngredients.put( "Milk", 2 );
        recipeIngredients.put( "Sugar", 1 );
        final RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName( "Latte" );
        recipeDto.setPrice( 75 );
        recipeDto.setIngredients( recipeIngredients );
        final RecipeDto createdRecipe = recipeService.createRecipe( recipeDto );

        // Test successful order with exact payment
        final int change = makeRecipeService.makeRecipe( createdRecipe.getId(), "75" );

        assertEquals( 0, change, "Change should be 0 when exact payment is provided" );
    }

    /**
     * Test method for successful order beverage with overpayment.
     */
    @Test
    @Transactional
    void testMakeRecipeSuccessWithChange () {
        // Set up inventory
        final Map<String, Integer> inventoryIngredients = new HashMap<>();
        inventoryIngredients.put( "Coffee", 15 );
        inventoryIngredients.put( "Milk", 10 );
        inventoryIngredients.put( "Sugar", 8 );
        inventoryIngredients.put( "Chocolate", 5 );
        final InventoryDto inventoryDto = new InventoryDto( inventoryIngredients );
        inventoryService.createInventory( inventoryDto );

        // Set up recipe
        final Map<String, Integer> recipeIngredients = new HashMap<>();
        recipeIngredients.put( "Coffee", 3 );
        recipeIngredients.put( "Milk", 1 );
        final RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName( "Simple Coffee" );
        recipeDto.setPrice( 50 );
        recipeDto.setIngredients( recipeIngredients );
        final RecipeDto createdRecipe = recipeService.createRecipe( recipeDto );

        // Test successful order with overpayment
        final int change = makeRecipeService.makeRecipe( createdRecipe.getId(), "100" );

        assertEquals( 50, change, "Change should be 50 when paying 100 for a 50 price recipe" );
    }

    /**
     * Test method for insufficient payment alternative flow.
     */
    @Test
    @Transactional
    void testMakeRecipeInsufficientPayment () {
        // Set up inventory
        final Map<String, Integer> inventoryIngredients = new HashMap<>();
        inventoryIngredients.put( "Coffee", 15 );
        inventoryIngredients.put( "Milk", 10 );
        final InventoryDto inventoryDto = new InventoryDto( inventoryIngredients );
        inventoryService.createInventory( inventoryDto );

        // Set up recipe
        final Map<String, Integer> recipeIngredients = new HashMap<>();
        recipeIngredients.put( "Coffee", 2 );
        final RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName( "Expensive Coffee" );
        recipeDto.setPrice( 100 );
        recipeDto.setIngredients( recipeIngredients );
        final RecipeDto createdRecipe = recipeService.createRecipe( recipeDto );

        // Test insufficient payment
        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> {
            makeRecipeService.makeRecipe( createdRecipe.getId(), "50" );
        } );

        assertEquals( "Insufficient Payment", exception.getMessage() );
    }

    /**
     * Test method for invalid payment (non-numeric) alternative flow.
     */
    @Test
    @Transactional
    void testMakeRecipeInvalidPayment () {
        // Set up inventory
        final Map<String, Integer> inventoryIngredients = new HashMap<>();
        inventoryIngredients.put( "Coffee", 15 );
        final InventoryDto inventoryDto = new InventoryDto( inventoryIngredients );
        inventoryService.createInventory( inventoryDto );

        // Set up recipe
        final Map<String, Integer> recipeIngredients = new HashMap<>();
        recipeIngredients.put( "Coffee", 1 );
        final RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName( "Simple Coffee" );
        recipeDto.setPrice( 25 );
        recipeDto.setIngredients( recipeIngredients );
        final RecipeDto createdRecipe = recipeService.createRecipe( recipeDto );

        // Test invalid payment (non-numeric)
        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> {
            makeRecipeService.makeRecipe( createdRecipe.getId(), "abc" );
        } );

        assertEquals( "Invalid Payment", exception.getMessage() );
    }

    /**
     * Test method for insufficient ingredients alternative flow.
     */
    @Test
    @Transactional
    void testMakeRecipeInsufficientIngredients () {
        // Set up inventory with insufficient ingredients
        final Map<String, Integer> inventoryIngredients = new HashMap<>();
        inventoryIngredients.put( "Coffee", 1 );
        inventoryIngredients.put( "Milk", 1 );
        final InventoryDto inventoryDto = new InventoryDto( inventoryIngredients );
        inventoryService.createInventory( inventoryDto );

        // Set up recipe requiring more ingredients than available
        final Map<String, Integer> recipeIngredients = new HashMap<>();
        recipeIngredients.put( "Coffee", 5 );
        recipeIngredients.put( "Milk", 3 );
        final RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName( "Large Latte" );
        recipeDto.setPrice( 50 );
        recipeDto.setIngredients( recipeIngredients );
        final RecipeDto createdRecipe = recipeService.createRecipe( recipeDto );

        // Test insufficient ingredients
        final InvalidIngredientAmountException exception = assertThrows( InvalidIngredientAmountException.class, () -> {
            makeRecipeService.makeRecipe( createdRecipe.getId(), "100" );
        } );

        assertEquals( "Insufficient Ingredients", exception.getMessage() );
    }

    /**
     * Test method for recipe not found alternative flow.
     */
    @Test
    @Transactional
    void testMakeRecipeNotFound () {
        // Set up inventory (not used in this test but needed for service)
        final Map<String, Integer> inventoryIngredients = new HashMap<>();
        inventoryIngredients.put( "Coffee", 15 );
        final InventoryDto inventoryDto = new InventoryDto( inventoryIngredients );
        inventoryService.createInventory( inventoryDto );

        // Test with non-existent recipe ID
        final ResourceNotFoundException exception = assertThrows( ResourceNotFoundException.class, () -> {
            makeRecipeService.makeRecipe( 999L, "100" );
        } );

        assertEquals( "Recipe not found", exception.getMessage() );
    }

}
