package edu.ncsu.csc326.wolfcafe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.Item;

/**
 * Repository interface for Items.
 *
 * @author Dania Swelam
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Finds a Item object with the provided name. Spring will generate code to
     * make this happen. Optional let's us call .orElseThrow() when a client
     * works with the method and the value isn't found in the database.
     *
     * @param name
     *            Name of the item to find.
     * @return Found item, null if none.
     */
    Optional<Item> findByName ( String name );
}
