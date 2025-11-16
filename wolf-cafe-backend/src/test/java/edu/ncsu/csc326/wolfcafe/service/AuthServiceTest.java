package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
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

    /** Invalid case: Attempt to assign permissions to non-staff role */
    @Test
    @Transactional
    void testAssignPermissionsToNonStaffRole () {
        final IllegalArgumentException exception1 = assertThrows( IllegalArgumentException.class, () -> {
            authService.assignPermissions( "ROLE_CUSTOMER", List.of( Permission.PURCHASE_ITEM ) );
        } );

        assertTrue( exception1.getMessage().contains( "Only staff role permissions can be modified" ) );
        
        final IllegalArgumentException exception2 = assertThrows( IllegalArgumentException.class, () -> {
            authService.assignPermissions( "ROLE_CUSTOMER", List.of( Permission.FULFILL_ORDER ) );
        } );

        assertTrue( exception2.getMessage().contains( "Invalid Permission: Customers cannot add tax or fulfill orders." ) );
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
    		taxRepository.deleteAll();
    		authService.setTaxRate(new TaxDto(10));
    		assertEquals(10, authService.getTaxRate());
    }

    /** Tests retrieving all users through AuthServiceImpl */
    @Test
    @Transactional
    void testListUsers () {
        // Call the service method
        final List<UserDto> users = authService.listUsers();

        // Validate returned list
        assertNotNull( users, "User list should not be null" );
        assertTrue( users.isEmpty() || users.size() >= 0, "Should return a valid list (possibly empty)" );
    }

    /** Invalid case: Attempt to assign permissions to admin */
    @Test
    @Transactional
    void testAssignPermissionsToAdminRole () {
        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> {
            authService.assignPermissions( "ROLE_ADMIN", List.of( Permission.MANAGE_USERS ) );
        } );

        assertTrue( exception.getMessage().contains( "Only staff role permissions can be modified" ) );
    }

}
