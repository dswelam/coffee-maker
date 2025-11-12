package edu.ncsu.csc326.wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import edu.ncsu.csc326.wolfcafe.entity.Tax;
import jakarta.persistence.EntityManager;

/**
 * Tests TaxRepository. Uses the real database - not an embedded one.
 */
@DataJpaTest
@AutoConfigureTestDatabase ( replace = Replace.NONE )
public class TaxRepositoryTest {

    /** Reference to tax repository */
    @Autowired
    private TaxRepository taxRepository;

    /** Reference to EntityManager */
    @Autowired
    private TestEntityManager   testEntityManager;

    /** Reference to tax */
    private Tax           tax;

    /**
     * Sets up the test case. We assume only one tax row.
     *
     * @throws java.lang.Exception
     *             if error
     * 
     *             Source: https://www.geeksforgeeks.org/sql/
     *             truncate-tables-with-dependent-foreign-key-constraints-in-sql/
     */
    @BeforeEach
    public void setUp () throws Exception {
        EntityManager entityManager = testEntityManager.getEntityManager();
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 0" ).executeUpdate();
        entityManager.createNativeQuery( "TRUNCATE TABLE tax" ).executeUpdate();
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 1" ).executeUpdate();

        // Make sure that Tax always has an id of 1L.
        tax = new Tax( 2.00 );
        taxRepository.save( tax );
    }

    /**
     * Test saving the tax and retrieving from the repository.
     */
    @Test
    public void testSaveAndGetTax () {
        Tax fetchedTax = taxRepository.findById( tax.getId() ).get();
        Tax expectedTax = new Tax(1L, 2.00);
        assertEquals( expectedTax.getId(), fetchedTax.getId() );
        assertEquals( expectedTax.getCurrentAmount(), fetchedTax.getCurrentAmount() );
    }

    /**
     * Tests updating the tax
     */
    @Test
    public void testUpdateTax () {
        Tax fetchedTax = taxRepository.findById( tax.getId() ).get();
        fetchedTax.setId(2L);
        fetchedTax.setCurrentAmount(5.25);

        Tax updatedTax = taxRepository.save( fetchedTax );
        assertEquals( 2L, updatedTax.getId() );
        assertEquals( 5.25, updatedTax.getCurrentAmount() );
    }
}
