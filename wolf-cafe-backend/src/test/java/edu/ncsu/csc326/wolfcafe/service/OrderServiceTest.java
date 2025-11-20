package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderLineDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.Order.OrderStatus;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.mapper.ItemMapper;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import jakarta.persistence.EntityManager;

/**
 * Test class for the OrderService and its implementation
 *
 * @author Brooke Wu
 * @author Diya Patel (dapatel8)
 */
@SpringBootTest
@ActiveProfiles ( "test" )
public class OrderServiceTest {

    /** Reference to EntityManager for cleanup */
    @Autowired
    private EntityManager    entityManager;

    /** The service being tested. */
    @Autowired
    private OrderService     orderService;

    /** The inventory service */
    @Autowired
    private InventoryService inventoryService;

    /** The auth service */
    @Autowired
    private AuthService      authService;

    /** The item service */
    @Autowired
    private ItemService      itemService;

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
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.OrderService#createOrder(edu.ncsu.csc326.coffee_maker.dto.OrderDto)}.
     */
    @Test
    @Transactional
    void testCreateOrder () {
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
        final RegisterDto customerRegister = new RegisterDto();
        customerRegister.setName( "Customer" );
        customerRegister.setUsername( "customer" );
        customerRegister.setEmail( "customer@mail.com" );
        customerRegister.setPassword( "abc123" );
        authService.register( customerRegister );
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

        final OrderDto createdOrder = orderService.createOrder( order );
        assertEquals( createdOrder.getOrderItems().getFirst().getItem().getName(),
                order.getOrderItems().getFirst().getItem().getName() );
        inventory = inventoryService.getInventory();
        assertEquals( 7, inventory.getIngredients().get( "Chocolate" ) );
        assertEquals( 8, inventory.getIngredients().get( "Sugar" ) );
        assertEquals( 9, inventory.getIngredients().get( "Milk" ) );
    }

    /**
     * Test method for updateOrder
     */
    @Test
    @Transactional
    void testUpdateOrder () {
        // Setup: create item, inventory, customer, barista, and order
        ItemDto item = new ItemDto();
        item.setName( "Tea" );
        item.setPrice( 3.50 );
        final Map<String, Integer> teaIngredients = new HashMap<>();
        teaIngredients.put( "Tea Leaves", 2 );
        teaIngredients.put( "Sugar", 1 );
        teaIngredients.put( "Lemon", 1 );
        item.setIngredients( teaIngredients );
        item = itemService.addItem( item );

        InventoryDto inventory = new InventoryDto();
        final Map<String, Integer> inventoryIngredients = new HashMap<>();
        inventoryIngredients.put( "Tea Leaves", 10 );
        inventoryIngredients.put( "Sugar", 10 );
        inventoryIngredients.put( "Lemon", 10 );
        inventory.setIngredients( inventoryIngredients );
        inventory = inventoryService.createInventory( inventory );

        final RegisterDto customerRegister = new RegisterDto();
        customerRegister.setName( "Alice" );
        customerRegister.setUsername( "alice" );
        customerRegister.setEmail( "alice@mail.com" );
        customerRegister.setPassword( "password" );
        authService.register( customerRegister );
        final User customer = userRepository.findByUsername( "alice" ).get();

        User preparedBy = new User();
        preparedBy.setName( "Eve" );
        preparedBy.setUsername( "barista2" );
        preparedBy.setEmail( "eve@mail.com" );
        preparedBy.setPassword( "barista2pass" );
        preparedBy = userRepository.save( preparedBy );

        final OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setPreparedBy( preparedBy );

        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Tea" ) ) );
        orderLine.setQuantity( 1 );
        final List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine );
        order.setOrderItems( orderItems );

        final OrderDto createdOrder = orderService.createOrder( order );

        // Update order status
        createdOrder.setStatus( OrderStatus.PLACED );
        final OrderDto updatedOrder = orderService.updateOrder( createdOrder.getId(), createdOrder );

