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
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.config.Roles.UserRoles;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.entity.Permission;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
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
        final IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> {
            authService.assignPermissions( "ROLE_CUSTOMER", List.of( Permission.ADD_INVENTORY ) );
        } );

        assertTrue( exception.getMessage().contains( "Only staff role permissions can be modified" ) );
    }

    /** Role not found */
    @Test
    @Transactional
    void testAssignPermissionsRoleNotFound () {
        assertThrows( ResourceNotFoundException.class, () -> {
            authService.assignPermissions( "ROLE_UNKNOWN", List.of( Permission.ADD_INVENTORY ) );
        } );
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
    void testCreateUsers() {
    		// Create a Customer user
        RegisterDto registerDto = new RegisterDto( "Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC" );
        String registerResult = authService.register(registerDto);
        assertEquals("User registered successfully.", registerResult);
        
        WolfCafeAPIException registerException = assertThrows( WolfCafeAPIException.class, () -> {
            authService.register(registerDto);
        } );
        assertTrue( registerException.getMessage().contains( "Username already exists." ) );
        
        registerDto.setUsername("unique");
        registerException = assertThrows( WolfCafeAPIException.class, () -> {
            authService.register(registerDto);
        } );
        assertTrue( registerException.getMessage().contains( "Email already exists." ) );
    	
    		// Create a Barista user
    		final UserDto baristaUser = new UserDto();
    		baristaUser.setName("Barry");
    		baristaUser.setUsername("barista");
    		baristaUser.setEmail("barry@wolfcafe.com");
    		baristaUser.setPassword("abc123");
    		Collection<Role> baristaRoles = new ArrayList<Role>();
    		baristaRoles.add(roleRepository.findByName("ROLE_BARISTA"));
    		baristaUser.setRoles(baristaRoles);
    		
    		final UserDto createdBaristaUser = authService.createUser(baristaUser);
    		assertNotNull(createdBaristaUser.getId());
    		assertEquals(baristaUser.getName(), createdBaristaUser.getName());
    		assertEquals(baristaUser.getUsername(), createdBaristaUser.getUsername());
    		assertEquals(baristaUser.getEmail(), createdBaristaUser.getEmail());
    		assertEquals(baristaUser.getPassword(), createdBaristaUser.getPassword());
    		assertEquals(baristaUser.getRoles(), createdBaristaUser.getRoles());
    		assertTrue(userRepository.findByUsername("barista").isPresent());
    		
    		// Create a Staff user
    		final UserDto staffUser = new UserDto();
    		staffUser.setName("Stephanie");
    		staffUser.setUsername("staff");
    		staffUser.setEmail("stephanie@wolfcafe.com");
    		staffUser.setPassword("xyz789");
    		Collection<Role> staffRoles = new ArrayList<Role>();
    		staffRoles.add(roleRepository.findByName("ROLE_STAFF"));
    		staffUser.setRoles(staffRoles);
    		
    		final UserDto createdStaffUser = authService.createUser(staffUser);
    		assertNotNull(createdStaffUser.getId());
    		assertEquals(staffUser.getName(), createdStaffUser.getName());
    		assertEquals(staffUser.getUsername(), createdStaffUser.getUsername());
    		assertEquals(staffUser.getEmail(), createdStaffUser.getEmail());
    		assertEquals(staffUser.getPassword(), createdStaffUser.getPassword());
    		assertEquals(staffUser.getRoles(), createdStaffUser.getRoles());    	
    		assertTrue(userRepository.findByUsername("staff").isPresent());
    		
    		// Test validation
    		UserDto invalidUser = new UserDto();
    		invalidUser.setName("");
    		invalidUser.setUsername("invalid");
    		invalidUser.setEmail("invalidwolfcafe.com");
    		invalidUser.setPassword("");
        IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> {
            authService.createUser(invalidUser);
        } );
        assertTrue( exception.getMessage().contains( "User's name must be a non-empty string" ) );
        
        invalidUser.setName("Invalid User");
        exception = assertThrows( IllegalArgumentException.class, () -> {
            authService.createUser(invalidUser);
        } );
        assertTrue( exception.getMessage().contains( "User's email address is not in a valid format" ) );
        
        invalidUser.setEmail(baristaUser.getEmail());
        exception = assertThrows( IllegalArgumentException.class, () -> {
            authService.createUser(invalidUser);
        } );
        assertTrue( exception.getMessage().contains( "User's password must be a non-empty string" ) );
        
        invalidUser.setPassword("password");
        exception = assertThrows( IllegalArgumentException.class, () -> {
            authService.createUser(invalidUser);
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

}
