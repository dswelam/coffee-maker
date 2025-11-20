package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;

/**
 * Test class for the IngredientService and its implementation
 *
 * @author Nora Cam (nncam)
 */
@SpringBootTest
@ActiveProfiles ( "test" )
public class IngredientServiceTest {

    /** The service being tested. */
    @Autowired
    private IngredientService    ingredientService;

    /** The ingredient repository. */
    @Autowired
    private IngredientRepository ingredientRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    void setUp () throws Exception {
        ingredientRepository.deleteAll();
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.IngredientService#createIngredient(edu.ncsu.csc326.coffee_maker.dto.IngredientDto)}.
     */
    @Test
    @Transactional
    void testCreateIngredient () {
        final IngredientDto ingredientDto = new IngredientDto( "coffee" );
        final IngredientDto savedIngredient = ingredientService.createIngredient( ingredientDto );
        assertAll( "Ingredient contents", () -> assertTrue( savedIngredient.getId() > 1L ),
                () -> assertEquals( "coffee", savedIngredient.getName() ) );

        final IngredientDto retrievedIngredient = ingredientService.getIngredientById( savedIngredient.getId() );
        assertAll( "Ingredient contents", () -> assertEquals( savedIngredient.getId(), retrievedIngredient.getId() ),
                () -> assertEquals( "coffee", retrievedIngredient.getName() ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.IngredientService#isDuplicateName(java.lang.String)}.
     */
    @Test
    @Transactional
    void testIsDuplicateName () {
        // create coffee ingredient
        final IngredientDto ingredientDto = new IngredientDto( "coffee" );
        ingredientService.createIngredient( ingredientDto );

        // check for duplicates
        assertTrue( ingredientService.isDuplicateName( "coffee" ) );
        assertFalse( ingredientService.isDuplicateName( "Latte" ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.IngredientService#getAllIngredients()}.
     */
    @Test
    @Transactional
    void testGetAllIngredients () {
        // create ingredients
        IngredientDto newIngredientDto = new IngredientDto( "coffee" );
        final IngredientDto coffeeIngredient = ingredientService.createIngredient( newIngredientDto );

        newIngredientDto = new IngredientDto( "tea" );
        final IngredientDto mochaIngredient = ingredientService.createIngredient( newIngredientDto );

        newIngredientDto = new IngredientDto( "sugar" );
        final IngredientDto latteIngredient = ingredientService.createIngredient( newIngredientDto );

        // get all ingredients
        final List<IngredientDto> ingredients = ingredientService.getAllIngredients();

        // validate ingredients
        assertEquals( 3, ingredients.size() );

        assertAll( "coffee ingredient contents",
                () -> assertEquals( coffeeIngredient.getId(), ingredients.get( 0 ).getId() ),
                () -> assertEquals( "coffee", ingredients.get( 0 ).getName() ) );

        assertAll( "Mocha ingredient contents",
                () -> assertEquals( mochaIngredient.getId(), ingredients.get( 1 ).getId() ),
                () -> assertEquals( "tea", ingredients.get( 1 ).getName() ) );

        assertAll( "Latte ingredient contents",
                () -> assertEquals( latteIngredient.getId(), ingredients.get( 2 ).getId() ),
                () -> assertEquals( "sugar", ingredients.get( 2 ).getName() ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.IngredientService#updateIngredient(java.lang.Long, edu.ncsu.csc326.coffee_maker.dto.IngredientDto)}.
     */
    @Test
    @Transactional
    void testUpdateIngredient () {
        // create ingredient
        final IngredientDto ingredientDto = new IngredientDto( "coffee" );
        final IngredientDto savedIngredient = ingredientService.createIngredient( ingredientDto );

        // update ingredient
        ingredientService.updateIngredient( savedIngredient.getId(), new IngredientDto( "boba" ) );

        // check
        final IngredientDto newIngredient = ingredientService.getIngredientById( savedIngredient.getId() );
        assertAll( "Ingredient contents", () -> assertEquals( savedIngredient.getId(), newIngredient.getId() ),
                () -> assertEquals( "boba", newIngredient.getName() ) );

        assertThrows( ResourceNotFoundException.class,
                () -> ingredientService.updateIngredient( 0L, new IngredientDto( "coffee" ) ) );

    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.IngredientService#deleteIngredient(java.lang.Long)}.
     */
    @Test
    @Transactional
    void testDeleteIngredient () {
        // create ingredient
        final IngredientDto ingredientDto = new IngredientDto( "coffee" );
        final IngredientDto savedIngredient = ingredientService.createIngredient( ingredientDto );

        // delete ingredient
        ingredientService.deleteIngredient( savedIngredient.getId() );

        // validate deletion
        assertThrows( ResourceNotFoundException.class,
                () -> ingredientService.getIngredientById( savedIngredient.getId() ) );
        assertThrows( ResourceNotFoundException.class,
                () -> ingredientService.deleteIngredient( savedIngredient.getId() ) );
    }

}
