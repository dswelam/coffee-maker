package edu.ncsu.csc326.wolfcafe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.Order.OrderStatus;

/**
 * OrderRepository for working with the DB through the JpaRepository.
 *
 * @author Brooke Wu
 * @author Dania Swelam
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds an order by its ID
     * @param id the ID of the order
     * @return an Optional containing the order if found, or empty if not found
     */
    @Override
    Optional<Order> findById ( Long id );

    /**
     * Lists all orders with a given status
     * @param status the status to filter by
     * @return list of orders with the given status
     */
    List<Order> findAllByStatus ( OrderStatus status );

    /**
     * Finds all orders by a given customer ID
     * @param customerId the customer ID
     * @return list of orders for the given customer ID
     */
    List<Order> findAllByCustomerId ( Long customerId );

    /**
     * Deletes an order by its ID
     * @param id the ID of the order to delete
     * @return void
     */
    @Override
    void deleteById ( Long id );

    /**
     * Finds all orders by a given customer's username
     * @param username the customer's username
     * @return list of orders for the given customer's username
     */
    List<Order> findAllByCustomerUsername ( String username );

}
