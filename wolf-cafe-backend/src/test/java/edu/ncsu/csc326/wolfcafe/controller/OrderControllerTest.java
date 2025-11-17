package edu.ncsu.csc326.wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
 * Tests the Order controller.
 *
 * @author Brooke Wu
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

    /** The order controller */
    @Autowired
    private OrderController  orderController;

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
     * Tests changing the status for an order to IN_PROGRESS.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "barista", roles = { "BARISTA" } )
    public void testPrepareOrder () throws Exception {
        // Create customer and order
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

        OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setStatus( OrderStatus.PLACED );
        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Coffee" ) ) );
        orderLine.setQuantity( 1 );
        final List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine );
        order.setOrderItems( orderItems );
        order = orderService.createOrder( order );

        // Prepare order
        final var response = orderController.prepareOrder( order.getId() );
        assertEquals( 200, response.getStatusCode().value() );
        assertEquals( OrderStatus.IN_PROGRESS, response.getBody().getStatus() );
        assertEquals( "barista", response.getBody().getPreparedBy().getUsername() );
    }

    /**
     * Tests changing the status for an order to READY.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "barista", roles = { "BARISTA" } )
    public void testMarkReady () throws Exception {
        // Setup order in IN_PROGRESS
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

        OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setStatus( OrderStatus.IN_PROGRESS );
        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Coffee" ) ) );
        orderLine.setQuantity( 1 );
        final List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine );
        order.setOrderItems( orderItems );
        order = orderService.createOrder( order );

        // Mark ready
        final var response = orderController.markReady( order.getId() );
        assertEquals( 200, response.getStatusCode().value() );
        assertEquals( OrderStatus.READY, response.getBody().getStatus() );
    }

    /**
     * Tests changing the status for an order to FULLFILLED.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "customer", roles = { "CUSTOMER" } )
    public void testOrderFulfilled () throws Exception {
        // Setup order in READY
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

        OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setStatus( OrderStatus.READY );
        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Coffee" ) ) );
        orderLine.setQuantity( 1 );
        final List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine );
        order.setOrderItems( orderItems );
        order = orderService.createOrder( order );

        // Fulfill order
        final var response = orderController.orderFulfilled( order.getId() );
        assertEquals( 200, response.getStatusCode().value() );
        assertEquals( OrderStatus.FULFILLED, response.getBody().getStatus() );
    }

    /**
     * Tests changing the status for an order to CANCELLED.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "customer", roles = { "CUSTOMER" } )
    public void testOrderCancelled () throws Exception {
        // Setup order in PLACED
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

        OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setStatus( OrderStatus.PLACED );
        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Coffee" ) ) );
        orderLine.setQuantity( 1 );
        final List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine );
        order.setOrderItems( orderItems );
        order = orderService.createOrder( order );

        // Cancel order
        final var response = orderController.orderCancelled( order.getId() );
        assertEquals( 200, response.getStatusCode().value() );
        assertEquals( OrderStatus.CANCELLED, response.getBody().getStatus() );
    }

    /**
     * Tests listing orders by customer.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "customer", roles = { "CUSTOMER" } )
    public void testListOrdersByCustomer () throws Exception {
        // Setup customer and two orders
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
            final OrderDto order = new OrderDto();
            order.setCustomer( customer );
            order.setStatus( OrderStatus.PLACED );
            final OrderLineDto orderLine = new OrderLineDto();
            orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Coffee" ) ) );
            orderLine.setQuantity( 1 );
            final List<OrderLineDto> orderItems = new ArrayList<>();
            orderItems.add( orderLine );
            order.setOrderItems( orderItems );
            orderService.createOrder( order );
        }

        final var response = orderController.listMyOrders();
        assertEquals( 200, response.getStatusCode().value() );
        assertNotNull( response.getBody() );
        assertEquals( 2, response.getBody().size() );
        for ( final OrderDto o : response.getBody() ) {
            assertEquals( "customer", o.getCustomer().getUsername() );
        }
    }
}

/**
 * GENERATIVE AI USED:
 * Model: GPT-4.1
 * This code snippet was completed with the assistance of Generative AI technology.
 * The code was reviewed and edited by human engineers, but may contain errors.
 *
 * Prompts used:
 * - "Complete the OrderControllerTest class to include tests for preparing an order,
 *    marking an order as ready, fulfilling an order, cancelling an order, and listing
 *    orders by customer. Use JUnit 5 and Spring Boot testing annotations."
 */