        assertEquals( OrderStatus.PLACED, updatedOrder.getStatus() );
        assertEquals( "Tea", updatedOrder.getOrderItems().getFirst().getItem().getName() );
    }

    @Test
    @Transactional
    void testListOrdersByStatus () {
        // Setup: create two orders with different statuses
        final RegisterDto customerRegister = new RegisterDto();
        customerRegister.setName( "Bob" );
        customerRegister.setUsername( "bob" );
        customerRegister.setEmail( "bob@mail.com" );
        customerRegister.setPassword( "password" );
        authService.register( customerRegister );
        final User customer = userRepository.findByUsername( "bob" ).get();

        User barista = new User();
        barista.setName( "Sam" );
        barista.setUsername( "barista3" );
        barista.setEmail( "sam@mail.com" );
        barista.setPassword( "barista3pass" );
        barista = userRepository.save( barista );

        ItemDto item = new ItemDto();
        item.setName( "Latte" );
        item.setPrice( 5.00 );
        item.setIngredients( Map.of( "Milk", 2, "Coffee", 1 ) );
        item = itemService.addItem( item );

        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Latte" ) ) );
        orderLine.setQuantity( 1 );

        // Create order1 with IN_PROGRESS status
        final OrderDto order1 = new OrderDto();
        order1.setCustomer( customer );
        order1.setPreparedBy( barista );
        final List<OrderLineDto> orderItems1 = new ArrayList<>();
        final OrderLineDto orderLine1 = new OrderLineDto();
        orderLine1.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Latte" ) ) );
        orderLine1.setQuantity( 1 );
        orderItems1.add( orderLine1 );
        order1.setOrderItems( orderItems1 );
        final OrderDto createdOrder = orderService.createOrder( order1 );
        orderService.prepareOrder( createdOrder.getId(), barista.getUsername() );

        // Create order2 with READY status
        final OrderDto order2 = new OrderDto();
        order2.setCustomer( customer );
        order2.setPreparedBy( barista );
        final List<OrderLineDto> orderItems2 = new ArrayList<>();
        final OrderLineDto orderLine2 = new OrderLineDto();
        orderLine2.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Latte" ) ) );
        orderLine2.setQuantity( 1 );
        orderItems2.add( orderLine2 );
        order2.setOrderItems( orderItems2 );
        final OrderDto createdOrder2 = orderService.createOrder( order2 );
        orderService.prepareOrder( createdOrder2.getId(), barista.getUsername() );
        orderService.markReady( createdOrder2.getId(), barista.getUsername() );

        final List<OrderDto> preparingOrders = orderService.listOrders( OrderStatus.IN_PROGRESS );
        final List<OrderDto> readyOrders = orderService.listOrders( OrderStatus.READY );

        assertEquals( 1, preparingOrders.size() );
        assertEquals( OrderStatus.IN_PROGRESS, preparingOrders.getFirst().getStatus() );
        assertEquals( 1, readyOrders.size() );
        assertEquals( OrderStatus.READY, readyOrders.getFirst().getStatus() );
    }

    @Test
    @Transactional
    void testPrepareOrder () {
        // Setup: create order with status NEW
        final RegisterDto customerRegister = new RegisterDto();
        customerRegister.setName( "Charlie" );
        customerRegister.setUsername( "charlie" );
        customerRegister.setEmail( "charlie@mail.com" );
        customerRegister.setPassword( "password" );
        authService.register( customerRegister );
        final User customer = userRepository.findByUsername( "charlie" ).get();

        User barista = new User();
        barista.setName( "Dana" );
        barista.setUsername( "barista4" );
        barista.setEmail( "dana@mail.com" );
        barista.setPassword( "barista4pass" );
        barista = userRepository.save( barista );

        ItemDto item = new ItemDto();
        item.setName( "Mocha" );
        item.setPrice( 5.50 );
        item.setIngredients( Map.of( "Chocolate", 2, "Coffee", 1 ) );
        item = itemService.addItem( item );

        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Mocha" ) ) );
        orderLine.setQuantity( 1 );

        final OrderDto order = new OrderDto();
        order.setCustomer( customer );
        final List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine );
        order.setOrderItems( orderItems );
        order.setStatus( OrderStatus.PLACED );
        final OrderDto createdOrder = orderService.createOrder( order );

        // Prepare order
        final OrderDto preparedOrder = orderService.prepareOrder( createdOrder.getId(), barista.getUsername() );

        assertEquals( OrderStatus.IN_PROGRESS, preparedOrder.getStatus() );
        assertEquals( "barista4", preparedOrder.getPreparedBy().getUsername() );
    }

    @Test
    @Transactional
    void testMarkReady () {
        // Setup: create and prepare order
        final RegisterDto customerRegister = new RegisterDto();
        customerRegister.setName( "Derek" );
        customerRegister.setUsername( "derek" );
        customerRegister.setEmail( "derek@mail.com" );
        customerRegister.setPassword( "password" );
        authService.register( customerRegister );
        final User customer = userRepository.findByUsername( "derek" ).get();

        User barista = new User();
        barista.setName( "Erin" );
        barista.setUsername( "barista5" );
        barista.setEmail( "erin@mail.com" );
        barista.setPassword( "barista5pass" );
        barista = userRepository.save( barista );

        ItemDto item = new ItemDto();
        item.setName( "Espresso" );
        item.setPrice( 3.00 );
        item.setIngredients( Map.of( "Coffee", 2 ) );
        item = itemService.addItem( item );

        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Espresso" ) ) );
        orderLine.setQuantity( 1 );

        final OrderDto order = new OrderDto();
        order.setCustomer( customer );
        final List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine );
        order.setOrderItems( orderItems );
        order.setStatus( OrderStatus.IN_PROGRESS );
        order.setPreparedBy( barista );
        final OrderDto createdOrder = orderService.createOrder( order );

        // Mark order as ready
        final OrderDto readyOrder = orderService.markReady( createdOrder.getId(), barista.getUsername() );

        assertEquals( OrderStatus.READY, readyOrder.getStatus() );
        assertEquals( "barista5", readyOrder.getPreparedBy().getUsername() );
    }

    @Test
    @Transactional
    void testMarkFulfilled () {
        // Setup: create and ready order
        final RegisterDto customerRegister = new RegisterDto();
        customerRegister.setName( "Ellen" );
        customerRegister.setUsername( "ellen" );
        customerRegister.setEmail( "ellen@mail.com" );
        customerRegister.setPassword( "password" );
        authService.register( customerRegister );
        final User customer = userRepository.findByUsername( "ellen" ).get();

        User barista = new User();
        barista.setName( "Frank" );
        barista.setUsername( "barista6" );
        barista.setEmail( "frank@mail.com" );
        barista.setPassword( "barista6pass" );
        barista = userRepository.save( barista );

        ItemDto item = new ItemDto();
        item.setName( "Cappuccino" );
        item.setPrice( 4.00 );
        item.setIngredients( Map.of( "Milk", 1, "Coffee", 2 ) );
        item = itemService.addItem( item );

        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Cappuccino" ) ) );
        orderLine.setQuantity( 1 );

        final OrderDto order = new OrderDto();
        order.setCustomer( customer );
        final List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine );
        order.setOrderItems( orderItems );
        order.setPreparedBy( barista );
        final OrderDto createdOrder = orderService.createOrder( order );

        final Order existing = orderRepository.findById( createdOrder.getId() ).get();
        existing.setStatus( OrderStatus.READY );
        orderRepository.save( existing );

        // Mark order as fulfilled
        final OrderDto fulfilledOrder = orderService.orderFulfilled( createdOrder.getId() );

        assertEquals( OrderStatus.FULFILLED, fulfilledOrder.getStatus() );
    }

    @Test
    @Transactional
    void testCancelOrder () {
        // Setup: create order
        final RegisterDto customerRegister = new RegisterDto();
        customerRegister.setName( "Fiona" );
        customerRegister.setUsername( "fiona" );
        customerRegister.setEmail( "fiona@mail.com" );
        customerRegister.setPassword( "password" );
        authService.register( customerRegister );
        final User customer = userRepository.findByUsername( "fiona" ).get();

        ItemDto item = new ItemDto();
        item.setName( "Americano" );
        item.setPrice( 3.75 );
        item.setIngredients( Map.of( "Coffee", 2, "Water", 1 ) );
        item = itemService.addItem( item );

        final OrderLineDto orderLine = new OrderLineDto();
        orderLine.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Americano" ) ) );
        orderLine.setQuantity( 1 );

        final OrderDto order = new OrderDto();
        order.setCustomer( customer );
        final List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine );
        order.setOrderItems( orderItems );
        order.setStatus( OrderStatus.PLACED );
        final OrderDto createdOrder = orderService.createOrder( order );

        // Cancel order
        final OrderDto canceledOrder = orderService.cancelOrder( createdOrder.getId() );

        assertEquals( OrderStatus.CANCELLED, canceledOrder.getStatus() );
    }

    @Test
    @Transactional
    void testListOrdersByCustomer () {
        // Setup: create customer and two orders
        final RegisterDto customerRegister = new RegisterDto();
        customerRegister.setName( "George" );
        customerRegister.setUsername( "george" );
        customerRegister.setEmail( "george@mail.com" );
        customerRegister.setPassword( "password" );
        authService.register( customerRegister );
        final User customer = userRepository.findByUsername( "george" ).get();

        ItemDto item1 = new ItemDto();
        item1.setName( "Flat White" );
        item1.setPrice( 4.25 );
        item1.setIngredients( Map.of( "Milk", 2, "Coffee", 1 ) );
        item1 = itemService.addItem( item1 );

        ItemDto item2 = new ItemDto();
        item2.setName( "Macchiato" );
        item2.setPrice( 4.00 );
        item2.setIngredients( Map.of( "Coffee", 2, "Milk", 1 ) );
        item2 = itemService.addItem( item2 );

        final OrderLineDto orderLine1 = new OrderLineDto();
        orderLine1.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Flat White" ) ) );
        orderLine1.setQuantity( 1 );

        final OrderLineDto orderLine2 = new OrderLineDto();
        orderLine2.setItem( ItemMapper.mapToItem( itemService.getItemByName( "Macchiato" ) ) );
        orderLine2.setQuantity( 1 );

        final OrderDto order1 = new OrderDto();
        order1.setCustomer( customer );
        final List<OrderLineDto> orderItems = new ArrayList<>();
        orderItems.add( orderLine1 );
        order1.setOrderItems( orderItems );
        order1.setStatus( OrderStatus.PLACED );
        orderService.createOrder( order1 );

        final OrderDto order2 = new OrderDto();
        order2.setCustomer( customer );
        final List<OrderLineDto> orderItems2 = new ArrayList<>();
        orderItems2.add( orderLine2 );
        order2.setOrderItems( orderItems );
        order2.setStatus( OrderStatus.PLACED );
        orderService.createOrder( order2 );

        final List<OrderDto> customerOrders = orderService.getCustomersOrders( customer.getUsername() );

        assertEquals( 2, customerOrders.size() );
        assertEquals( "george", customerOrders.getFirst().getCustomer().getUsername() );
    }

    /**
     * Tests that exception is thrown when order is not placed and is trying to
     * get updated
     */
    @Test
    @Transactional
    void testUpdateOrderFailsWhenNotPlaced () {
        // Create customer
        final RegisterDto reg = new RegisterDto();
        reg.setName( "Test" );
        reg.setUsername( "testUser1" );
        reg.setEmail( "test1@mail.com" );
        reg.setPassword( "pwd" );
        authService.register( reg );
        final User customer = userRepository.findByUsername( "testUser1" ).get();

        // Create item
        ItemDto item = new ItemDto();
        item.setName( "FailTea" );
        item.setPrice( 3.0 );
        item.setIngredients( Map.of( "Tea", 1 ) );
        item = itemService.addItem( item );

        // Create order
        final OrderLineDto line = new OrderLineDto();
        line.setItem( ItemMapper.mapToItem( itemService.getItemByName( "FailTea" ) ) );
        line.setQuantity( 1 );

        final OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setOrderItems( new ArrayList<>( List.of( line ) ) );
        final OrderDto created = orderService.createOrder( order );

        // Manually change to non-PLACED status
        final Order existing = orderRepository.findById( created.getId() ).get();
        existing.setStatus( OrderStatus.READY );
        orderRepository.save( existing );

        // Expect exception
        assertThrows( IllegalStateException.class, () -> {
            created.setStatus( OrderStatus.READY );
            orderService.updateOrder( created.getId(), created );
        } );
    }

    /**
     * Tests that order is failed when trying to fufill when it is not ready
     */
    @Test
    @Transactional
    void testOrderFulfilledFailsWhenNotReady () {
        // Create customer
        final RegisterDto reg = new RegisterDto();
        reg.setName( "Test2" );
        reg.setUsername( "testUser2" );
        reg.setEmail( "test2@mail.com" );
        reg.setPassword( "pwd" );
        authService.register( reg );
        final User customer = userRepository.findByUsername( "testUser2" ).get();

        // Create item
        ItemDto item = new ItemDto();
        item.setName( "FailCoffee" );
        item.setPrice( 4.0 );
        item.setIngredients( Map.of( "Coffee", 1 ) );
        item = itemService.addItem( item );

        // Create order
        final OrderLineDto line = new OrderLineDto();
        line.setItem( ItemMapper.mapToItem( itemService.getItemByName( "FailCoffee" ) ) );
        line.setQuantity( 1 );

        final OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setOrderItems( new ArrayList<>( List.of( line ) ) );
        final OrderDto created = orderService.createOrder( order );

        // Order is PLACED, not READY → SHOULD FAIL
        assertThrows( IllegalStateException.class, () -> {
            orderService.orderFulfilled( created.getId() );
        } );
    }

    /**
     * If order is not placed then it cannot be cancelled.
     */
    @Test
    @Transactional
    void testCancelOrderFailsWhenNotPlaced () {
        // Create customer
        final RegisterDto reg = new RegisterDto();
        reg.setName( "Test3" );
        reg.setUsername( "testUser3" );
        reg.setEmail( "test3@mail.com" );
        reg.setPassword( "pwd" );
        authService.register( reg );
        final User customer = userRepository.findByUsername( "testUser3" ).get();

        // Create item
        ItemDto item = new ItemDto();
        item.setName( "FailLatte" );
        item.setPrice( 5.0 );
        item.setIngredients( Map.of( "Milk", 1, "Coffee", 1 ) );
        item = itemService.addItem( item );

        // Create order
        final OrderLineDto line = new OrderLineDto();
        line.setItem( ItemMapper.mapToItem( itemService.getItemByName( "FailLatte" ) ) );
        line.setQuantity( 1 );

        final OrderDto order = new OrderDto();
        order.setCustomer( customer );
        order.setOrderItems( new ArrayList<>( List.of( line ) ) );
        final OrderDto created = orderService.createOrder( order );

        // Force order into non-PLACED state
        final Order existing = orderRepository.findById( created.getId() ).get();
        existing.setStatus( OrderStatus.IN_PROGRESS );
        orderRepository.save( existing );

        // Expect failure
        assertThrows( IllegalStateException.class, () -> {
            orderService.cancelOrder( created.getId() );
        } );
    }

}
