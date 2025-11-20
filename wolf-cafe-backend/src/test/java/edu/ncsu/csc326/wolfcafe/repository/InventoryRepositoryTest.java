package edu.ncsu.csc326.wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import jakarta.persistence.EntityManager;

/**
 * Tests InventoryRepository. Uses the real database - not an embedded one.
 */
@DataJpaTest
@ActiveProfiles ( "test" )
@AutoConfigureTestDatabase ( replace = Replace.NONE )
public class InventoryRepositoryTest {

    /** Reference to inventory repository */
    @Autowired
    private InventoryRepository inventoryRepository;

    /** Reference to EntityManager */
    @Autowired
    private TestEntityManager   testEntityManager;

    /** Reference to inventory */
    private Inventory           inventory;

    /**
     * Sets up the test case. We assume only one inventory row.
     *
     * @throws java.lang.Exception
     *             if error
     *
     *             Source: https://www.geeksforgeeks.org/sql/
     *             truncate-tables-with-dependent-foreign-key-constraints-in-sql/
     */
    @BeforeEach
    public void setUp () throws Exception {
        EntityManager entityManager = testEntityManager.getEntityManager();
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 0" ).executeUpdate();
        entityManager.createNativeQuery( "TRUNCATE TABLE inventory" ).executeUpdate();
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 1" ).executeUpdate();

        // Make sure that Inventory always has an id of 1L.
        Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put( "coffee", 20 );
        ingredients.put( "milk", 14 );
        ingredients.put( "sugar", 32 );
        ingredients.put( "chocolate", 10 );
        inventory = new Inventory( ingredients );
        inventoryRepository.save( inventory );
    }

    /**
     * Test saving the inventory and retrieving from the repository.
     */
    @Test
    public void testSaveAndGetInventory () {
        Inventory fetchedInventory = inventoryRepository.findById( inventory.getId() ).get();
        assertEquals( 20, fetchedInventory.getIngredients().get( "coffee" ) );
        assertEquals( 14, fetchedInventory.getIngredients().get( "milk" ) );
        assertEquals( 32, fetchedInventory.getIngredients().get( "sugar" ) );
        assertEquals( 10, fetchedInventory.getIngredients().get( "chocolate" ) );
    }

    /**
     * Tests updating the inventory
     */
    @Test
    public void testUpdateInventory () {
        Inventory fetchedInventory = inventoryRepository.findById( inventory.getId() ).get();
        fetchedInventory.getIngredients().put( "coffee", 13 );
        fetchedInventory.getIngredients().put( "milk", 14 );
        fetchedInventory.getIngredients().put( "sugar", 27 );

        Inventory updatedInventory = inventoryRepository.save( fetchedInventory );
        assertEquals( 13, updatedInventory.getIngredients().get( "coffee" ) );
        assertEquals( 14, updatedInventory.getIngredients().get( "milk" ) );
        assertEquals( 27, updatedInventory.getIngredients().get( "sugar" ) );
        assertEquals( 10, updatedInventory.getIngredients().get( "chocolate" ) );
    }
}
