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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.WolfCafeApplication;
import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;

/**
 * Test class for the IngredientController.
 *
 * Tests the REST API endpoints for managing ingredients. CRUD operations.
 *
 * @author Nora Cam (nncam)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( "test" )
@ContextConfiguration ( classes = WolfCafeApplication.class )
public class IngredientControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc              mvc;

    /** Reference to ingredient repository */
    @Autowired
    private IngredientRepository ingredientRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        ingredientRepository.deleteAll();
    }

    /**
     * Tests the GET /api/ingredients endpoint with all ingredients in list.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testGetIngredients () throws Exception {
        // Create three ingredients through the API
        mvc.perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new IngredientDto( "coffee" ) ) ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new IngredientDto( "tea" ) ) ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new IngredientDto( "milk" ) ) ) ).andExpect( status().isOk() );

        mvc.perform( get( "/api/ingredients" ) ).andDo( print() ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 3 ) ) );

        final String ingredient = mvc.perform( get( "/api/ingredients" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        assertTrue( ingredient.contains( "coffee" ) );
        assertTrue( ingredient.contains( "tea" ) );
        assertTrue( ingredient.contains( "milk" ) );
        assertFalse( ingredient.contains( "matcha" ) );
    }

    /**
     * Tests the GET /api/ingredients endpoint with one ingredient in the
     * database.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testGetSingleIngredient () throws Exception {
        final IngredientDto ingredientDto = new IngredientDto( "vanilla" );

        mvc.perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredientDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "vanilla" ) );

        final String ingredient = mvc.perform( get( "/api/ingredients" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        assertTrue( ingredient.contains( "vanilla" ) );
        assertFalse( ingredient.contains( "tea" ) );
    }

    /**
     * Tests the POST /api/ingredients endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testCreateIngredient () throws Exception {
        final IngredientDto ingredientDto = new IngredientDto( "coffee" );

        mvc.perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredientDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "coffee" ) );
        assertTrue( ingredientRepository.findByName( "coffee" ).isPresent() );
        assertFalse( ingredientRepository.findByName( "tea" ).isPresent() );
    }

    /**
     * Tests the POST /api/ingredients endpoint for duplicate ingredient names.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testCreateDuplicateIngredient () throws Exception {
        final IngredientDto ingredientDto = new IngredientDto( "coffee" );

        mvc.perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredientDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "coffee" ) );

        mvc.perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredientDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isConflict() );
        assertTrue( ingredientRepository.findByName( "coffee" ).isPresent() );

        mvc.perform( get( "/api/ingredients" ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$", hasSize( 1 ) ) )
                .andExpect( jsonPath( "$[*].name", hasItem( "coffee" ) ) );
    }

    /**
     * Tests the GET /api/ingredients/{name} endpoint for a non-existent
     * ingredient.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testGetNonExistentIngredient () throws Exception {
        mvc.perform( get( "/api/ingredients/coffee" ) ).andDo( print() ).andExpect( status().isNotFound() );
    }

    /**
     * Tests the POST /api/ingredients endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testUpdateIngredient () throws Exception {
        final IngredientDto ingredientDto = new IngredientDto( "coffee" );

        mvc.perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredientDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "coffee" ) );
        assertTrue( ingredientRepository.findByName( "coffee" ).isPresent() );
        assertFalse( ingredientRepository.findByName( "tea" ).isPresent() );

        final Long id = ingredientRepository.findByName( "coffee" ).get().getId();
        ingredientDto.setName( "tea" );

        mvc.perform( put( "/api/ingredients/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredientDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "tea" ) );
        assertTrue( ingredientRepository.findByName( "tea" ).isPresent() );
        assertFalse( ingredientRepository.findByName( "coffee" ).isPresent() );

        mvc.perform( put( "/api/ingredients/" + ( id + 1 ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredientDto ) ) ).andDo( print() )
                .andExpect( status().isNotFound() );
    }

    /**
     * Tests DELETE /api/ingredients/{name} endpoint for deleting a ingredient.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testDeleteIngredient () throws Exception {
        final IngredientDto ingredientDto = new IngredientDto( "coffee" );

        mvc.perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredientDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.name" ).value( "coffee" ) );

        final Long id = ingredientRepository.findByName( "coffee" ).get().getId();

        mvc.perform( get( "/api/ingredients/coffee" ) ).andDo( print() ).andExpect( status().isOk() );
        assertTrue( ingredientRepository.findByName( "coffee" ).isPresent() );

        mvc.perform( delete( "/api/ingredients/" + id ) ).andDo( print() ).andExpect( status().isOk() );

        mvc.perform( get( "/api/ingredients/coffee" ) ).andDo( print() ).andExpect( status().isNotFound() );
        assertFalse( ingredientRepository.findByName( "coffee" ).isPresent() );
        assertFalse( ingredientRepository.findById( id ).isPresent() );
    }
}
