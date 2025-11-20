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

import edu.ncsu.csc326.wolfcafe.entity.Item;

/**
 * Tests Recipe repository
 */
@DataJpaTest
@AutoConfigureTestDatabase ( replace = Replace.NONE )
class ItemRepositoryTest {

    /** Reference to recipe repository */
    @Autowired
    private ItemRepository itemRepository;

    /** Coffee recipe */
    private Item           item1;
    /** Latte recipe */
    private Item           item2;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        itemRepository.deleteAll();

        // Setup Coffee recipe
        final Map<String, Integer> coffeeIngredients = Map.of( "Coffee", 3 );
        item1 = new Item( null, "Coffee", "Hot coffee", 5.0, coffeeIngredients );

        // Setup Latte recipe
        final Map<String, Integer> latteIngredients = Map.of( "Coffee", 3, "Milk", 2, "Sugar", 1 );
        item2 = new Item( null, "Latte", "Iced latte", 10.0, latteIngredients );

        itemRepository.save( item1 );
        itemRepository.save( item2 );
    }

    /**
     * Tests retrieving the Coffee recipe by name. Verifies that the correct
     * values are retrieved for the price and number of each ingredient.
     */
    @Test
    public void testGetCoffeeItemByName () {
        final Optional<Item> item = itemRepository.findByName( "Coffee" );
        final Item actualItem = item.get();
        assertAll( "Item contents", () -> assertEquals( "Coffee", actualItem.getName() ),
                () -> assertEquals( 5.0, actualItem.getPrice() ),
                () -> assertEquals( 3, actualItem.getIngredients().get( "Coffee" ) ) );
    }

    /**
     * Tests retrieving the Latte recipe by name. Verifies that the correct
     * values are retrieved for the price and number of each ingredient.
     */
    @Test
    public void testGetLatteItemByName () {
        final Optional<Item> item = itemRepository.findByName( "Latte" );
        final Item actualItem = item.get();
        assertAll( "Item contents", () -> assertEquals( "Latte", actualItem.getName() ),
                () -> assertEquals( 10.0, actualItem.getPrice() ),
                () -> assertEquals( 3, actualItem.getIngredients().get( "Coffee" ) ),
                () -> assertEquals( 2, actualItem.getIngredients().get( "Milk" ) ),
                () -> assertEquals( 1, actualItem.getIngredients().get( "Sugar" ) ) );
    }

    /**
     * Tests retrieving an invalid recipe by name. Verifies that an empty recipe
     * is returned.
     */
    @Test
    public void testGetItemByNameInvalid () {
        final Optional<Item> item = itemRepository.findByName( "Unknown" );
        assertTrue( item.isEmpty() );
    }

}
