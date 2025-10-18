package edu.ncsu.csc326.wolfcafe.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.RecipeDto;
import edu.ncsu.csc326.wolfcafe.repository.RecipeRepository;

/**
 * Test class for the RecipeController.
 *
 * Tests the REST API endpoints for managing recipes. Includes tests for
 * creating, retrieving, and deleting recipes, as well as handling edge cases
 * like duplicates and limits. Uses MockMvc to simulate HTTP requests and
 * validate responses. Database is reset before each test to ensure isolation.
 * Tests are transactional to roll back changes after each test.
 *
 * @author Dania Swelam
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RecipeControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc          mvc;

    /** Reference to recipe repository */
    @Autowired
    private RecipeRepository recipeRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        recipeRepository.deleteAll();
    }

    /**
     * Tests the GET /api/recipes endpoint with all recipes in list.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    @Transactional
    public void testGetRecipes () throws Exception {
        final RecipeDto recipeDto1 = new RecipeDto( "Mocha", 200,
                Map.of( "coffee", 2, "milk", 1, "sugar", 1, "chocolate", 3 ) );
        final RecipeDto recipeDto2 = new RecipeDto( "Latte", 150,
                Map.of( "coffee", 2, "milk", 3, "sugar", 1, "chocolate", 0 ) );

        // Create two recipes through the API
        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto1 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Mocha" ) )
                .andExpect( jsonPath( "$.price" ).value( "200" ) )
                .andExpect( jsonPath( "$.ingredients.coffee" ).value( 2 ) )
                .andExpect( jsonPath( "$.ingredients.milk" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.sugar" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.chocolate" ).value( 3 ) );

        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto2 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Latte" ) )
                .andExpect( jsonPath( "$.price" ).value( "150" ) )
                .andExpect( jsonPath( "$.ingredients.coffee" ).value( 2 ) )
                .andExpect( jsonPath( "$.ingredients.milk" ).value( 3 ) )
                .andExpect( jsonPath( "$.ingredients.sugar" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.chocolate" ).value( 0 ) );

        final String recipe = mvc.perform( get( "/api/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertTrue( recipe.contains( "Mocha" ) );
        assertTrue( recipe.contains( "200" ) );
        assertTrue( recipe.contains( "Latte" ) );
        assertTrue( recipe.contains( "150" ) );
        assertTrue( recipe.contains( "2" ) );
        assertTrue( recipe.contains( "1" ) );
    }

    /**
     * Tests the GET /api/recipes endpoint with one recipe in the database.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    @Transactional
    public void testGetSingleRecipe () throws Exception {
        final RecipeDto recipeDto = new RecipeDto( "Mocha", 200,
                Map.of( "coffee", 2, "milk", 1, "sugar", 1, "chocolate", 3 ) );

        // Create a recipe through the API
        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Mocha" ) )
                .andExpect( jsonPath( "$.price" ).value( "200" ) )
                .andExpect( jsonPath( "$.ingredients.coffee" ).value( 2 ) )
                .andExpect( jsonPath( "$.ingredients.milk" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.sugar" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.chocolate" ).value( 3 ) );

        final String recipe = mvc.perform( get( "/api/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        assertTrue( recipe.contains( "Mocha" ) );
        assertTrue( recipe.contains( "200" ) );
        assertTrue( recipe.contains( "2" ) );
        assertTrue( recipe.contains( "1" ) );
        assertTrue( recipe.contains( "3" ) );
        assertFalse( recipe.contains( "Latte" ) );
    }

    /**
     * Tests the POST /api/recipes endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    @Transactional
    public void testCreateRecipe () throws Exception {
        final RecipeDto recipeDto = new RecipeDto( "Mocha", 200,
                Map.of( "coffee", 2, "milk", 1, "sugar", 1, "chocolate", 3 ) );

        // Create a recipe through the API
        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Mocha" ) )
                .andExpect( jsonPath( "$.price" ).value( "200" ) )
                .andExpect( jsonPath( "$.ingredients.coffee" ).value( 2 ) )
                .andExpect( jsonPath( "$.ingredients.milk" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.sugar" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.chocolate" ).value( 3 ) );
        assertTrue( recipeRepository.findByName( "Mocha" ).isPresent() );
        assertFalse( recipeRepository.findByName( "Latte" ).isPresent() );
    }

    /**
     * Tests the POST /api/recipes endpoint for duplicate recipe names.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    @Transactional
    public void testCreateDuplicateRecipe () throws Exception {
        final RecipeDto recipeDto = new RecipeDto( "Mocha", 200,
                Map.of( "coffee", 2, "milk", 1, "sugar", 1, "chocolate", 3 ) );

        // Step 1: Create a recipe "Mocha" through the API
        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Mocha" ) )
                .andExpect( jsonPath( "$.price" ).value( "200" ) )
                .andExpect( jsonPath( "$.ingredients.coffee" ).value( 2 ) )
                .andExpect( jsonPath( "$.ingredients.milk" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.sugar" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.chocolate" ).value( 3 ) );

        // Step 2: Try to create another recipe "Mocha" through the API
        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isConflict() );
        assertTrue( recipeRepository.findByName( "Mocha" ).isPresent() );

        // Step 3: Make sure JSON has only one "Mocha"
        mvc.perform( get( "/api/recipes" ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$", hasSize( 1 ) ) )
                .andExpect( jsonPath( "$[*].name", hasItem( "Mocha" ) ) );
    }

    /**
     * Tests the POST /api/recipes endpoint for exceeding recipe limit.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    @Transactional
    public void testCreateExceedRecipeLimit () throws Exception {
        final RecipeDto recipeDto1 = new RecipeDto( "Mocha", 200,
                Map.of( "coffee", 2, "milk", 1, "sugar", 1, "chocolate", 3 ) );
        final RecipeDto recipeDto2 = new RecipeDto( "Latte", 150,
                Map.of( "coffee", 2, "milk", 3, "sugar", 1, "chocolate", 0 ) );
        final RecipeDto recipeDto3 = new RecipeDto( "Cappuccino", 250,
                Map.of( "coffee", 4, "milk", 3, "sugar", 2, "chocolate", 0 ) );
        final RecipeDto recipeDto4 = new RecipeDto( "Espresso", 100,
                Map.of( "coffee", 3, "milk", 0, "sugar", 0, "chocolate", 0 ) );

        // Create four recipes through the API
        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto1 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Mocha" ) )
                .andExpect( jsonPath( "$.price" ).value( "200" ) )
                .andExpect( jsonPath( "$.ingredients.coffee" ).value( 2 ) )
                .andExpect( jsonPath( "$.ingredients.milk" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.sugar" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.chocolate" ).value( 3 ) );

        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto2 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Latte" ) )
                .andExpect( jsonPath( "$.price" ).value( "150" ) )
                .andExpect( jsonPath( "$.ingredients.coffee" ).value( 2 ) )
                .andExpect( jsonPath( "$.ingredients.milk" ).value( 3 ) )
                .andExpect( jsonPath( "$.ingredients.sugar" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.chocolate" ).value( 0 ) );

        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto3 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Cappuccino" ) )
                .andExpect( jsonPath( "$.price" ).value( "250" ) )
                .andExpect( jsonPath( "$.ingredients.coffee" ).value( 4 ) )
                .andExpect( jsonPath( "$.ingredients.milk" ).value( 3 ) )
                .andExpect( jsonPath( "$.ingredients.sugar" ).value( 2 ) )
                .andExpect( jsonPath( "$.ingredients.chocolate" ).value( 0 ) );

        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto4 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isInsufficientStorage() );

        assertTrue( recipeRepository.findByName( "Mocha" ).isPresent() );
        assertTrue( recipeRepository.findByName( "Latte" ).isPresent() );
        assertTrue( recipeRepository.findByName( "Cappuccino" ).isPresent() );
        assertFalse( recipeRepository.findByName( "Espresso" ).isPresent() );
    }

    /**
     * Tests the POST /api/recipes endpoint followed by GET /api/recipes to
     * ensure the recipe was created.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    @Transactional
    public void testCreateAndGetRecipes () throws Exception {
        // Step 1: Create a new recipe
        final RecipeDto recipeDto = new RecipeDto( "Mocha", 200,
                Map.of( "coffee", 2, "milk", 1, "sugar", 1, "chocolate", 3 ) );

        // Step 2: Verify the recipe was created successfully
        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Mocha" ) )
                .andExpect( jsonPath( "$.price" ).value( "200" ) )
                .andExpect( jsonPath( "$.ingredients.coffee" ).value( 2 ) )
                .andExpect( jsonPath( "$.ingredients.milk" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.sugar" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.chocolate" ).value( 3 ) );

        // Step 3: Verify the recipe shows up in the list of recipes
        final String recipe = mvc.perform( get( "/api/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertTrue( recipe.contains( "Mocha" ) );
        assertTrue( recipe.contains( "200" ) );
        assertTrue( recipe.contains( "2" ) );
        assertTrue( recipe.contains( "1" ) );
        assertTrue( recipe.contains( "3" ) );
    }

    /**
     * Tests the GET /api/recipes/{name} endpoint for a non-existent recipe.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    @Transactional
    public void testGetNonExistentRecipe () throws Exception {
        mvc.perform( get( "/api/recipes/Mocha" ) ).andDo( print() ).andExpect( status().isNotFound() );
    }

    /**
     * Tests the PUT /api/recipes/{id} endpoint for updating a recipe.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    @Transactional
    public void testUpdateRecipe () throws Exception {
        // Create a valid recipe
        final RecipeDto recipeDto = new RecipeDto( "Mocha", 200,
                Map.of( "coffee", 2, "milk", 1, "sugar", 1, "chocolate", 3 ) );
        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

        final Long id = recipeRepository.findByName( "Mocha" ).get().getId();

        // Successful update
        final RecipeDto updatedDto = new RecipeDto( "Mocha", 250,
                Map.of( "coffee", 3, "milk", 2, "sugar", 1, "chocolate", 2 ) );
        mvc.perform( put( "/api/recipes/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( updatedDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.price" ).value( 250 ) )
                .andExpect( jsonPath( "$.ingredients.coffee" ).value( 3 ) );

        // Invalid price
        final RecipeDto invalidPriceDto = new RecipeDto( "Mocha", -10,
                Map.of( "coffee", 2, "milk", 1, "sugar", 1, "chocolate", 3 ) );
        mvc.perform( put( "/api/recipes/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalidPriceDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isBadRequest() );

        // Invalid unit
        final RecipeDto invalidUnitDto = new RecipeDto( "Mocha", 200,
                Map.of( "coffee", 0, "milk", 1, "sugar", 1, "chocolate", 3 ) );
        mvc.perform( put( "/api/recipes/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalidUnitDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isBadRequest() );

        // No ingredients
        final RecipeDto noIngredientsDto = new RecipeDto( "Mocha", 200, Map.of() );
        mvc.perform( put( "/api/recipes/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( noIngredientsDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isBadRequest() );

        // Recipe not found
        mvc.perform( put( "/api/recipes/99999" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( updatedDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );

        // Duplicate name
        final RecipeDto recipeDto2 = new RecipeDto( "Latte", 150,
                Map.of( "coffee", 2, "milk", 3, "sugar", 1, "chocolate", 0 ) );
        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto2 ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

        final RecipeDto duplicateNameDto = new RecipeDto( "Latte", 200,
                Map.of( "coffee", 2, "milk", 1, "sugar", 1, "chocolate", 3 ) );
        mvc.perform( put( "/api/recipes/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( duplicateNameDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isConflict() );
    }

    /**
     * Tests DELETE /api/recipes/{name} endpoint for deleting a recipe.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    @Transactional
    public void testDeleteRecipe () throws Exception {
        final RecipeDto recipeDto = new RecipeDto( "Mocha", 200,
                Map.of( "coffee", 2, "milk", 1, "sugar", 1, "chocolate", 3 ) );

        // Step 1: Create a new recipe
        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "Mocha" ) )
                .andExpect( jsonPath( "$.price" ).value( "200" ) )
                .andExpect( jsonPath( "$.ingredients.coffee" ).value( 2 ) )
                .andExpect( jsonPath( "$.ingredients.milk" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.sugar" ).value( 1 ) )
                .andExpect( jsonPath( "$.ingredients.chocolate" ).value( 3 ) );

        // Create recipe ID for deletion
        final Long id = recipeRepository.findByName( "Mocha" ).get().getId();

        // Step 2: Ensure the recipe exists
        mvc.perform( get( "/api/recipes/Mocha" ) ).andDo( print() ).andExpect( status().isOk() );
        assertTrue( recipeRepository.findByName( "Mocha" ).isPresent() );
        // Step 3: Delete the recipe
        mvc.perform( delete( "/api/recipes/" + id ) ).andDo( print() ).andExpect( status().isOk() );
        // Step 4: Ensure the recipe is deleted
        mvc.perform( get( "/api/recipes/Mocha" ) ).andDo( print() ).andExpect( status().isNotFound() );
        assertFalse( recipeRepository.findByName( "Mocha" ).isPresent() );
        assertFalse( recipeRepository.findById( id ).isPresent() );
    }
}
