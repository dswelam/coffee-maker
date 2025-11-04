package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.WolfCafeApplication;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.service.ItemService;

/**
 * Tests the ItemController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration ( classes = WolfCafeApplication.class )
public class ItemControllerTest {

    /** Mocked MVC */
    @Autowired
    private MockMvc                           mvc;

    /** Item Service */
    @MockitoBean
    private ItemService                       itemService;

    /** Object mapper */
    private static final ObjectMapper         MAPPER           = new ObjectMapper();

    /** API path */
    private static final String               API_PATH         = "/api/items";
    /** Encoding */
    private static final String               ENCODING         = "utf-8";
    /** Item name */
    private static final String               ITEM_NAME        = "Coffee";
    /** Item description */
    private static final String               ITEM_DESCRIPTION = "Coffee is life";
    /** Item price */
    private static final double               ITEM_PRICE       = 3.25;
    /** Item ingredients */
    private static final Map<String, Integer> ITEM_INGREDIENTS = Map.of( "Espresso", 2 );

    /**
     * Test adding an item
     *
     * @throws Exception
     *             if error
     */
    @Test
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testAddItem () throws Exception {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );
        itemDto.setIngredients( ITEM_INGREDIENTS );

        Mockito.when( itemService.addItem( ArgumentMatchers.any() ) ).thenReturn( itemDto );

        final String json = MAPPER.writeValueAsString( itemDto );

        mvc.perform( post( API_PATH ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                .andExpect( jsonPath( "$.name", Matchers.equalTo( ITEM_NAME ) ) )
                .andExpect( jsonPath( "$.description", Matchers.equalTo( ITEM_DESCRIPTION ) ) )
                .andExpect( jsonPath( "$.price", Matchers.equalTo( ITEM_PRICE ) ) )
                .andExpect( jsonPath( "$.ingredients.Espresso", Matchers.equalTo( 2 ) ) );
    }

    /**
     * Tests trying to create an item if the user role is incorrect
     *
     * @throws Exception
     *             if error
     */
    @Test
    public void testAddItemNotAdmin () throws Exception {
        // Create ItemDto with all contents but the id
        final ItemDto itemDto = new ItemDto();
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );
        itemDto.setIngredients( ITEM_INGREDIENTS );

        Mockito.when( itemService.addItem( ArgumentMatchers.any() ) ).thenReturn( itemDto );

        final String json = MAPPER.writeValueAsString( itemDto );

        mvc.perform( post( API_PATH ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isUnauthorized() );
    }

    /**
     * Tests getting the item as a staff member
     *
     * @throws Exception
     *             if error
     */
    @Test
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testGetItem () throws Exception {
        final ItemDto itemDto = new ItemDto();
        itemDto.setId( 27L );
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );
        itemDto.setIngredients( ITEM_INGREDIENTS );

        Mockito.when( itemService.getItemById( ArgumentMatchers.any() ) ).thenReturn( itemDto );
        final String json = "";

        mvc.perform( get( API_PATH + "/27" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.id", Matchers.equalTo( 27 ) ) )
                .andExpect( jsonPath( "$.name", Matchers.equalTo( ITEM_NAME ) ) )
                .andExpect( jsonPath( "$.description", Matchers.equalTo( ITEM_DESCRIPTION ) ) )
                .andExpect( jsonPath( "$.price", Matchers.equalTo( ITEM_PRICE ) ) )
                .andExpect( jsonPath( "$.ingredients.Espresso", Matchers.equalTo( 2 ) ) );
    }

    /**
     * Tests getting all items as an admin
     *
     * @throws Exception
     *             if error
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testGetAllItems () throws Exception {
        final ItemDto itemDto1 = new ItemDto();
        itemDto1.setId( 1L );
        itemDto1.setName( ITEM_NAME );
        itemDto1.setDescription( ITEM_DESCRIPTION );
        itemDto1.setPrice( ITEM_PRICE );
        itemDto1.setIngredients( ITEM_INGREDIENTS );

        final ItemDto itemDto2 = new ItemDto();
        itemDto2.setId( 2L );
        itemDto2.setName( "Water" );
        itemDto2.setDescription( "Bottle of water 500mL" );
        itemDto2.setPrice( 4.00 );
        itemDto2.setIngredients( Map.of( "Water Bottle", 2 ) );

        Mockito.when( itemService.getAllItems() ).thenReturn( java.util.List.of( itemDto1, itemDto2 ) );
        final String json = "";

        mvc.perform( get( API_PATH ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", Matchers.hasSize( 2 ) ) )
                .andExpect( jsonPath( "$[0].id", Matchers.equalTo( 1 ) ) )
                .andExpect( jsonPath( "$[0].name", Matchers.equalTo( "Coffee" ) ) )
                .andExpect( jsonPath( "$[1].id", Matchers.equalTo( 2 ) ) )
                .andExpect( jsonPath( "$[1].name", Matchers.equalTo( "Water" ) ) );
    }

    /**
     * Tests updating an item as an admin
     *
     * @throws Exception
     *             if error
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testUpdateItem () throws Exception {
        final ItemDto itemDto = new ItemDto();
        itemDto.setId( 1L );
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );
        itemDto.setIngredients( ITEM_INGREDIENTS );

        Mockito.when( itemService.updateItem( ArgumentMatchers.eq( 1L ), ArgumentMatchers.any() ) )
                .thenReturn( itemDto );

        final String json = MAPPER.writeValueAsString( itemDto );

        mvc.perform( put( API_PATH + "/1" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.id", Matchers.equalTo( 1 ) ) )
                .andExpect( jsonPath( "$.name", Matchers.equalTo( ITEM_NAME ) ) )
                .andExpect( jsonPath( "$.description", Matchers.equalTo( ITEM_DESCRIPTION ) ) )
                .andExpect( jsonPath( "$.price", Matchers.equalTo( ITEM_PRICE ) ) )
                .andExpect( jsonPath( "$.ingredients.Espresso", Matchers.equalTo( 2 ) ) );
    }

    /**
     * Test deleting an item
     *
     * @throws Exception
     *             if error
     */
    @Test
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testDeleteItem () throws Exception {
        final ItemDto itemDto = new ItemDto();
        itemDto.setId( 27L );
        itemDto.setName( ITEM_NAME );
        itemDto.setDescription( ITEM_DESCRIPTION );
        itemDto.setPrice( ITEM_PRICE );
        itemDto.setIngredients( ITEM_INGREDIENTS );

        mvc.perform( post( API_PATH ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( itemDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().is2xxSuccessful() );

        mvc.perform( delete( API_PATH + "/27" ) ).andDo( print() ).andExpect( status().isOk() );
    }

    /**
     * Tests updateItem returns 400 for invalid price
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testUpdateItemInvalidPrice () throws Exception {
        final ItemDto itemDto = new ItemDto( 1L, "Mocha", "Chocolate coffee", -2.0, Map.of( "Espresso", 1 ) );

        // Simulate service throwing exception
        Mockito.when( itemService.updateItem( ArgumentMatchers.eq( 1L ), ArgumentMatchers.any() ) )
                .thenThrow( new IllegalArgumentException( "Invalid Price: must be a positive value." ) );

        final String json = MAPPER.writeValueAsString( itemDto );

        mvc.perform( put( API_PATH + "/1" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ) ).andExpect( status().isBadRequest() )
                .andExpect( jsonPath( "$", Matchers.containsString( "Invalid Price" ) ) );
    }

    /**
     * Tests updateItem returns 400 for invalid units
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testUpdateItemInvalidUnits () throws Exception {
        final ItemDto itemDto = new ItemDto( 1L, "Latte", "Milk coffee", 4.0, Map.of( "Milk", -1 ) );

        Mockito.when( itemService.updateItem( ArgumentMatchers.eq( 1L ), ArgumentMatchers.any() ) ).thenThrow(
                new IllegalArgumentException( "Invalid Unit: all ingredient amounts must be positive integers." ) );

        final String json = MAPPER.writeValueAsString( itemDto );

        mvc.perform( put( API_PATH + "/1" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ) ).andExpect( status().isBadRequest() )
                .andExpect( jsonPath( "$", Matchers.containsString( "Invalid Unit" ) ) );
    }

    /**
     * Tests updateItem returns 400 for no ingredients
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testUpdateItemNoIngredients () throws Exception {
        final ItemDto itemDto = new ItemDto( 1L, "Cappuccino", "Foamy coffee", 3.5, Map.of() );

        Mockito.when( itemService.updateItem( ArgumentMatchers.eq( 1L ), ArgumentMatchers.any() ) ).thenThrow(
                new IllegalArgumentException( "No Ingredients: item must include at least one ingredient." ) );

        final String json = MAPPER.writeValueAsString( itemDto );

        mvc.perform( put( API_PATH + "/1" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ) ).andExpect( status().isBadRequest() )
                .andExpect( jsonPath( "$", Matchers.containsString( "No Ingredients" ) ) );
    }

    /**
     * Tests updateItem returns 409 when item does not exist
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testUpdateItemNotFound () throws Exception {
        final ItemDto itemDto = new ItemDto( 999L, "Nonexistent", "Missing item", 5.0, Map.of( "Espresso", 1 ) );

        Mockito.when( itemService.updateItem( ArgumentMatchers.eq( 999L ), ArgumentMatchers.any() ) ).thenThrow(
                new edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException( "Item not found with id 999" ) );

        final String json = MAPPER.writeValueAsString( itemDto );

        mvc.perform( put( API_PATH + "/999" ).contentType( MediaType.APPLICATION_JSON ).characterEncoding( ENCODING )
                .content( json ) ).andExpect( status().isConflict() )
                .andExpect( jsonPath( "$", Matchers.containsString( "Item not found" ) ) );
    }

}
