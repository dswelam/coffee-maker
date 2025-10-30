package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.service.OrderService;
import lombok.AllArgsConstructor;

/**
 * Controller for Orders. Contains CRUD operations.
 *
 * @author Brooke Wu (bwu25)
 */
@CrossOrigin ( "*" )
@RestController
@AllArgsConstructor
@RequestMapping ( "/api/orders" )
public class OrderController {

    /** Link to OrderService */
    @Autowired
    private final OrderService orderService;
    
    /**
     * TODO
     * @param itemName
     * @param amtPaid
     * @return
     */
    public ResponseEntity<OrderDto> orderItem(String itemName, Integer amtPaid) {
    	 	// TODO
    		return null;
    }
    
    /**
     * TODO
     * @param toPurchase
     * @param amtPaid
     * @return
     */
    public int orderItem(ItemDto toPurchase, int amtPaid) {
    		// TODO
    		return 0;
    }

    /**
     * REST API method to provide POST access to the Order model.
     *
     * @param orderDto
     *            The valid Order to be saved.
     * @return ResponseEntity indicating success if the Order could be
     *         saved to the inventory, or an error if it could not be
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @PostMapping
    public ResponseEntity<OrderDto> createOrder ( @RequestBody final OrderDto orderDto ) {
        // TODO
    		return null;
    }

    /**
     * REST API method to provide PUT access to the Order model.
     *
     * @param orderId
     *            the id of the order to update
     * @param orderDto
     *            The valid Order to be updated.
     * @return ResponseEntity indicating success if the Order could be
     *         updated, or an error if it could not be.
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @PutMapping ( "{id}" )
    public ResponseEntity<OrderDto> updateOrder ( @PathVariable ( "id" ) final Long orderId,
            @RequestBody final OrderDto orderDto ) {
        // TODO
    		return null;
    }

    /**
     * REST API method to allow deleting a Order from the CoffeeMaker's
     * Inventory, by making a DELETE request to the API endpoint and indicating
     * the order to delete (as a path variable)
     *
     * @param orderId
     *            The id of the Order to delete
     * @return Success if the order could be deleted; an error if the
     *         order does not exist
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @DeleteMapping ( "{id}" )
    public ResponseEntity<String> deleteOrder ( @PathVariable ( "id" ) final Long orderId ) {
        orderService.deleteOrder( orderId );
        return ResponseEntity.ok( "Order deleted successfully." );
    }
    
    /**
     * TODO
     * @param status
     * @return
     */
    public ResponseEntity<OrderDto> listQueue(String status) {
    		// TODO
    		return null;
    }
    
    /**
     * TODO
     * @param orderId
     * @param staffId
     * @return
     */
    public ResponseEntity<OrderDto> prepareOrder(Long orderId, Long staffId) {
    		// TODO
    		return null;
    }
    
    /**
     * TODO
     * @param orderId
     * @param staffId
     * @return
     */
    public ResponseEntity<OrderDto> markReady(Long orderId, Long staffId) {
    		// TODO
    		return null;
    }
    
    /**
     * TODO
     * @param orderId
     * @return
     */
    public ResponseEntity<OrderDto> orderFulfilled(Long orderId) {
    		// TODO
    		return null;
    }
    
    /**
     * TODO
     * @param orderId
     * @param byCustomer
     * @return
     */
    public ResponseEntity<OrderDto> orderCancelled(Long orderId, boolean byCustomer) {
    		// TODO
    		return null;
    }
    
    /**
     * TODO
     * @return
     */
    public ResponseEntity<List<OrderDto>> listMyOrders() {
    		// TODO
    		return null;
    }
}
