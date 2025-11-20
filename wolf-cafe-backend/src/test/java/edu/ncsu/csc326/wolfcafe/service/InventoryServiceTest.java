package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.exception.InvalidIngredientAmountException;
import jakarta.persistence.EntityManager;

/**
 * Tests InventoryServiceImpl.
 */
@SpringBootTest
@ActiveProfiles ( "test" )
public class InventoryServiceTest {

    /** Reference to InventoryService (and InventoryServiceImpl). */
    @Autowired
    private InventoryService inventoryService;

    /** Reference to EntityManager */
    @Autowired
    private EntityManager    entityManager;

    /**
     * Sets up the test case. We assume only one inventory row. Because
     * inventory is treated as a singleton (only one row), we must truncate for
     * auto increment on the id to work correctly.
     *
     * @throws java.lang.Exception
     *             if error
     *
     *             Source: https://www.geeksforgeeks.org/sql/
     *             truncate-tables-with-dependent-foreign-key-constraints-in-sql/
     */
    @BeforeEach
    public void setUp () throws Exception {
        entityManager.createNativeQuery( "DELETE FROM inventory_ingredients" ).executeUpdate();
        entityManager.createNativeQuery( "DELETE FROM inventory" ).executeUpdate();
        entityManager.createNativeQuery( "ALTER TABLE inventory AUTO_INCREMENT = 1" ).executeUpdate();
    }

    /**
     * Tests InventoryService.createInventory().
     */
    @Test
    @Transactional
    public void testCreateInventory () {
        final Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put( "Coffee", 5 );
        ingredients.put( "Milk", 9 );
        ingredients.put( "Sugar", 14 );
        ingredients.put( "Chocolate", 23 );

        final InventoryDto inventoryDto = new InventoryDto( ingredients );

        final InventoryDto createdInventoryDto = inventoryService.createInventory( inventoryDto );
        // Check contents of returned InventoryDto
        assertAll( "InventoryDto contents",
                () -> assertEquals( 5, createdInventoryDto.getIngredients().get( "Coffee" ) ),
                () -> assertEquals( 9, createdInventoryDto.getIngredients().get( "Milk" ) ),
                () -> assertEquals( 14, createdInventoryDto.getIngredients().get( "Sugar" ) ),
                () -> assertEquals( 23, createdInventoryDto.getIngredients().get( "Chocolate" ) ) );

        final InventoryDto inventory = inventoryService.getInventory();
        assertEquals( createdInventoryDto.getId(), inventory.getId() );
    }

    /**
     * Tests InventoryService.updateInventory()
     */
    @Test
    @Transactional
    public void testUpdateInventory () {
        final InventoryDto inventoryDto = inventoryService.getInventory();

        final Map<String, Integer> initialIngredients = new HashMap<>();
        initialIngredients.put( "Coffee", 35 );
        initialIngredients.put( "Milk", 17 );
        initialIngredients.put( "Sugar", 12 );
        initialIngredients.put( "Chocolate", 14 );
        inventoryDto.setIngredients( initialIngredients );

        final InventoryDto updatedInventoryDto = inventoryService.updateInventory( inventoryDto );
        assertAll( "InventoryDto contents",
                () -> assertEquals( 35, updatedInventoryDto.getIngredients().get( "Coffee" ) ),
                () -> assertEquals( 17, updatedInventoryDto.getIngredients().get( "Milk" ) ),
                () -> assertEquals( 12, updatedInventoryDto.getIngredients().get( "Sugar" ) ),
                () -> assertEquals( 14, updatedInventoryDto.getIngredients().get( "Chocolate" ) ) );

        final Map<String, Integer> additionalIngredients = new HashMap<>();
        additionalIngredients.put( "Coffee", 5 );
        additionalIngredients.put( "Milk", 6 );
        additionalIngredients.put( "Sugar", 7 );
        additionalIngredients.put( "Chocolate", 8 );
        inventoryDto.setIngredients( additionalIngredients );

        final InventoryDto updatedInventoryDto2 = inventoryService.updateInventory( inventoryDto );
        assertAll( "InventoryDto contents",
                () -> assertEquals( 40, updatedInventoryDto2.getIngredients().get( "Coffee" ) ),
                () -> assertEquals( 23, updatedInventoryDto2.getIngredients().get( "Milk" ) ),
                () -> assertEquals( 19, updatedInventoryDto2.getIngredients().get( "Sugar" ) ),
                () -> assertEquals( 22, updatedInventoryDto2.getIngredients().get( "Chocolate" ) ) );

        final Map<String, Integer> invalidIngredients = new HashMap<>();
        invalidIngredients.put( "Coffee", -1 );
        inventoryDto.setIngredients( invalidIngredients );
        final InvalidIngredientAmountException ex = assertThrows( InvalidIngredientAmountException.class,
                () -> inventoryService.updateInventory( inventoryDto ) );
        assertEquals( ex.getMessage(), "Invalid amount for Coffee. Must be a positive integer" );

        final Map<String, Integer> nullIngredients = new HashMap<>();
        nullIngredients.put( "Milk", null );
        inventoryDto.setIngredients( nullIngredients );
        final InvalidIngredientAmountException ex2 = assertThrows( InvalidIngredientAmountException.class,
                () -> inventoryService.updateInventory( inventoryDto ) );
        assertEquals( ex2.getMessage(), "Invalid amount for Milk. Must be a positive integer" );
    }

    /**
     * Tests using ingredients and checking for enough ingredients.
     */
    @Test
    @Transactional
    void testUseAndCheckIngredients () {
        final Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put( "Coffee", 10 );
        ingredients.put( "Milk", 10 );
        ingredients.put( "Sugar", 10 );
        ingredients.put( "Chocolate", 10 );

        final InventoryDto inventoryDto = new InventoryDto( ingredients );
        inventoryService.createInventory( inventoryDto );

        // Create a recipe/item that uses some ingredients
        final Map<String, Integer> recipeIngredients = new HashMap<>();
        recipeIngredients.put( "Coffee", 4 );
        recipeIngredients.put( "Milk", 5 );
        recipeIngredients.put( "Sugar", 2 );

        final Item item = new Item();
        item.setIngredients( recipeIngredients );

        // Check if enough ingredients
        final boolean hasEnough = inventoryService.hasEnoughIngredients( item );
        assertEquals( true, hasEnough );

        // Use the ingredients
        inventoryService.useIngredients( item );

        final InventoryDto updatedInventory = inventoryService.getInventory();
        assertAll( "Updated InventoryDto contents",
                () -> assertEquals( 6, updatedInventory.getIngredients().get( "Coffee" ) ),
                () -> assertEquals( 5, updatedInventory.getIngredients().get( "Milk" ) ),
                () -> assertEquals( 8, updatedInventory.getIngredients().get( "Sugar" ) ),
                () -> assertEquals( 10, updatedInventory.getIngredients().get( "Chocolate" ) ) );

        // Check if enough ingredients for another item that requires more than
        // available
        final Map<String, Integer> largeRecipeIngredients = new HashMap<>();
        largeRecipeIngredients.put( "Coffee", 7 );
        largeRecipeIngredients.put( "Milk", 6 );

        final Item largeItem = new Item();
        largeItem.setIngredients( largeRecipeIngredients );

        final boolean hasEnoughForLargeItem = inventoryService.hasEnoughIngredients( largeItem );
        assertEquals( false, hasEnoughForLargeItem );
    }
}
