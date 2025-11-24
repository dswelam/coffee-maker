package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderLineDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.Order.OrderStatus;
import edu.ncsu.csc326.wolfcafe.entity.OrderLine;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.ItemMapper;
import edu.ncsu.csc326.wolfcafe.mapper.OrderMapper;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
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

    /** Connection to the item repository to get item info */
    @Autowired
    private ItemRepository   itemRepository;

    /**
     * Creates a new order.
     * @param orderDto
     *           the order DTO
     * @param username
     *           the customer's username
     *
     * @return the created order DTO
     */
    @Override
    public OrderDto createOrder ( final OrderDto orderDto, final String username ) {
        User customer = null;
        // Handle anonymous users
        if ( !"Anonymous User".equals( username ) ) {
            customer = userRepository.findByUsername( username ).orElseThrow(
                    () -> new ResourceNotFoundException( "User does not exist with username " + username ) );
        }
        InventoryDto inventory = inventoryService.getInventory();

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

        // Fetch the full Item entity from DB for each order line
        for ( final OrderLineDto orderLine : orderItems ) {
            final Item item = itemRepository.findById( orderLine.getItem().getId() )
                    .orElseThrow( () -> new ResourceNotFoundException( "Item not found" ) );
            orderLine.setItem( item );
        }

        // Deduct ingredients from inventory
        for ( final OrderLineDto orderLine : orderItems ) {
            final ItemDto item = ItemMapper.mapToItemDto( orderLine.getItem() );
            inventory = deductIngredients( inventory, item, orderLine.getQuantity() );
        }

        // Update inventory in database
        inventoryService.updateInventoryForOrder( inventory );

        // Set the order status to PLACED - this was missing previously
        orderDto.setStatus( OrderStatus.PLACED );

        // Save the order to the database
        final Order order = OrderMapper.mapToOrder( orderDto );
        order.setCustomer( customer );
        order.setPreparedBy( null );
        final Order savedOrder = orderRepository.save( order );
        return OrderMapper.mapToOrderDto( savedOrder );
    }

    /**
     * Deducts the ingredients of an item from the inventory.
     * @param inventory the current inventory
     * @param item the item to deduct ingredients for
     * @param quantity the quantity of the item
     * @return the updated inventory
     */
    private InventoryDto deductIngredients ( final InventoryDto inventory, final ItemDto item, final int quantity ) {
        final Map<String, Integer> updatedIngredients = new HashMap<>( inventory.getIngredients() );

        for ( final Map.Entry<String, Integer> entry : item.getIngredients().entrySet() ) {
            final String ingredientName = entry.getKey();
            final Integer quantityNeededPerItem = entry.getValue();
            final Integer totalQuantityNeeded = quantityNeededPerItem * quantity;

            final Integer currentAmount = updatedIngredients.get( ingredientName );
            if ( currentAmount != null && currentAmount >= totalQuantityNeeded ) {
                updatedIngredients.put( ingredientName, currentAmount - totalQuantityNeeded );
            }
        }

        final InventoryDto updatedInventory = new InventoryDto();
        updatedInventory.setId( inventory.getId() );
        updatedInventory.setIngredients( updatedIngredients );

        return updatedInventory;
    }

    /**
     * Checks if the inventory has sufficient ingredients for the item.
     * @param inventoryDto the inventory DTO
     * @param itemDto the item DTO
     * @return true if the inventory has sufficient ingredients, false otherwise
     */
    @Override
    public boolean checkInventory ( final InventoryDto inventoryDto, final ItemDto itemDto ) {
        // First, loop through each ingredient needed for the item
        for ( final Entry<String, Integer> entry : itemDto.getIngredients().entrySet() ) {
            final String ingredientName = entry.getKey();
            final Integer quantityNeeded = entry.getValue();

            // Check if this ingredient exists in inventory with sufficient
            // quantity
            final Integer availableQuantity = inventoryDto.getIngredients().get( ingredientName );

            if ( availableQuantity == null || availableQuantity < quantityNeeded ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId
     *            the order ID
     * @return the order DTO
     */
    @Override
    public OrderDto getOrderById ( final Long orderId ) {
        final Order order = orderRepository.findById( orderId )
                .orElseThrow( () -> new ResourceNotFoundException( "Order does not exist with id " + orderId ) );
        return OrderMapper.mapToOrderDto( order );
    }

    /**
     * Updates an existing order.
     *
     * @param orderId
     *            the order ID
     * @param updatedOrderDto
     *            the updated order DTO
     * @return the updated order DTO
     */
    @Override
    public OrderDto updateOrder ( final Long orderId, final OrderDto updatedOrderDto ) {
        final Order existingOrder = orderRepository.findById( orderId )
                .orElseThrow( () -> new ResourceNotFoundException( "Order not found with id: " + orderId ) );

        // Only allow updates to PLACED orders
        if ( existingOrder.getStatus() != OrderStatus.PLACED ) {
            throw new IllegalStateException( "Can only update orders that are in PLACED status" );
        }

        // Calculate the difference in ingredients needed
        InventoryDto currentInventory = inventoryService.getInventory();

        // First, restore ingredients from the original order
        for ( final OrderLine originalOrderLine : existingOrder.getOrderItems() ) {
            final ItemDto originalItem = ItemMapper.mapToItemDto( originalOrderLine.getItem() );

            // Add back ingredients from original quantity
            for ( final Map.Entry<String, Integer> entry : originalItem.getIngredients().entrySet() ) {
                final String ingredientName = entry.getKey();
                final Integer ingredientAmount = entry.getValue() * originalOrderLine.getQuantity();

                final Map<String, Integer> ingredients = new HashMap<>( currentInventory.getIngredients() );
                ingredients.put( ingredientName, ingredients.get( ingredientName ) + ingredientAmount );
                currentInventory.setIngredients( ingredients );
            }
        }

        // Then, deduct ingredients for the new order
        for ( final OrderLineDto newOrderLine : updatedOrderDto.getOrderItems() ) {
            final ItemDto newItem = ItemMapper.mapToItemDto( newOrderLine.getItem() );
            currentInventory = deductIngredients( currentInventory, newItem, newOrderLine.getQuantity() );
        }

        // Update inventory in database
        inventoryService.updateInventoryForOrder( currentInventory );

        // Update the order
        existingOrder.setStatus( updatedOrderDto.getStatus() );

        // Clear existing order items and add new ones
        existingOrder.getOrderItems().clear();
        for ( final OrderLineDto orderLineDto : updatedOrderDto.getOrderItems() ) {
            final OrderLine orderLine = new OrderLine();
            orderLine.setItem( orderLineDto.getItem() );
            orderLine.setQuantity( orderLineDto.getQuantity() );
            orderLine.setOrder( existingOrder );
            existingOrder.getOrderItems().add( orderLine );
        }

        final Order savedOrder = orderRepository.save( existingOrder );
        return OrderMapper.mapToOrderDto( savedOrder );
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

        // Load current inventory and merge returned quantities into it
        final InventoryDto inventory = inventoryService.getInventory();
        final Map<String, Integer> merged = new HashMap<>(
                inventory.getIngredients() != null ? inventory.getIngredients() : Map.of() );

        // Loop through each order line and add back the ingredients to
        // inventory
        for ( final OrderLine line : order.getOrderItems() ) {
            final Item item = line.getItem();
            final int qty = line.getQuantity();
            for ( final Map.Entry<String, Integer> ent : item.getIngredients().entrySet() ) {
                final String name = ent.getKey();
                final int amount = ent.getValue() * qty;
                merged.merge( name, amount, Integer::sum );
            }
        }

        inventory.setIngredients( merged );

        // use the same update method used when placing orders to persist
        // changes
        inventoryService.updateInventoryForOrder( inventory );

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
