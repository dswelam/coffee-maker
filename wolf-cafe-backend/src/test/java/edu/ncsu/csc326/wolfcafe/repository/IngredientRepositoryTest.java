package edu.ncsu.csc326.wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import jakarta.transaction.Transactional;

/**
 * Test for IngredientRepository
 *
 * @author Nora Cam (nncam)
 */
@DataJpaTest
@AutoConfigureTestDatabase ( replace = Replace.NONE )
class IngredientRepositoryTest {

    /** Repository under test */
    @Autowired
    private IngredientRepository ingredientRepository;

    /** Test ingredients */
    private Long                 ingredient1Id;

    /** Test ingredients */
    private Long                 ingredient2Id;

    /** Test ingredients */
    private final String         ingredient1Name = "Tea";

    /** Test ingredients */
    private final String         ingredient2Name = "Honey";

    @BeforeEach
    public void setUp () throws Exception {
        ingredientRepository.deleteAll();

        final Ingredient ingredient1 = new Ingredient( ingredient1Name );
        final Ingredient ingredient2 = new Ingredient( ingredient2Name );

        ingredient1Id = ingredientRepository.save( ingredient1 ).getId();
        ingredient2Id = ingredientRepository.save( ingredient2 ).getId();

        System.out.println( ingredient1Id + " " + ingredient2Id );
    }

    @Test
    @Transactional
    public void testAddIngredients () {
        final Ingredient i1 = ingredientRepository.findById( ingredient1Id ).get();
        assertAll( "Ingredient contents", () -> assertEquals( ingredient1Id, i1.getId() ),
                () -> assertEquals( ingredient1Name, i1.getName() ) );

        final Ingredient i2 = ingredientRepository.findById( ingredient2Id ).get();
        assertAll( "Ingredient contents", () -> assertEquals( ingredient2Id, i2.getId() ),
                () -> assertEquals( ingredient2Name, i2.getName() ) );
    }

    @Test
    @Transactional
    public void testGetIngredientByNameSuceeds () {
        final Ingredient i1 = ingredientRepository.findById( ingredient1Id ).get();
        assertAll( "Ingredient contents", () -> assertEquals( ingredient1Id, i1.getId() ),
                () -> assertEquals( ingredient1Name, i1.getName() ) );

        final Ingredient i2 = ingredientRepository.findById( ingredient2Id ).get();
        assertAll( "Ingredient contents", () -> assertEquals( ingredient2Id, i2.getId() ),
                () -> assertEquals( ingredient2Name, i2.getName() ) );
    }

    @Test
    @Transactional
    public void testGetIngredientByNameFails () {
        final Optional<Ingredient> recipe = ingredientRepository.findByName( "Unknown" );
        assertTrue( recipe.isEmpty() );
    }

}
