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
	 * TODO
	 * @param inventoryDto
	 * @param itemDto
	 * @return
	 */
	public boolean placeOrder(InventoryDto inventoryDto, ItemDto itemDto);

	/**
	 * TODO
	 * @param itemId
	 * @param tip
	 * @param payment
	 * @return
	 */
	public int placeOrder(Long itemId, int tip, int payment);
	
	/**
	 * TODO
	 * @param orderId
	 * @param orderDto
	 * @return
	 */
	public OrderDto updateOrder(Long orderId, OrderDto orderDto);

	/**
	 * TODO
	 * @param orderId
	 */
	public void deleteOrder(Long orderId);
	
	/**
	 * TODO
	 * @param status
	 * @return
	 */
	public List<OrderDto> listOrders(OrderStatus status);

	/**
	 * TODO
	 * @param orderId
	 * @param staffId
	 * @return
	 */
	public OrderDto prepareOrder(Long orderId, Long staffId);
	
	/**
	 * TODO
	 * @param orderId
	 * @param staffId
	 * @return
	 */
	public OrderDto markReady(Long orderId, Long staffId);

	/**
	 * TODO
	 * @param orderId
	 * @return
	 */
	public OrderDto orderFulfilled(Long orderId);
	
	/**
	 * TODO
	 * @param orderId
	 * @param byCustomer
	 * @return
	 */
	public OrderDto orderCancelled(Long orderId, boolean byCustomer);
	
	/**
	 * TODO
	 * @param customerId
	 * @return
	 */
	public List<OrderDto> listMyOrders(Long customerId);
}
