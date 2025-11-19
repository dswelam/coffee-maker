package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderLineDto;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.Order.OrderStatus;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.ItemMapper;
import edu.ncsu.csc326.wolfcafe.mapper.OrderMapper;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.OrderService;

/**
 * Implementation of the OrderService interface.
 *
 * @author Brooke Wu bwu25
 * @author Dania Swelam dswelam (dswelam)
 */
@Service
public class OrderServiceImpl implements OrderService {

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private OrderRepository  orderRepository;

    /** Connection to the inventory service to update the inventory */
    @Autowired
    private InventoryService inventoryService;

    /** Connection to the user repository to get user info */
    @Autowired
    private UserRepository   userRepository;

    @Override
    public OrderDto createOrder ( final OrderDto orderDto ) {
        final InventoryDto inventory = inventoryService.getInventory();

        // Before creating the order, need to check if the inventory has
        // sufficient ingredients to make the items on the order
        final List<OrderLineDto> orderItems = orderDto.getOrderItems();
        final List<OrderLineDto> toRemove = new ArrayList<>();

        // Collect the order items that cannot be fulfilled in a separate list
        // then remove them after the iteration. This change was necessary to
        // avoid
        // a ConcurrentModificationException when removing items from the list
        for ( final OrderLineDto orderLine : orderItems ) {
            final ItemDto item = ItemMapper.mapToItemDto( orderLine.getItem() );
            if ( !checkInventory( inventory, item ) ) {
                toRemove.add( orderLine );
            }
        }
        orderItems.removeAll( toRemove );
        orderDto.setOrderItems( orderItems );
        // Set the order status to PLACED - this was missing previously
        orderDto.setStatus( OrderStatus.PLACED );

        // Save the order to the database
        final Order order = OrderMapper.mapToOrder( orderDto );
        final Order savedOrder = orderRepository.save( order );
        return OrderMapper.mapToOrderDto( savedOrder );
    }

    @Override
    public boolean checkInventory ( final InventoryDto inventoryDto, final ItemDto itemDto ) {
        InventoryDto updatedInventory = inventoryDto;

        // First, loop through each ingredient needed for the item
        for ( final Entry<String, Integer> entry : itemDto.getIngredients().entrySet() ) {
            final String ingredientName = entry.getKey();
            final Integer quantityNeeded = entry.getValue();

            // Check if this ingredient can be found in the inventory
            boolean ingredientFoundInInventory = false;

            for ( final Entry<String, Integer> inventoryEntry : updatedInventory.getIngredients().entrySet() ) {
                if ( inventoryEntry.getKey().equals( ingredientName ) ) {
                    ingredientFoundInInventory = true;

                    // If the ingredient was found in the inventory, check if
                    // the quantity in the inventory is sufficient for the item
                    if ( inventoryEntry.getValue() >= quantityNeeded ) {
                        // If so, subtract the needed quantity of the ingredient
                        // from the inventory
                        updatedInventory.getIngredients().put( ingredientName,
                                inventoryEntry.getValue() - quantityNeeded );
                    }
                    else {
                        return false;
                    }
                }
            }

            if ( !ingredientFoundInInventory ) {
                return false;
            }
        }

        // Save the updated inventory
        updatedInventory = inventoryService.updateInventory( updatedInventory );

        return true;
    }

    @Override
    public int placeOrder ( final Long itemId, final int tip, final int payment ) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public OrderDto getOrderById ( final Long orderId ) {
        final Order order = orderRepository.findById( orderId )
                .orElseThrow( () -> new ResourceNotFoundException( "Order does not exist with id " + orderId ) );
        return OrderMapper.mapToOrderDto( order );
    }

    @Override
    public OrderDto updateOrder ( final Long orderId, final OrderDto orderDto ) {
        // Find existing order or throw if not found
        final Order existing = orderRepository.findById( orderId )
                .orElseThrow( () -> new ResourceNotFoundException( "Order does not exist with id " + orderId ) );

        // UC13: Edit Order alternative flow
        // Order can only be edited when it is in PLACED status
        if ( existing.getStatus() != OrderStatus.PLACED ) {
            throw new IllegalStateException( "Order can only be edited when it is in PLACED status" );
        }

        // Map the incoming DTO to an Order entity
        final Order updated = OrderMapper.mapToOrder( orderDto );

        // Make sure we're updating the correct existing order
        updated.setId( existing.getId() );
        updated.setCustomer( existing.getCustomer() );
        // Keep status as PLACED (editing should not secretly change it)
        updated.setStatus( OrderStatus.PLACED );

        final Order savedOrder = orderRepository.save( updated );
        return OrderMapper.mapToOrderDto( savedOrder );
    }

