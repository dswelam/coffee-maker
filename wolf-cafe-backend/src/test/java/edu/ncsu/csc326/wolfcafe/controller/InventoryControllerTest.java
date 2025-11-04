package edu.ncsu.csc326.wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
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
import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import jakarta.persistence.EntityManager;

/**
 * Tests InventoryController
 *
 * @author Dania Swelam
 */
@SpringBootTest
@AutoConfigureMockMvc
public class InventoryControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc       mvc;

    /** Reference to EntityManager */
    @Autowired
    private EntityManager entityManager;

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
     * Tests the GET /api/inventory endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @WithMockUser ( username = "staff", roles = "STAFF" )
    @Transactional
    public void testGetInventory () throws Exception {
        final Map<String, Integer> emptyIngredients = new HashMap<>();
        final InventoryDto expectedEmpty = new InventoryDto( 1L, emptyIngredients );

        mvc.perform( get( "/api/inventory" ) ).andExpect( content().json( TestUtils.asJsonString( expectedEmpty ) ) )
                .andExpect( status().isOk() );

        final Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put( "Coffee", 10 );
        ingredients.put( "Milk", 20 );
        ingredients.put( "Sugar", 30 );
        ingredients.put( "Chocolate", 40 );

        final InventoryDto expectedInventory = new InventoryDto( 1L, ingredients );

        mvc.perform( put( "/api/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( expectedInventory ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

        final String inventory = mvc.perform( get( "/api/inventory" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.ingredients.Coffee" ).value( "10" ) )
                .andExpect( jsonPath( "$.ingredients.Milk" ).value( "20" ) )
                .andExpect( jsonPath( "$.ingredients.Sugar" ).value( "30" ) )
                .andExpect( jsonPath( "$.ingredients.Chocolate" ).value( "40" ) ).andReturn().getResponse()
                .getContentAsString();

        assertTrue( inventory.contains( "Coffee" ) );
        assertTrue( inventory.contains( "Milk" ) );
        assertTrue( inventory.contains( "Sugar" ) );
        assertTrue( inventory.contains( "Chocolate" ) );
        assertTrue( inventory.contains( "10" ) );
        assertTrue( inventory.contains( "20" ) );
        assertTrue( inventory.contains( "30" ) );
        assertTrue( inventory.contains( "40" ) );

    }

    /**
     * Tests the PUT /api/inventory endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    @Transactional
    public void testUpdateInventory () throws Exception {
        mvc.perform( get( "/api/inventory" ) ).andExpect( status().isOk() );

        final Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put( "coffee", 5 );
        ingredients.put( "milk", 10 );
        ingredients.put( "sugar", 15 );
        ingredients.put( "chocolate", 20 );

        final InventoryDto updatedInventory = new InventoryDto( 1L, ingredients );

        mvc.perform( put( "/api/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( updatedInventory ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.ingredients.coffee" ).value( "5" ) )
                .andExpect( jsonPath( "$.ingredients.milk" ).value( "10" ) )
                .andExpect( jsonPath( "$.ingredients.sugar" ).value( "15" ) )
                .andExpect( jsonPath( "$.ingredients.chocolate" ).value( "20" ) );

        final String inventory = mvc.perform( get( "/api/inventory" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertTrue( inventory.contains( "coffee" ) );
        assertTrue( inventory.contains( "milk" ) );
        assertTrue( inventory.contains( "sugar" ) );
        assertTrue( inventory.contains( "chocolate" ) );
        assertTrue( inventory.contains( "5" ) );
        assertTrue( inventory.contains( "10" ) );
        assertTrue( inventory.contains( "15" ) );
        assertTrue( inventory.contains( "20" ) );

    }

}
