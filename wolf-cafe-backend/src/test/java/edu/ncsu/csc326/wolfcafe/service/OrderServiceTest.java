package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderLineDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.mapper.ItemMapper;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import jakarta.persistence.EntityManager;

/**
 * Test class for the OrderService and its implementation
 *
 * @author Brooke Wu
 */
@SpringBootTest
public class OrderServiceTest {
	
    /** Reference to EntityManager for cleanup */
    @Autowired
    private EntityManager     entityManager;

    /** The service being tested. */
    @Autowired
    private OrderService    orderService;
    
    /** The inventory service */
    @Autowired
    private InventoryService inventoryService;
    
    /** The auth service */
    @Autowired
    private AuthService authService;
    
    /** The item service */
    @Autowired
    private ItemService itemService;

    /** The order repository. */
    @Autowired
    private OrderRepository orderRepository;
    
    /** The user repository */
    @Autowired
    private UserRepository userRepository;

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
        catch ( Exception e ) {
            // Table might not exist yet, ignore
        }
        try {
            entityManager.createNativeQuery( "DELETE FROM recipe_ingredients" ).executeUpdate();
        }
        catch ( Exception e ) {
            // Table might not exist yet, ignore
        }
        try {
            entityManager.createNativeQuery( "DELETE FROM inventory" ).executeUpdate();
        }
        catch ( Exception e ) {
            // Table might not exist yet, ignore
        }
        try {
            entityManager.createNativeQuery( "DELETE FROM recipe" ).executeUpdate();
        }
        catch ( Exception e ) {
            // Table might not exist yet, ignore
        }
        try {
            entityManager.createNativeQuery( "ALTER TABLE inventory AUTO_INCREMENT = 1" ).executeUpdate();
        }
        catch ( Exception e ) {
            // Table might not exist yet, ignore
        }
        try {
            entityManager.createNativeQuery( "ALTER TABLE recipe AUTO_INCREMENT = 1" ).executeUpdate();
        }
        catch ( Exception e ) {
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
        item.setName("Coffee");
        item.setPrice(4.35);
        Map<String, Integer> coffeeIngredients = new HashMap<String, Integer>();
        coffeeIngredients.put("Chocolate", 3);
        coffeeIngredients.put("Sugar", 2);
        coffeeIngredients.put("Milk", 1);
        item.setIngredients(coffeeIngredients);
        item = itemService.addItem(item);
        
        InventoryDto inventory = new InventoryDto();
        coffeeIngredients.put("Chocolate", 10);
        coffeeIngredients.put("Sugar", 10);
        coffeeIngredients.put("Milk", 10);
        inventory.setIngredients(coffeeIngredients);
        inventory = inventoryService.createInventory(inventory);
        
    		OrderDto order = new OrderDto();
    		RegisterDto customerRegister = new RegisterDto();
    		customerRegister.setName("Customer");
    		customerRegister.setUsername("customer");
    		customerRegister.setEmail("customer@mail.com");
    		customerRegister.setPassword("abc123");
    		authService.register(customerRegister);
    		order.setCustomer(userRepository.findByUsername(customerRegister.getUsername()).get());
    		
    		User preparedBy = new User();
    		preparedBy.setName("Barry");
    		preparedBy.setUsername("barista");
    		preparedBy.setEmail("barry@wolfcafe.com");
    		preparedBy.setPassword("xyz789");
    		preparedBy =  userRepository.save(preparedBy);
    		order.setPreparedBy(preparedBy);
    		
    		OrderLineDto orderLine = new OrderLineDto();
    		orderLine.setItem(ItemMapper.mapToItem(itemService.getItemByName("Coffee")));
    		orderLine.setQuantity(1);
    		List<OrderLineDto> orderItems = new ArrayList<OrderLineDto>();
    		orderItems.add(orderLine);
    		order.setOrderItems(orderItems);
    		
    		OrderDto createdOrder = orderService.createOrder(order);
    		assertEquals(createdOrder.getOrderItems().getFirst().getItem().getName(), order.getOrderItems().getFirst().getItem().getName());
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.OrderService#isDuplicateName(java.lang.String)}.
     */
    @Test
    @Transactional
    void testIsDuplicateName () {
    		// TODO
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.OrderService#getAllOrders()}.
     */
    @Test
    @Transactional
    void testGetAllOrders () {
    		// TODO
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.OrderService#updateOrder(java.lang.Long, edu.ncsu.csc326.coffee_maker.dto.OrderDto)}.
     */
    @Test
    @Transactional
    void testUpdateOrder () {
    		// TODO
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.OrderService#deleteOrder(java.lang.Long)}.
     */
    @Test
    @Transactional
    void testDeleteOrder () {
        // TODO
    }

}
