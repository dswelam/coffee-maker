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
 */
public interface OrderService {
	
    /**
     * Create and save a new order
     *
     * @param orderDto
     *            order to create
     * @return the DTO for the order including an ID
     */
	public OrderDto createOrder(OrderDto orderDto);

    /**
     * TODO: Would it be better to name this method something like "checkInventory" instead?
     * Check if the inventory has sufficient quantity of ingredients to make the item and deducts 
     * the appropriate quantity of ingredients from the inventory to make the item if so
     * @param inventoryDto the inventory instance 
     * @param itemDto the item to make
     * @return true if the item can be made, false if not
     */
    public boolean placeOrder ( InventoryDto inventoryDto, ItemDto itemDto );

    /**
     * Checks that the payment is sufficient and calculates the change
     * @param itemId the id of the item // TODO: Can't there be multiple items on an order?
     * @param tip // TODO: Why is this needed?
     * @param payment the amount paid by the customer
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
	OrderDto getOrderById(Long orderId);

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
     * TODO
     * @param orderId
     */
    public void deleteOrder ( Long orderId );

    /**
     * TODO
     * @param status
     * @return
     */
    public List<OrderDto> listOrders ( OrderStatus status );

    /**
     * TODO
     * @param orderId
     * @param staffId
     * @return
     */
    public OrderDto prepareOrder ( Long orderId, Long staffId );

    /**
     * TODO
     * @param orderId
     * @param staffId
     * @return
     */
    public OrderDto markReady ( Long orderId, Long staffId );

    /**
     * TODO
     * @param orderId
     * @return
     */
    public OrderDto orderFulfilled ( Long orderId );

    /**
     * TODO
     * @param orderId
     * @param byCustomer
     * @return
     */
    public OrderDto orderCancelled ( Long orderId, boolean byCustomer );

    /**
     * TODO
     * @param customerId
     * @return
     */
    public List<OrderDto> listMyOrders ( Long customerId );
}
