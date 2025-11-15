package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderLineDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.Order.OrderStatus;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.IngredientMapper;
import edu.ncsu.csc326.wolfcafe.mapper.ItemMapper;
import edu.ncsu.csc326.wolfcafe.mapper.OrderMapper;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.OrderService;

/**
 * Implementation of the OrderService interface.
 *
 * @author Brooke Wu bwu25
 */
@Service
public class OrderServiceImpl implements OrderService {

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private OrderRepository orderRepository;
    
    /** Connection to the inventory service to update the inventory */
    @Autowired
    private InventoryService inventoryService;
    
	@Override
	public OrderDto createOrder(OrderDto orderDto) {
		InventoryDto inventory = inventoryService.getInventory();
		
		// Before creating the order, need to check if the inventory has sufficient ingredients to make the items on the order
		for (OrderLineDto orderLine : orderDto.getOrderItems()) {
			ItemDto item = ItemMapper.mapToItemDto(orderLine.getItem());
			if (!placeOrder(inventory, item)) {
				// TODO: How to handle not being able to create this specific item on the order?
			}
		}
		
		// TODO: Is this method also responsible for checking if the payment is sufficient? There is no payment
		// property on an OrderDto
		
		// Save the order to the database
		Order order = OrderMapper.mapToOrder( orderDto );
        Order savedOrder = orderRepository.save( order );
        return OrderMapper.mapToOrderDto( savedOrder );
	}

	@Override
	public boolean placeOrder(InventoryDto inventoryDto, ItemDto itemDto) {
		InventoryDto updatedInventory = inventoryDto;
		
		// First, loop through each ingredient needed for the item
		for (Entry<String, Integer> entry : itemDto.getIngredients().entrySet()) {
			String ingredientName = entry.getKey();
			Integer quantityNeeded = entry.getValue();
			
			// Check if this ingredient can be found in the inventory
			boolean ingredientFoundInInventory = false;
			
			for (Entry<String, Integer> inventoryEntry : updatedInventory.getIngredients().entrySet()) {
				if (inventoryEntry.getKey().equals(ingredientName)) {
					ingredientFoundInInventory = true;
					
					// If the ingredient was found in the inventory, check if the quantity in the inventory is sufficient for the item
					if (inventoryEntry.getValue() >= quantityNeeded) {
						// If so, subtract the needed quantity of the ingredient from the inventory 
						updatedInventory.getIngredients().put(ingredientName, inventoryEntry.getValue() - quantityNeeded);
					}
					else {
						return false;
					}
				}
			}
			
			if (!ingredientFoundInInventory) return false;
		}
		
		// Save the updated inventory
		updatedInventory = inventoryService.updateInventory(updatedInventory);
		
		return true;
	}

	@Override
	public int placeOrder(Long itemId, int tip, int payment) {
		// TODO Auto-generated method stub
		return 0;
	}
	
    @Override
    public OrderDto getOrderById ( Long orderId ) {
        Order order = orderRepository.findById( orderId ).orElseThrow(
                () -> new ResourceNotFoundException( "Order does not exist with id " + orderId ) );
        return OrderMapper.mapToOrderDto( order );
    }

	@Override
	public OrderDto updateOrder(Long orderId, OrderDto orderDto) {
        Order order = orderRepository.findById( orderId ).orElseThrow(
                () -> new ResourceNotFoundException( "Order does not exist with id " + orderId ) );

        order = OrderMapper.mapToOrder(orderDto);

        Order savedOrder = orderRepository.save( order );

        return OrderMapper.mapToOrderDto( savedOrder );
	}

	@Override
	public void deleteOrder(Long orderId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<OrderDto> listOrders(OrderStatus status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderDto prepareOrder(Long orderId, Long staffId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderDto markReady(Long orderId, Long staffId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderDto orderFulfilled(Long orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderDto orderCancelled(Long orderId, boolean byCustomer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OrderDto> listMyOrders(Long customerId) {
		// TODO Auto-generated method stub
		return null;
	}



}
