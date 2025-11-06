package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Order.OrderStatus;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.IngredientMapper;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.service.IngredientService;
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

	@Override
	public boolean placeOrder(InventoryDto inventoryDto, ItemDto itemDto) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int placeOrder(Long itemId, int tip, int payment) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public OrderDto updateOrder(Long orderId, OrderDto orderDto) {
		// TODO Auto-generated method stub
		return null;
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
