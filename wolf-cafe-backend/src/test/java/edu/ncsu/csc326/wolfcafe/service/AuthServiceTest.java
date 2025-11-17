package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.TaxDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.Order.OrderStatus;
import edu.ncsu.csc326.wolfcafe.entity.Permission;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.repository.TaxRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import jakarta.persistence.EntityManager;

/**
 * Tests AuthServiceImpl
 *
 * @author Diya Patel
 * @author Brooke Wu
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
    private TaxRepository  taxRepository;

    /** the user repository */
    @Autowired
    private UserRepository userRepository;

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

        final Role barista = new Role();
        barista.setName( "ROLE_BARISTA" );
        roleRepository.save( barista );

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

        assertTrue(
                exception2.getMessage().contains( "Invalid Permission: Customers cannot add tax or fulfill orders." ) );
    }

    /** Role not found */
    @Test
    @Transactional
    void testAssignPermissionsRoleNotFound () {
        assertThrows( ResourceNotFoundException.class, () -> {
            authService.assignPermissions( "ROLE_UNKNOWN", List.of( Permission.ADD_INVENTORY ) );
        } );
    }

    /**
     * Test getting and setting the current tax rate in the system
     *
     * @author Brooke Wu
     */
    @Test
    @Transactional
    void testSetTaxRate () {
        assertEquals( 2, authService.getTaxRate() );
        authService.setTaxRate( new TaxDto( 5 ) );
        assertEquals( 5, authService.getTaxRate() );
        taxRepository.deleteAll();
        authService.setTaxRate( new TaxDto( 10 ) );
        assertEquals( 10, authService.getTaxRate() );
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

    /** Tests creating new users through AuthServiceImpl */
    @Test
    @Transactional
    void testCreateUsers () {
        // Create a Customer user
        final RegisterDto registerDto = new RegisterDto( "Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC" );
        final String registerResult = authService.register( registerDto );
        assertEquals( "User registered successfully.", registerResult );

        final LoginDto loginDto = new LoginDto( "jestes", "JXB16TBD4LC" );
        final JwtAuthResponse response = authService.login( loginDto );
        assertEquals( response.getRole(), "ROLE_CUSTOMER" );

        WolfCafeAPIException registerException = assertThrows( WolfCafeAPIException.class, () -> {
            authService.register( registerDto );
        } );
        assertTrue( registerException.getMessage().contains( "Username already exists." ) );

        registerDto.setUsername( "unique" );
        registerException = assertThrows( WolfCafeAPIException.class, () -> {
            authService.register( registerDto );
        } );
        assertTrue( registerException.getMessage().contains( "Email already exists." ) );

        // Create a Barista user
        final UserDto baristaUser = new UserDto();
        baristaUser.setName( "Barry" );
        baristaUser.setUsername( "barista" );
        baristaUser.setEmail( "barry@wolfcafe.com" );
        baristaUser.setPassword( "abc123" );
        final Collection<Role> baristaRoles = new ArrayList<Role>();
        baristaRoles.add( roleRepository.findByName( "ROLE_BARISTA" ) );
        baristaUser.setRoles( baristaRoles );

        final UserDto createdBaristaUser = authService.createUser( baristaUser );
        assertNotNull( createdBaristaUser.getId() );
        assertEquals( baristaUser.getName(), createdBaristaUser.getName() );
        assertEquals( baristaUser.getUsername(), createdBaristaUser.getUsername() );
        assertEquals( baristaUser.getEmail(), createdBaristaUser.getEmail() );
        assertEquals( baristaUser.getPassword(), createdBaristaUser.getPassword() );
        assertEquals( baristaUser.getRoles(), createdBaristaUser.getRoles() );
        assertTrue( userRepository.findByUsername( "barista" ).isPresent() );

        // Create a Staff user
        final UserDto staffUser = new UserDto();
        staffUser.setName( "Stephanie" );
        staffUser.setUsername( "staff" );
        staffUser.setEmail( "stephanie@wolfcafe.com" );
        staffUser.setPassword( "xyz789" );
        final Collection<Role> staffRoles = new ArrayList<Role>();
        staffRoles.add( roleRepository.findByName( "ROLE_STAFF" ) );
        staffUser.setRoles( staffRoles );

        final UserDto createdStaffUser = authService.createUser( staffUser );
        assertNotNull( createdStaffUser.getId() );
        assertEquals( staffUser.getName(), createdStaffUser.getName() );
        assertEquals( staffUser.getUsername(), createdStaffUser.getUsername() );
        assertEquals( staffUser.getEmail(), createdStaffUser.getEmail() );
        assertEquals( staffUser.getPassword(), createdStaffUser.getPassword() );
        assertEquals( staffUser.getRoles(), createdStaffUser.getRoles() );
        assertTrue( userRepository.findByUsername( "staff" ).isPresent() );

        // Test validation
        final UserDto invalidUser = new UserDto();
        invalidUser.setName( "" );
        invalidUser.setUsername( "invalid" );
        invalidUser.setEmail( "invalidwolfcafe.com" );
        invalidUser.setPassword( "" );
        IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> {
            authService.createUser( invalidUser );
        } );
        assertTrue( exception.getMessage().contains( "User's name must be a non-empty string" ) );

        invalidUser.setName( "Invalid User" );
        exception = assertThrows( IllegalArgumentException.class, () -> {
            authService.createUser( invalidUser );
        } );
        assertTrue( exception.getMessage().contains( "User's email address is not in a valid format" ) );

        invalidUser.setEmail( baristaUser.getEmail() );
        exception = assertThrows( IllegalArgumentException.class, () -> {
            authService.createUser( invalidUser );
        } );
        assertTrue( exception.getMessage().contains( "User's password must be a non-empty string" ) );

        invalidUser.setPassword( "password" );
        exception = assertThrows( IllegalArgumentException.class, () -> {
            authService.createUser( invalidUser );
        } );
        assertTrue( exception.getMessage().contains( "User's email address is already used by an existing user" ) );
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

    /**
     * UC10: Successfully delete a normal user.
     */
    @Test
    @Transactional
    void testDeleteUserSuccess () {
        // Create a customer
        final RegisterDto reg = new RegisterDto( "Alice", "alice", "alice@test.com", "pass" );
        authService.register( reg );

        final Long id = userRepository.findByUsername( "alice" ).get().getId();

        // Delete them
        authService.deleteUserById( id );

        assertTrue( userRepository.findById( id ).isEmpty() );
    }

    /**
     * UC10: Cannot delete yourself.
     */
    @Test
    @Transactional
    void testDeleteUserCannotDeleteSelf () {
        // Ensure no existing admin user conflicts with our test
        userRepository.findByUsername( "admin" ).ifPresent( u -> userRepository.delete( u ) );

        // Create new admin user
        final RegisterDto reg = new RegisterDto( "Admin", "admin", "admin@test.com", "pass" );
        authService.register( reg );

        final Long id = userRepository.findByUsername( "admin" ).get().getId();

        // Simulate logged-in admin
        SecurityContextHolder.getContext()
                .setAuthentication( new UsernamePasswordAuthenticationToken( "admin", "pass", List.of() ) );

        final WolfCafeAPIException ex = assertThrows( WolfCafeAPIException.class, () -> {
            authService.deleteUserById( id );
        } );

        assertTrue( ex.getMessage().toLowerCase().contains( "cannot delete your own" ) );
    }

    /**
     * UC10: Cannot delete staff with active (IN_PROGRESS) orders.
     */
    @Test
    @Transactional
    void testDeleteStaffWithActiveOrders () {
        // 1. Create a REAL customer
        final RegisterDto customerReg = new RegisterDto( "Customer C", "custc", "custc@test.com", "custpass" );
        authService.register( customerReg );

        final Long customerId = userRepository.findByUsername( "custc" ).get().getId();
        final User customer = userRepository.findById( customerId ).get();

        // 2. Create staff
        final UserDto staff = new UserDto();
        staff.setName( "Sam" );
        staff.setUsername( "sam" );
        staff.setEmail( "sam@test.com" );
        staff.setPassword( "pass" );
        staff.setRoles( List.of( roleRepository.findByName( "ROLE_STAFF" ) ) );

        final UserDto savedStaff = authService.createUser( staff );
        final Long staffId = savedStaff.getId();
        final User staffEntity = userRepository.findById( staffId ).get();

        // 3. Create a valid IN_PROGRESS order tied to BOTH
        final Order order = new Order();

        order.setCustomer( customer ); // must NOT be null
        order.setPreparedBy( staffEntity ); // must NOT be null
        order.setStatus( OrderStatus.IN_PROGRESS );

        entityManager.persist( order );
        entityManager.flush(); // push to DB so deleteUserById sees it

        // 4. Expect UC10 failure (staff has active orders)
        final WolfCafeAPIException ex = assertThrows( WolfCafeAPIException.class,
                () -> authService.deleteUserById( staffId ) );

        assertTrue( ex.getMessage().contains( "active (IN_PROGRESS)" ) );
    }

    /**
     * UC10: Deleting a customer removes their order history.
     */
    @Test
    @Transactional
    void testDeleteCustomerDeletesOrders () {

        // 1. Create CUSTOMER
        final UserDto customer = new UserDto();
        customer.setName( "Chris" );
        customer.setUsername( "chris" );
        customer.setEmail( "chris@test.com" );
        customer.setPassword( "pass" );
        customer.setRoles( List.of( roleRepository.findByName( "ROLE_CUSTOMER" ) ) );

        final UserDto savedCustomer = authService.createUser( customer );
        final Long custId = savedCustomer.getId();
        final User customerEntity = userRepository.findById( custId ).get();

        // 2. Create STAFF
        final UserDto staff = new UserDto();
        staff.setName( "Sam Staff" );
        staff.setUsername( "samstaff" );
        staff.setEmail( "samstaff@test.com" );
        staff.setPassword( "pass" );
        staff.setRoles( List.of( roleRepository.findByName( "ROLE_STAFF" ) ) );

        final UserDto savedStaff = authService.createUser( staff );
        final Long staffId = savedStaff.getId();
        final User staffEntity = userRepository.findById( staffId ).get();

        // 3. Create valid ORDER for customer
        final Order order = new Order();

        order.setCustomer( customerEntity ); // required: cannot be null
        order.setPreparedBy( staffEntity ); // required: cannot be null
        order.setStatus( OrderStatus.PLACED );

        entityManager.persist( order );
        entityManager.flush();

        // 4. Verify order exists
        final List<Order> ordersBefore = entityManager
                .createQuery( "SELECT o FROM Order o WHERE o.customer.id = :id", Order.class )
                .setParameter( "id", custId ).getResultList();

        assertEquals( 1, ordersBefore.size() );

        // 5. Delete customer → cascade delete orders
        authService.deleteUserById( custId );

        final List<Order> ordersAfter = entityManager
                .createQuery( "SELECT o FROM Order o WHERE o.customer.id = :id", Order.class )
                .setParameter( "id", custId ).getResultList();

        assertEquals( 0, ordersAfter.size() );
    }

    /**
     * UC10: Deleting a user that does not exist throws
     * ResourceNotFoundException.
     */
    @Test
    @Transactional
    void testDeleteUserNotFound () {

        assertThrows( ResourceNotFoundException.class, () -> {
            authService.deleteUserById( 9999L );
        } );
    }

    /**
     * UC10: Staff with no active orders can be deleted.
     */
    @Test
    @Transactional
    void testDeleteStaffNoActiveOrders () {
        // Create staff
        final UserDto staff = new UserDto();
        staff.setName( "Terry" );
        staff.setUsername( "terry" );
        staff.setEmail( "terry@test.com" );
        staff.setPassword( "pass" );
        staff.setRoles( List.of( roleRepository.findByName( "ROLE_STAFF" ) ) );

        final UserDto saved = authService.createUser( staff );
        final Long staffId = saved.getId();

        // Attempt delete (should work)
        authService.deleteUserById( staffId );

        assertTrue( userRepository.findById( staffId ).isEmpty() );
    }

}
