package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;

/**
 * Tests ItemServiceImpl
 */
@SpringBootTest
public class ItemServiceTest {

    /** Reference to ItemService */
    @Autowired
    private ItemService                       itemService;

    /** Reference to EntityManager */
    @Autowired
    private EntityManager                     entityManager;

    /** Item name */
    private static final String               ITEM_NAME        = "Coffee";
    /** Item description */
    private static final String               ITEM_DESCRIPTION = "Coffee is life";
    /** Item price */
    private static final double               ITEM_PRICE       = 3.25;
    /** Item ingredients */
    private static final Map<String, Integer> ITEM_INGREDIENTS = Map.of( "Espresso", 2 );

    /**
     * Sets up the test case.
     * @throws java.lang.Exception if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        // remove dependent rows first to satisfy FK constraints
        entityManager.createNativeQuery( "DELETE FROM item_ingredients" ).executeUpdate();
        entityManager.createNativeQuery( "DELETE FROM items" ).executeUpdate();

        // optional reset auto-increment for predictable ids in tests
        entityManager.createNativeQuery( "ALTER TABLE items AUTO_INCREMENT = 1" ).executeUpdate();
    }

    /**
     * Tests creating an item
     */
    @Test
    @Transactional
    void testAddItem () {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );
        itemDto.setIngredients( ITEM_INGREDIENTS );

        final ItemDto createdItemDto = itemService.addItem( itemDto );
        assertAll( "ItemDto contents", () -> assertEquals( ITEM_NAME, createdItemDto.getName() ),
                () -> assertEquals( ITEM_DESCRIPTION, createdItemDto.getDescription() ),
                () -> assertEquals( ITEM_PRICE, createdItemDto.getPrice() ),
                () -> assertEquals( ITEM_INGREDIENTS, createdItemDto.getIngredients() ) );
    }

    /**
     * Tests creating an item
     */
    @Test
    @Transactional
    void testGetItemById () {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );
        itemDto.setIngredients( ITEM_INGREDIENTS );

        final ItemDto createdItemDto = itemService.addItem( itemDto );

        final ItemDto retrievedItemDto = itemService.getItemById( createdItemDto.getId() );
        assertAll( "ItemDto contents", () -> assertEquals( ITEM_NAME, retrievedItemDto.getName() ),
                () -> assertEquals( ITEM_DESCRIPTION, retrievedItemDto.getDescription() ),
                () -> assertEquals( ITEM_PRICE, retrievedItemDto.getPrice() ),
                () -> assertEquals( ITEM_INGREDIENTS, retrievedItemDto.getIngredients() ) );
    }

    /**
     * Tests creating an item
     */
    @Test
    @Transactional
    void testGetItemException () {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );

        final ItemDto createdItemDto = itemService.addItem( itemDto );

        assertThrows( ResourceNotFoundException.class, () -> itemService.getItemById( createdItemDto.getId() + 1 ) );
    }

    /**
     * Test getting an item by name
     */
    @Test
    @Transactional
    void testGetItemByName () {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );
        itemDto.setIngredients( ITEM_INGREDIENTS );

        final ItemDto createdItemDto = itemService.addItem( itemDto );

        final ItemDto retrievedItemDto = itemService.getItemByName( createdItemDto.getName() );
        assertAll( "ItemDto contents", () -> assertEquals( ITEM_NAME, retrievedItemDto.getName() ),
                () -> assertEquals( ITEM_DESCRIPTION, retrievedItemDto.getDescription() ),
                () -> assertEquals( ITEM_PRICE, retrievedItemDto.getPrice() ),
                () -> assertEquals( ITEM_INGREDIENTS, retrievedItemDto.getIngredients() ) );
    }

    /**
     * Tests getting an item by name exception
     */
    @Test
    @Transactional
    void testGetItemByNameException () {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );

        final ItemDto createdItemDto = itemService.addItem( itemDto );

        assertThrows( ResourceNotFoundException.class,
                () -> itemService.getItemByName( createdItemDto.getName() + "DoesNotExist" ) );
    }

    /**
     * Tests getting all items
     */
    @Test
    @Transactional
    void testGetAllItems () {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );

        itemService.addItem( itemDto );

        final var items = itemService.getAllItems();
        assertEquals( 1, items.size() );
    }

    /**
     * Tests creating an item
     */
    @Test
    @Transactional
    void testUpdateItem () {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );

        final ItemDto createdItemDto = itemService.addItem( itemDto );

        final ItemDto itemDto2 = new ItemDto();
        itemDto2.setName( "Latte" );
        itemDto2.setDescription( "A yummy beverage" );
        itemDto2.setPrice( 3.57 );

        final ItemDto updatedItemDto = itemService.updateItem( createdItemDto.getId(), itemDto2 );
        assertAll( "ItemDto contents", () -> assertEquals( "Latte", updatedItemDto.getName() ),
                () -> assertEquals( "A yummy beverage", updatedItemDto.getDescription() ),
                () -> assertEquals( 3.57, updatedItemDto.getPrice() ) );
    }

    /**
     * Tests creating an item
     */
    @Test
    @Transactional
    void testUpdateItemException () {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );

        final ItemDto createdItemDto = itemService.addItem( itemDto );

        final ItemDto itemDto2 = new ItemDto();
        itemDto2.setName( "Latte" );
        itemDto2.setDescription( "A yummy beverage" );
        itemDto2.setPrice( 3.57 );

        assertThrows( ResourceNotFoundException.class,
                () -> itemService.updateItem( createdItemDto.getId() + 1, itemDto2 ) );
    }

    /**
     * Tests creating an item
     */
    @Test
    @Transactional
    void testDeleteItem () {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );

        final ItemDto createdItemDto = itemService.addItem( itemDto );

        itemService.deleteItem( createdItemDto.getId() );
        assertThrows( ResourceNotFoundException.class, () -> itemService.getItemById( createdItemDto.getId() ) );
    }

    /**
     * Tests duplicate name check
     */
    @Test
    @Transactional
    void testIsDuplicateName () {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );

        itemService.addItem( itemDto );

        final boolean isDuplicate = itemService.isDuplicateName( ITEM_NAME );
        assertEquals( true, isDuplicate );

        final boolean isNotDuplicate = itemService.isDuplicateName( "Latte" );
        assertEquals( false, isNotDuplicate );
    }
}
