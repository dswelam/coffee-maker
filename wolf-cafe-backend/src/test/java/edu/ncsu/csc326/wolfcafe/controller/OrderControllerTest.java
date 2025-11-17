package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
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
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.mapper.ItemMapper;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.service.AuthService;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.ItemService;
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
    private String                    adminUserPassword;

    /** Mocked MVC for testing */
    @Autowired
    private MockMvc                   mvc;

    /** Mocked AuthService */
    @MockitoBean
    private AuthService               authService;
    
    /** Reference to EntityManager for cleanup */
    @Autowired
    private EntityManager     entityManager;
    
    /** The inventory service */
    @Autowired
    private InventoryService inventoryService;
    
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
    		User customerRegister = new User();
    		customerRegister.setName("Customer");
    		customerRegister.setUsername("customer");
    		customerRegister.setEmail("customer@mail.com");
    		customerRegister.setPassword("abc123");
    		userRepository.save(customerRegister);
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
    		
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

    }

}
