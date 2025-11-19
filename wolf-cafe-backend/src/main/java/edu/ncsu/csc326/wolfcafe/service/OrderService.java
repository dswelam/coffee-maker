package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.entity.Order.OrderStatus;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;

/**
 * Interface defining Order behaviors
 *
 * @author Brooke Wu (bwu25)
 * @author Dania Swelam (dswelam)
 */
public interface OrderService {

    /**
     * Create and save a new order
     *
     * @param orderDto
     *            order to create
     * @return the DTO for the order including an ID
     */
    public OrderDto createOrder ( OrderDto orderDto );

    /**
     * Check if the inventory has sufficient quantity of ingredients to make the
     * item and deducts the appropriate quantity of ingredients from the
     * inventory to make the item if so
     *
     * @param inventoryDto
     *            the inventory instance
     * @param itemDto
     *            the item to make
     * @return true if the item can be made, false if not
     */
    public boolean checkInventory ( InventoryDto inventoryDto, ItemDto itemDto );

    /**
     * Checks that the payment is sufficient and calculates the change
     *
     * @param itemId
     *            the id of the item
     * @param tip
     *            the tip amount
     * @param payment
     *            the amount paid by the customer
     * @return change to return to the user
     */
    public int placeOrder ( Long itemId, int tip, int payment );

    /**
     * Get an order by its ID
     *
     * @param orderId
     *            id of the order to get
     * @return the DTO for the order
     * @throws ResourceNotFoundException
     *             if the order doesn't exist
     */
    OrderDto getOrderById ( Long orderId );

    /**
     * Update an order with all new fields
     *
     * @param orderId
     *            id of the order to update
     * @param orderDto
     *            order to replace old one with
     * @return the DTO for the order
     * @throws ResourceNotFoundException
     *             if the order doesn't exist
     */
    public OrderDto updateOrder ( Long orderId, OrderDto orderDto );

    /**
     * Delete an order by its ID
     *
     * @param orderId
     *            id of the order to delete
     * @throws ResourceNotFoundException
     *             if the order doesn't exist
     */
    public void deleteOrder ( Long orderId );

    /**
     * List orders by their status
     *
     * @param status
     *            the status to filter by
     * @return list of orders with the given status
     */
    public List<OrderDto> listOrders ( OrderStatus status );

    /**
     * Action to prepare an order
     *
     * @param orderId
     *            The order to prepare by ID
     * @param staffUsername
     *            The staff member preparing the order by username
     * @return The updated order DTO
     */
    public OrderDto prepareOrder ( Long orderId, String staffUsername );

    /**
     * Order is marked as ready for pickup
     *
     * @param orderId
     *            The order to mark as ready by ID
     * @param staffUsername
     *            The staff member marking the order as ready by username
     * @return The updated order DTO
     */
    public OrderDto markReady ( Long orderId, String staffUsername );

    /**
     * Action to mark an order as fulfilled
     *
     * @param orderId
     *            The order to mark as fulfilled by ID
     * @return The updated order DTO
     */
    public OrderDto orderFulfilled ( Long orderId );

    /**
     * Action to cancel an order
     *
     * @param orderId
     *            The order to cancel by ID
     * @return The updated order DTO
     */
    public OrderDto cancelOrder ( Long orderId );

    /**
     * List all orders for a given customer
     *
     * @param username
     *            the customer's username
     * @return list of orders for a customer
     */
    public List<OrderDto> getCustomersOrders ( String username );
}
