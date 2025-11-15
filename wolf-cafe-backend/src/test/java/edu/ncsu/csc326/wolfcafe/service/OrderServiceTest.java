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
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;

/**
 * Test class for the OrderService and its implementation
 *
 * @author Brooke Wu
 */
@SpringBootTest
public class OrderServiceTest {

    /** The service being tested. */
    @Autowired
    private OrderService    orderService;

    /** The order repository. */
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    void setUp () throws Exception {
        orderRepository.deleteAll();
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.OrderService#createOrder(edu.ncsu.csc326.coffee_maker.dto.OrderDto)}.
     */
    @Test
    @Transactional
    void testCreateOrder () {
    		// TODO
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
//        // create order
//        final OrderDto orderDto = new OrderDto( );
//        final OrderDto savedOrder = orderService.createOrder( orderDto );
//
//        // update order
//        orderService.updateOrder( savedOrder.getId(), new OrderDto( "boba" ) );
//
//        // check
//        final OrderDto newOrder = orderService.getOrderById( savedOrder.getId() );
//        assertAll( "Order contents", () -> assertEquals( savedOrder.getId(), newOrder.getId() ),
//                () -> assertEquals( "boba", newOrder.getName() ) );
//
//        assertThrows( ResourceNotFoundException.class,
//                () -> orderService.updateOrder( 0L, new OrderDto( "coffee" ) ) );

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
