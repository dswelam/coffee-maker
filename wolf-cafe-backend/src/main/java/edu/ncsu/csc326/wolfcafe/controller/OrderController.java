package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.entity.Order.OrderStatus;
import edu.ncsu.csc326.wolfcafe.service.OrderService;
import lombok.AllArgsConstructor;

/**
 * Controller for Orders. Contains CRUD operations.
 *
 * @author Brooke Wu (bwu25)
 * @author Dania Swelam (dswelam)
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
     * REST API method to provide POST access to the Order model.
     *
     * @param orderDto
     *            The valid Order to be saved.
     * @param authentication
     *            The authentication object of the requester.
     * @return ResponseEntity indicating success if the Order could be saved to
     *         the inventory, or an error if it could not be
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN', 'CUSTOMER', 'ANONYMOUS')" )
    @PostMapping
    public ResponseEntity<OrderDto> createOrder ( @RequestBody final OrderDto orderDto,
            final Authentication authentication ) {
        // If the user is authenticated, set the customer username on the order
        final String username = authentication.getName();
        final OrderDto savedOrderDto = orderService.createOrder( orderDto, username );
        return new ResponseEntity<>( savedOrderDto, HttpStatus.CREATED );
    }

    /**
     * REST API method to provide PUT access to the Order model.
     *
     * @param orderId
     *            the id of the order to update
     * @param orderDto
     *            The valid Order to be updated.
     * @return ResponseEntity indicating success if the Order could be updated,
     *         or an error if it could not be.
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN', 'CUSTOMER', 'ANONYMOUS')" )
    @PutMapping ( "{id}" )
    public ResponseEntity<OrderDto> updateOrder ( @PathVariable ( "id" ) final Long orderId,
            @RequestBody final OrderDto orderDto ) {
        if ( orderService.getOrderById( orderId ) == null ) {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }
        final OrderDto savedOrderDto = orderService.updateOrder( orderId, orderDto );
        return ResponseEntity.ok( savedOrderDto );
    }

    /**
     * REST API method to allow deleting a Order from the CoffeeMaker's
     * Inventory, by making a DELETE request to the API endpoint and indicating
     * the order to delete (as a path variable)
     *
     * @param orderId
     *            The id of the Order to delete
     * @return Success if the order could be deleted; an error if the order does
     *         not exist
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @DeleteMapping ( "{id}" )
    public ResponseEntity<String> deleteOrder ( @PathVariable ( "id" ) final Long orderId ) {
        orderService.deleteOrder( orderId );
        return ResponseEntity.ok( "Order deleted successfully." );
    }

    /**
     * REST API method to list orders by status.
     *
     * @param status
     *            Status of orders to list
     * @return List of orders with the given status
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN', 'BARISTA')" )
    @GetMapping ( "/queue" )
    public ResponseEntity<List<OrderDto>> listQueue ( @RequestParam ( "status" ) final OrderStatus status ) {
        final List<OrderDto> orders = orderService.listOrders( status );
        return ResponseEntity.ok( orders );
    }

    /**
     * REST API method to prepare an order. Changes the order status to
     * IN_PROGRESS and assigns it to the barista member.
     *
     * @param orderId
     *            ID of the order to prepare
     * @return The updated order
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN', 'BARISTA')" )
    @PutMapping ( "/{id}/prepare" )
    public ResponseEntity<OrderDto> prepareOrder ( @PathVariable ( "id" ) final Long orderId ) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String staffUsername = auth.getName();
        final OrderDto order = orderService.prepareOrder( orderId, staffUsername );
        return ResponseEntity.ok( order );
    }

    /**
     * REST API method to mark an order as ready. Changes the order status to
     * READY when the order is prepared.
     *
     * @param orderId
     *            ID of the order to mark as ready
     * @return The updated order
     */
    @PreAuthorize ( "hasAnyRole('BARISTA', 'STAFF')" )
    @PutMapping ( "/{id}/ready" )
    public ResponseEntity<OrderDto> markReady ( @PathVariable ( "id" ) final Long orderId ) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String staffUsername = auth.getName();
        final OrderDto order = orderService.markReady( orderId, staffUsername );
        return ResponseEntity.ok( order );
    }

    /**
     * REST API method to mark an order as fullfilled. Changes the order status
     * to FULLFILLED when the customer picks up the order.
     *
     * @param orderId
     *            ID of the order to mark as fulfilled
     * @return The updated order
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN', 'CUSTOMER', 'BARISTA')" )
    @PutMapping ( "/{id}/fulfill" )
    public ResponseEntity<OrderDto> orderFulfilled ( @PathVariable ( "id" ) final Long orderId ) {
        final OrderDto order = orderService.orderFulfilled( orderId );
        return ResponseEntity.ok( order );
    }

    /**
     * REST API method to cancel an order.
     *
     * @param orderId
     *            ID of the order to cancel
     * @return The updated order
     */
    @PreAuthorize ( "hasAnyRole('CUSTOMER')" )
    @PutMapping ( "/{id}/cancel" )
    public ResponseEntity<OrderDto> orderCancelled ( @PathVariable ( "id" ) final Long orderId ) {
        final OrderDto order = orderService.cancelOrder( orderId );
        return ResponseEntity.ok( order );
    }

    /**
     * REST API method to list orders for the authenticated customer.
     *
     * @return List of orders for the authenticated customer
     */
    @PreAuthorize ( "hasAnyRole('CUSTOMER')" )
    @GetMapping ( "/myorders" )
    public ResponseEntity<List<OrderDto>> listMyOrders () {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String username = auth.getName();
        final List<OrderDto> orders = orderService.getCustomersOrders( username );
        return ResponseEntity.ok( orders );
    }

}
