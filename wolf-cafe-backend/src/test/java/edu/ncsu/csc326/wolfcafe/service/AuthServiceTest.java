package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.TaxDto;
import edu.ncsu.csc326.wolfcafe.entity.Permission;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.repository.TaxRepository;
import jakarta.persistence.EntityManager;

/**
 * Tests AuthServiceImpl
 *
 * @author Diya Patel
 */
@SpringBootTest
public class AuthServiceTest {

    /** the authorization service */
    @Autowired
    private AuthService    authService;

    /** the role repository */
    @Autowired
    private RoleRepository roleRepository;
    
    /** the tax repository */
    @Autowired
    private TaxRepository taxRepository;

    /** Entity manager used to run custom SQL cleanup queries */
    @Autowired
    private EntityManager  entityManager;

    /**
     * the set up before each test case
     */
    @BeforeEach
    public void setUp () {
        // Clear user-role associations first
        entityManager.createNativeQuery( "DELETE FROM users_roles" ).executeUpdate();
        entityManager.createNativeQuery( "DELETE FROM roles" ).executeUpdate();

        // Recreate test roles
        final Role admin = new Role();
        admin.setName( "ROLE_ADMIN" );
        roleRepository.save( admin );

        final Role staff = new Role();
        staff.setName( "ROLE_STAFF" );
        roleRepository.save( staff );

        final Role customer = new Role();
        customer.setName( "ROLE_CUSTOMER" );
        roleRepository.save( customer );
    }

    /** Valid case: Assign permissions to STAFF */
    @Test
    @Transactional
    void testAssignPermissionsToStaff () {
        final Role updated = authService.assignPermissions( "ROLE_STAFF",
                List.of( Permission.ADD_INVENTORY, Permission.FULFILL_ORDER ) );

        assertNotNull( updated );
        assertEquals( "ROLE_STAFF", updated.getName() );
        assertTrue( updated.getPermissions().contains( Permission.ADD_INVENTORY ) );
        assertTrue( updated.getPermissions().contains( Permission.FULFILL_ORDER ) );
    }

    /** Invalid case: Assign forbidden permission to CUSTOMER */
    @Test
    @Transactional
    void testAssignInvalidPermissionToCustomer () {
        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> {
            authService.assignPermissions( "ROLE_CUSTOMER", List.of( Permission.FULFILL_ORDER ) );
        } );

        assertTrue( exception.getMessage().contains( "Invalid Permission" ) );
    }

    /** Role not found */
    @Test
    @Transactional
    void testAssignPermissionsRoleNotFound () {
        assertThrows( ResourceNotFoundException.class, () -> {
            authService.assignPermissions( "ROLE_UNKNOWN", List.of( Permission.ADD_INVENTORY ) );
        } );
    }
    
    /** Test getting and setting the current tax rate in the system
     * @author Brooke Wu */
    @Test
    @Transactional
    void testSetTaxRate() {
    		assertEquals(2, authService.getTaxRate());
    		authService.setTaxRate(new TaxDto(5));
    		assertEquals(5, authService.getTaxRate());
    }
}