    @Override
    public void deleteOrder ( final Long orderId ) {
        // TODO Auto-generated method stub

    }

    /**
     * Lists all orders with the given status.
     *
     * @param status
     *            the status to filter by
     * @return list of orders with the given status
     */
    @Override
    public List<OrderDto> listOrders ( final OrderStatus status ) {
        final List<Order> orders = orderRepository.findAllByStatus( status );
        return orders.stream().map( OrderMapper::mapToOrderDto ).toList();
    }

    /**
     * Action to prepare an order
     *
     * @param orderId
     *            The order to prepare by ID
     * @param staffUsername
     *            The staff member preparing the order by username
     * @return The updated order DTO
     */
    @Override
    public OrderDto prepareOrder ( final Long orderId, final String staffUsername ) {
        final Order order = orderRepository.findById( orderId )
                .orElseThrow( () -> new ResourceNotFoundException( "Order does not exist with id " + orderId ) );

        final User staff = userRepository.findByUsername( staffUsername ).orElseThrow(
                () -> new ResourceNotFoundException( "User does not exist with username " + staffUsername ) );

        order.setStatus( OrderStatus.IN_PROGRESS );
        order.setPreparedBy( staff );

        final Order savedOrder = orderRepository.save( order );
        return OrderMapper.mapToOrderDto( savedOrder );
    }

    /**
     * Order is marked as ready for pickup
     *
     * @param orderId
     *            The order to mark as ready by ID
     * @param staffUsername
     *            The staff member marking the order as ready by username
     * @return The updated order DTO
     */
    @Override
    public OrderDto markReady ( final Long orderId, final String staffUsername ) {
        final Order order = orderRepository.findById( orderId )
                .orElseThrow( () -> new ResourceNotFoundException( "Order does not exist with id " + orderId ) );

        final User staff = userRepository.findByUsername( staffUsername ).orElseThrow(
                () -> new ResourceNotFoundException( "User does not exist with username " + staffUsername ) );

        order.setStatus( OrderStatus.READY );
        order.setPreparedBy( staff );

        final Order savedOrder = orderRepository.save( order );
        return OrderMapper.mapToOrderDto( savedOrder );
    }

    /**
     * Action to mark an order as fulfilled
     *
     * @param orderId
     *            The order to mark as fulfilled by ID
     * @return The updated order DTO
     */
    @Override
    public OrderDto orderFulfilled ( final Long orderId ) {
        final Order order = orderRepository.findById( orderId )
                .orElseThrow( () -> new ResourceNotFoundException( "Order does not exist with id " + orderId ) );

        // UC13 main flow: customer can only pick up when the order is READY
        if ( order.getStatus() != OrderStatus.READY ) {
            throw new IllegalStateException( "Order can only be picked up when it is in READY status" );
        }

        order.setStatus( OrderStatus.FULFILLED );

        final Order savedOrder = orderRepository.save( order );
        return OrderMapper.mapToOrderDto( savedOrder );
    }

    /**
     * Cancel an order
     *
     * @param orderId
     *            The order to cancel by ID
     * @return The updated order DTO
     */
    @Override
    public OrderDto cancelOrder ( final Long orderId ) {
        final Order order = orderRepository.findById( orderId )
                .orElseThrow( () -> new ResourceNotFoundException( "Order does not exist with id " + orderId ) );

        // UC13: Cancel Order alternative flow
        // Order can only be cancelled when it is in PLACED status
        if ( order.getStatus() != OrderStatus.PLACED ) {
            throw new IllegalStateException( "Order can only be cancelled when it is in PLACED status" );
        }

        order.setStatus( OrderStatus.CANCELLED );

        final Order savedOrder = orderRepository.save( order );
        return OrderMapper.mapToOrderDto( savedOrder );
    }

    /**
     * List all orders for a given customer
     *
     * @param username
     *            the customer's username
     * @return list of orders for a customer
     */
    @Override
    public List<OrderDto> getCustomersOrders ( final String username ) {
        final List<Order> orders = orderRepository.findAllByCustomerUsername( username );
        return orders.stream().map( OrderMapper::mapToOrderDto ).toList();
    }

}
