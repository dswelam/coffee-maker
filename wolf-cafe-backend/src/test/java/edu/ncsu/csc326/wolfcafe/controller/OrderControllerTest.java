package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderLineDto;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.Order.OrderStatus;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.mapper.ItemMapper;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.service.AuthService;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.ItemService;
import edu.ncsu.csc326.wolfcafe.service.OrderService;
import jakarta.persistence.EntityManager;

/**
 * GENERATIVE AI USED:
 *
 * Model: GPT-4.1 This code snippet was completed with the assistance of
 * Generative AI technology. The code was reviewed and edited by human
 * engineers, but may contain errors.
 *
 * Prompts used: - "Complete the OrderControllerTest class to include tests for
 * preparing an order, marking an order as ready, fulfilling an order,
 * cancelling an order, and listing orders by customer. Use JUnit 5 and Spring
 * Boot testing annotations."
 *
 * Tests the Order controller.
 *
 * @author Brooke Wu
 * @author Dania Swelam
 */
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    /** Admin password from application.properties */
    @Value ( "${app.admin-user-password}" )
    private String           adminUserPassword;

    /** Mocked MVC for testing */
    @Autowired
    private MockMvc          mvc;

    /** Mocked AuthService */
    @MockitoBean
    private AuthService      authService;

    /** Reference to EntityManager for cleanup */
    @Autowired
    private EntityManager    entityManager;

    /** The inventory service */
    @Autowired
    private InventoryService inventoryService;

    /** The item service */
    @Autowired
    private ItemService      itemService;

    /** The order service */
    @Autowired
    private OrderService     orderService;

    /** The order repository. */
    @Autowired
    private OrderRepository  orderRepository;

    /** The user repository */
    @Autowired
    private UserRepository   userRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    void setUp () throws Exception {
        orderRepository.deleteAll();

        // Clean up all tables in proper order due to foreign key constraints
        // Use IF EXISTS to avoid errors when tables don't exist yet
        try {
            entityManager.createNativeQuery( "DELETE FROM inventory_ingredients" ).executeUpdate();
        }
        catch ( final Exception e ) {
            // Table might not exist yet, ignore
        }
        try {
            entityManager.createNativeQuery( "DELETE FROM recipe_ingredients" ).executeUpdate();
        }
        catch ( final Exception e ) {
            // Table might not exist yet, ignore
        }
        try {
            entityManager.createNativeQuery( "DELETE FROM inventory" ).executeUpdate();
        }
        catch ( final Exception e ) {
            // Table might not exist yet, ignore
        }
        try {
            entityManager.createNativeQuery( "DELETE FROM recipe" ).executeUpdate();
        }
        catch ( final Exception e ) {
            // Table might not exist yet, ignore
        }
        try {
            entityManager.createNativeQuery( "ALTER TABLE inventory AUTO_INCREMENT = 1" ).executeUpdate();
        }
        catch ( final Exception e ) {
            // Table might not exist yet, ignore
        }
        try {
            entityManager.createNativeQuery( "ALTER TABLE recipe AUTO_INCREMENT = 1" ).executeUpdate();
        }
        catch ( final Exception e ) {
            // Table might not exist yet, ignore
        }
    }

    /**
     * Tests creating a customer user and logging in.
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testCreateOrder () throws Exception {
        ItemDto item = new ItemDto();
        item.setName( "Coffee" );
        item.setPrice( 4.35 );
        final Map<String, Integer> coffeeIngredients = new HashMap<String, Integer>();
        coffeeIngredients.put( "Chocolate", 3 );
        coffeeIngredients.put( "Sugar", 2 );
        coffeeIngredients.put( "Milk", 1 );
        item.setIngredients( coffeeIngredients );
        item = itemService.addItem( item );

        InventoryDto inventory = new InventoryDto();
        coffeeIngredients.put( "Chocolate", 10 );
        coffeeIngredients.put( "Sugar", 10 );
        coffeeIngredients.put( "Milk", 10 );
        inventory.setIngredients( coffeeIngredients );
        inventory = inventoryService.createInventory( inventory );

        final OrderDto order = new OrderDto();
        final User customerRegister = new User();
        customerRegister.setName( "Customer" );
        customerRegister.setUsername( "customer" );
        customerRegister.setEmail( "customer@mail.com" );
        customerRegister.setPassword( "abc123" );
        userRepository.save( customerRegister );
        order.setCustomer( userRepository.findByUsername( customerRegister.getUsername() ).get() );

        User preparedBy = new User();
        preparedBy.setName( "Barry" );
        preparedBy.setUsername( "barista" );
        preparedBy.setEmail( "barry@wolfcafe.com" );
        preparedBy.setPassword( "xyz789" );
        preparedBy = userRepository.save( preparedBy );
        order.setPreparedBy( preparedBy );

        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Coffee" ) ) );
        orderLine.setQuantity( 1 );
        final List<OrderLineDto> orderItems = new ArrayList<OrderLineDto>();
        orderItems.add( orderLine );
        order.setOrderItems( orderItems );

        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

    }

    /**
     * Tests updating an order.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "customer", roles = { "CUSTOMER" } )
    public void testUpdateOrder () throws Exception {
        // Create barista user
        User barista = new User();
        barista.setName( "Barry" );
        barista.setUsername( "barista" );
        barista.setEmail( "barista@mail.com" );
        barista.setPassword( "xyz789" );
        barista = userRepository.save( barista );

        // Create customer user
        User customer = new User();
        customer.setName( "Customer" );
        customer.setUsername( "customer" );
        customer.setEmail( "customer@mail.com" );
        customer.setPassword( "abc123" );
        customer = userRepository.save( customer );

        // Create item
        ItemDto item = new ItemDto();
        item.setName( "Coffee" );
        item.setPrice( 4.35 );
        final Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put( "Chocolate", 3 );
        ingredients.put( "Sugar", 2 );
        ingredients.put( "Milk", 1 );
        item.setIngredients( ingredients );
        item = itemService.addItem( item );

        // Create order
        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Coffee" ) ) );
        orderLine.setQuantity( 1 );
        List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine );

        OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setPreparedBy( barista );
        order.setStatus( OrderStatus.PLACED );
        order.setOrderItems( orderItems );
        order = orderService.createOrder( order );

        // Test NOT_FOUND case first
        mvc.perform( put( "/api/orders/999999" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );

        // Check if order items exist and add them if not
        if ( order.getOrderItems() == null || order.getOrderItems().isEmpty() ) {
            orderLine.setQuantity( 2 );
            orderItems = new ArrayList<>();
            orderItems.add( orderLine );
            order.setOrderItems( orderItems );
        }
        else {
            // Update existing order quantity
            order.getOrderItems().get( 0 ).setQuantity( 2 );
        }

        mvc.perform( put( "/api/orders/" + order.getId() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.orderItems[0].quantity" ).value( 2 ) );
    }

    /**
     * Tests updating an order.
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    @WithMockUser ( username = "barista", roles = { "BARISTA" } )
    public void testViewAllOrders () throws Exception {
        User barista = new User();
        barista.setName( "Barry" );
        barista.setUsername( "barista" );
        barista.setEmail( "barista@mail.com" );
        barista.setPassword( "xyz789" );
        barista = userRepository.save( barista );

        User customer = new User();
        customer.setName( "Customer" );
        customer.setUsername( "customer" );
        customer.setEmail( "customer@mail.com" );
        customer.setPassword( "abc123" );
        customer = userRepository.save( customer );

        ItemDto item = new ItemDto();
        item.setName( "Coffee" );
        item.setPrice( 4.35 );
        final Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put( "Chocolate", 3 );
        ingredients.put( "Sugar", 2 );
        ingredients.put( "Milk", 1 );
        item.setIngredients( ingredients );
        item = itemService.addItem( item );

        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Coffee" ) ) );
        orderLine.setQuantity( 1 );
        final List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine );

        OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setPreparedBy( barista );
        order.setStatus( OrderStatus.PLACED );
        order.setOrderItems( orderItems );
        order = orderService.createOrder( order );

        mvc.perform( get( "/api/orders/queue?status=PLACED" ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.length()" ).value( 1 ) )
                .andExpect( jsonPath( "$[0].customer.username" ).value( "customer" ) );
    }

    /**
     * Tests preparing an order.
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    @WithMockUser ( username = "barista", roles = { "BARISTA" } )
    public void testPrepareOrder () throws Exception {
        final User customer = new User();
        customer.setName( "Customer" );
        customer.setUsername( "customer" );
        customer.setEmail( "customer@mail.com" );
        customer.setPassword( "abc123" );
        userRepository.save( customer );

        User barista = new User();
        barista.setName( "Barry" );
        barista.setUsername( "barista" );
        barista.setEmail( "barry@wolfcafe.com" );
        barista.setPassword( "xyz789" );
        barista = userRepository.save( barista );

        ItemDto item = new ItemDto();
        item.setName( "Coffee" );
        item.setPrice( 4.35 );
        final Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put( "Chocolate", 3 );
        ingredients.put( "Sugar", 2 );
        ingredients.put( "Milk", 1 );
        item.setIngredients( ingredients );
        item = itemService.addItem( item );

        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Coffee" ) ) );
        orderLine.setQuantity( 1 );
        final List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine );

        OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setStatus( OrderStatus.PLACED );
        order.setOrderItems( orderItems );
        order = orderService.createOrder( order );

        mvc.perform( put( "/api/orders/" + order.getId() + "/prepare" ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.status" ).value( "IN_PROGRESS" ) )
                .andExpect( jsonPath( "$.preparedBy.username" ).value( "barista" ) );
    }

    /**
     * Tests preparing an order.
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    @WithMockUser ( username = "barista", roles = { "BARISTA" } )
    public void testMarkReady () throws Exception {
        final User customer = new User();
        customer.setName( "Customer" );
        customer.setUsername( "customer" );
        customer.setEmail( "customer@mail.com" );
        customer.setPassword( "abc123" );
        userRepository.save( customer );

        User barista = new User();
        barista.setName( "Barry" );
        barista.setUsername( "barista" );
        barista.setEmail( "barry@wolfcafe.com" );
        barista.setPassword( "xyz789" );
        barista = userRepository.save( barista );

        ItemDto item = new ItemDto();
        item.setName( "Coffee" );
        item.setPrice( 4.35 );
        final Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put( "Chocolate", 3 );
        ingredients.put( "Sugar", 2 );
        ingredients.put( "Milk", 1 );
        item.setIngredients( ingredients );
        item = itemService.addItem( item );

        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Coffee" ) ) );
        orderLine.setQuantity( 1 );
        final List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine );

        OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setStatus( OrderStatus.IN_PROGRESS );
        order.setOrderItems( orderItems );
        order = orderService.createOrder( order );

        mvc.perform( put( "/api/orders/" + order.getId() + "/ready" ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.status" ).value( "READY" ) );
    }

    /**
     * Tests fulfilling an order.
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    @WithMockUser ( username = "customer", roles = { "CUSTOMER" } )
    public void testOrderFulfilled () throws Exception {
        final User customer = new User();
        customer.setName( "Customer" );
        customer.setUsername( "customer" );
        customer.setEmail( "customer@mail.com" );
        customer.setPassword( "abc123" );
        userRepository.save( customer );

        ItemDto item = new ItemDto();
        item.setName( "Coffee" );
        item.setPrice( 4.35 );
        final Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put( "Chocolate", 3 );
        ingredients.put( "Sugar", 2 );
        ingredients.put( "Milk", 1 );
        item.setIngredients( ingredients );
        item = itemService.addItem( item );

        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Coffee" ) ) );
        orderLine.setQuantity( 1 );
        final List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine );

        OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setStatus( OrderStatus.READY );
        order.setOrderItems( orderItems );
        order = orderService.createOrder( order );

        final Order existing = orderRepository.findById( order.getId() ).get();
        existing.setStatus( OrderStatus.READY );
        orderRepository.save( existing );

        mvc.perform( put( "/api/orders/" + order.getId() + "/fulfill" ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.status" ).value( "FULFILLED" ) );
    }

    /**
     * Tests cancelling an order.
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    @WithMockUser ( username = "customer", roles = { "CUSTOMER" } )
    public void testOrderCancelled () throws Exception {
        final User customer = new User();
        customer.setName( "Customer" );
        customer.setUsername( "customer" );
        customer.setEmail( "customer@mail.com" );
        customer.setPassword( "abc123" );
        userRepository.save( customer );

        ItemDto item = new ItemDto();
        item.setName( "Coffee" );
        item.setPrice( 4.35 );
        final Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put( "Chocolate", 3 );
        ingredients.put( "Sugar", 2 );
        ingredients.put( "Milk", 1 );
        item.setIngredients( ingredients );
        item = itemService.addItem( item );

        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Coffee" ) ) );
        orderLine.setQuantity( 1 );
        final List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine );

        OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setStatus( OrderStatus.PLACED );
        order.setOrderItems( orderItems );
        order = orderService.createOrder( order );

        mvc.perform( put( "/api/orders/" + order.getId() + "/cancel" ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.status" ).value( "CANCELLED" ) );
    }

    /**
     * Tests listing orders by customer.
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    @WithMockUser ( username = "customer", roles = { "CUSTOMER" } )
    public void testListOrdersByCustomer () throws Exception {
        final User customer = new User();
        customer.setName( "Customer" );
        customer.setUsername( "customer" );
        customer.setEmail( "customer@mail.com" );
        customer.setPassword( "abc123" );
        userRepository.save( customer );

        ItemDto item = new ItemDto();
        item.setName( "Coffee" );
        item.setPrice( 4.35 );
        final Map<String, Integer> ingredients = new HashMap<>();
        ingredients.put( "Chocolate", 3 );
        ingredients.put( "Sugar", 2 );
        ingredients.put( "Milk", 1 );
        item.setIngredients( ingredients );
        item = itemService.addItem( item );

        for ( int i = 0; i < 2; i++ ) {
            final OrderLineDto orderLine = new OrderLineDto();
            orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Coffee" ) ) );
            orderLine.setQuantity( 1 );
            final List<OrderLineDto> orderItems = new ArrayList<>();
            orderItems.add( orderLine );

            final OrderDto order = new OrderDto();
            order.setCustomer( customer );
            order.setStatus( OrderStatus.PLACED );
            order.setOrderItems( orderItems );
            orderService.createOrder( order );
        }

        mvc.perform( get( "/api/orders/myorders" ).accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.length()" ).value( 2 ) )
                .andExpect( jsonPath( "$[0].customer.username" ).value( "customer" ) )
                .andExpect( jsonPath( "$[1].customer.username" ).value( "customer" ) );
    }

    /**
     * Test when order fails if it is not placed
     *
     * @throws Exception
     *             because order is not placed
     */
    @Test
    @Transactional
    @WithMockUser ( username = "customer", roles = { "CUSTOMER" } )
    public void testUpdateOrderFailsWhenNotPlaced () throws Exception {

        // Create users
        final User customer = new User();
        customer.setName( "Customer" );
        customer.setUsername( "customer" );
        customer.setEmail( "c@mail.com" );
        customer.setPassword( "pwd" );
        userRepository.save( customer );

        final User barista = new User();
        barista.setName( "Barry" );
        barista.setUsername( "barista" );
        barista.setEmail( "b@mail.com" );
        barista.setPassword( "pwd" );
        userRepository.save( barista );

        // Create item
        ItemDto item = new ItemDto();
        item.setName( "Latte" );
        item.setPrice( 4.0 );
        item.setIngredients( Map.of( "Milk", 1 ) );
        item = itemService.addItem( item );

        // Create order (PLACED)
        final OrderLineDto line = new OrderLineDto();
        line.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Latte" ) ) );
        line.setQuantity( 1 );

        OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setPreparedBy( barista );
        order.setOrderItems( new ArrayList<>( List.of( line ) ) );
        order = orderService.createOrder( order );

        // Change status to INVALID (READY)
        final Order existing = orderRepository.findById( order.getId() ).get();
        existing.setStatus( OrderStatus.READY );
        orderRepository.save( existing );

        // Expect 400 BAD REQUEST
        mvc.perform( put( "/api/orders/" + order.getId() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isBadRequest() );
    }

    /**
     * Tests that order is fufilled when it is not ready
     *
     * @throws Exception
     *             because order is not ready
     */
    @Test
    @Transactional
    @WithMockUser ( username = "customer", roles = { "CUSTOMER" } )
    public void testOrderFulfilledFailsWhenNotReady () throws Exception {

        // Create customer
        final User customer = new User();
        customer.setName( "Customer" );
        customer.setUsername( "customer" );
        customer.setEmail( "c@mail.com" );
        customer.setPassword( "pwd" );
        userRepository.save( customer );

        // Create item
        ItemDto item = new ItemDto();
        item.setName( "Mocha" );
        item.setPrice( 5.0 );
        item.setIngredients( Map.of( "Coffee", 1 ) );
        item = itemService.addItem( item );

        // Create order (PLACED, not READY)
        final OrderLineDto line = new OrderLineDto();
        line.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Mocha" ) ) );
        line.setQuantity( 1 );

        OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setOrderItems( new ArrayList<>( List.of( line ) ) );
        order = orderService.createOrder( order );

        // Should fail because status != READY
        mvc.perform( put( "/api/orders/" + order.getId() + "/fulfill" ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isBadRequest() );
    }

    /**
     * Tests that the cancel order fails if order is not placed
     *
     * @throws Exception
     *             if order is not placed
     */
    @Test
    @Transactional
    @WithMockUser ( username = "customer", roles = { "CUSTOMER" } )
    public void testCancelOrderFailsWhenNotPlaced () throws Exception {

        // Create customer
        final User customer = new User();
        customer.setName( "Customer" );
        customer.setUsername( "customer" );
        customer.setEmail( "c@mail.com" );
        customer.setPassword( "pwd" );
        userRepository.save( customer );

        // Create item
        ItemDto item = new ItemDto();
        item.setName( "Cappuccino" );
        item.setPrice( 4.0 );
        item.setIngredients( Map.of( "Coffee", 1 ) );
        item = itemService.addItem( item );

        // Create order (PLACED by default)
        final OrderLineDto line = new OrderLineDto();
        line.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Cappuccino" ) ) );
        line.setQuantity( 1 );

        OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setOrderItems( new ArrayList<>( List.of( line ) ) );
        order = orderService.createOrder( order );

        // Set to an invalid status
        final Order existing = orderRepository.findById( order.getId() ).get();
        existing.setStatus( OrderStatus.IN_PROGRESS );
        orderRepository.save( existing );

        // Should fail because status != PLACED
        mvc.perform( put( "/api/orders/" + order.getId() + "/cancel" ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isBadRequest() );
    }

}
