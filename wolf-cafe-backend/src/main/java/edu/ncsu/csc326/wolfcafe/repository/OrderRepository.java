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
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

	/**
	 * TODO
	 * @param id
	 * @return
	 */
    Optional<Order> findById(Long id);
    
    /**
     * TODO
     * @param status
     * @return
     */
    List<Order> findAllByStatus(OrderStatus status);

    /**
     * TODO
     * @param customerId
     * @return
     */
    List<Order> findAllByCustomerId(Long customerId);
    
    /**
     * TODO
     * @param order
     * @return
     */
    Order save(Order order);
    
    /**
     * TODO
     * @param id
     * @return
     */
    void deleteById(Long id);
    
}
