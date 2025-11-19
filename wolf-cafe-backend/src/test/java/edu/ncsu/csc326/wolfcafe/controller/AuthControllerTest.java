package edu.ncsu.csc326.wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.TaxDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.entity.Permission;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.service.AuthService;

/**
 * Tests the authorization controller.
 *
 * @author Diya Patel
 * @author Brooke Wu
 * @author Dania Swelam
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    /** Admin password from application.properties */
    @Value ( "${app.admin-user-password}" )
    private String                    adminUserPassword;

    /** Mocked MVC for testing */
    @Autowired
    private MockMvc                   mvc;

    /** Mocked AuthService for UC7 */
    @MockitoBean
    @Autowired
    private AuthService               authService;

    /** JSON mapper */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** the role repository instance */
    @Autowired
    private RoleRepository            roleRepository;

    /** the user repository instance */
    @Autowired
    private UserRepository            userRepository;

    /**
     * adding setup for the test cases
     */
    @BeforeEach
    public void seedRoles () {
        for ( final String name : List.of( "ROLE_CUSTOMER", "ROLE_ADMIN", "ROLE_STAFF" ) ) {
            if ( roleRepository.findByName( name ) == null ) {
                final Role r = new Role();
                r.setName( name );
                roleRepository.save( r );
            }
        }
    }

    /**
     * Tests logging in as an admin user.
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    public void testLoginAdmin () throws Exception {
        final JwtAuthResponse mockResponse = new JwtAuthResponse( "fake-token", "Bearer", "ROLE_ADMIN" );

        Mockito.when( authService.login( ArgumentMatchers.any() ) ).thenReturn( mockResponse );

        final LoginDto loginDto = new LoginDto( "admin", adminUserPassword );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_ADMIN" ) );
    }

    /**
     * Tests creating a customer user and logging in.
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    public void testCreateCustomerAndLogin () throws Exception {
        final RegisterDto registerDto = new RegisterDto( "Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC" );

        // tell mock what to return when register() is called
        Mockito.when( authService.register( ArgumentMatchers.any() ) ).thenReturn( "User registered successfully." );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ) ).andExpect( status().isCreated() )
                .andExpect( content().string( "User registered successfully." ) );

        final LoginDto loginDto = new LoginDto( "jestes", "JXB16TBD4LC" );

        final JwtAuthResponse mockResponse = new JwtAuthResponse( "fake-token", "Bearer", "ROLE_CUSTOMER" );
        Mockito.when( authService.login( ArgumentMatchers.any() ) ).thenReturn( mockResponse );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_CUSTOMER" ) );
    }

    /**
     * Tests creating a staff and barista user (as an admin user) and logging in as the created users.
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testCreateStaffBaristaAndLogin () throws Exception {
        // Create a barista user
        final UserDto baristaUser = new UserDto();
        baristaUser.setName( "Barry" );
        baristaUser.setUsername( "barista" );
        baristaUser.setEmail( "barry@wolfcafe.com" );
        baristaUser.setPassword( "abc123" );
        final Collection<Role> baristaRoles = new ArrayList<Role>();
        baristaRoles.add( roleRepository.findByName( "ROLE_BARISTA" ) );
        baristaUser.setRoles( baristaRoles );

        Mockito.when( authService.createUser( ArgumentMatchers.any() ) ).thenReturn( baristaUser );

        mvc.perform( post( "/api/auth/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( baristaUser ) ) ).andExpect( status().isCreated() );

        // Log in as the barista user
        LoginDto loginDto = new LoginDto( baristaUser.getUsername(), baristaUser.getPassword() );

        JwtAuthResponse mockResponse = new JwtAuthResponse( "fake-token", "Bearer", "ROLE_BARISTA" );
        Mockito.when( authService.login( ArgumentMatchers.any() ) ).thenReturn( mockResponse );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_BARISTA" ) );

        // Create a staff user
        final UserDto staffUser = new UserDto();
        staffUser.setName( "Stephanie" );
        staffUser.setUsername( "staff" );
        staffUser.setEmail( "stephanie@wolfcafe.com" );
        staffUser.setPassword( "xyz789" );
        final Collection<Role> staffRoles = new ArrayList<Role>();
        staffRoles.add( roleRepository.findByName( "ROLE_STAFF" ) );
        staffUser.setRoles( staffRoles );

        Mockito.when( authService.createUser( ArgumentMatchers.any() ) ).thenReturn( staffUser );

        mvc.perform( post( "/api/auth/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( baristaUser ) ) ).andExpect( status().isCreated() );

        // Log in as the staff user
        loginDto = new LoginDto( staffUser.getUsername(), staffUser.getPassword() );

        mockResponse = new JwtAuthResponse( "fake-token", "Bearer", "ROLE_STAFF" );
        Mockito.when( authService.login( ArgumentMatchers.any() ) ).thenReturn( mockResponse );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_STAFF" ) );
    }

    /**
     * Tests attempting to access the createUser() API endpoint when logged in
     * as a user who doesn't have the admin role
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = "STAFF" )
    public void testCreateUserAsNonAdminRole () throws Exception {
        // Create a barista user
        final UserDto baristaUser = new UserDto();
        baristaUser.setName( "Barry" );
        baristaUser.setUsername( "barista" );
        baristaUser.setEmail( "barry@wolfcafe.com" );
        baristaUser.setPassword( "abc123" );
        final Collection<Role> baristaRoles = new ArrayList<Role>();
        baristaRoles.add( roleRepository.findByName( "ROLE_BARISTA" ) );
        baristaUser.setRoles( baristaRoles );

        Mockito.when( authService.createUser( ArgumentMatchers.any() ) ).thenReturn( baristaUser );

        mvc.perform( post( "/api/auth/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( baristaUser ) ) ).andExpect( status().isForbidden() );

        assertTrue( userRepository.findByUsername( baristaUser.getUsername() ).isEmpty() );
    }

    /**
     * Successful permission assignment by ADMIN.
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testAssignPermissionsSuccess () throws Exception {
        final Role role = new Role();
        role.setId( 2L );
        role.setName( "ROLE_STAFF" );
        role.setPermissions( Set.of( Permission.ADD_INVENTORY, Permission.FULFILL_ORDER ) );

        Mockito.when(
                authService.assignPermissions( ArgumentMatchers.eq( "ROLE_STAFF" ), ArgumentMatchers.anyCollection() ) )
                .thenReturn( role );

        final String json = MAPPER.writeValueAsString( List.of( "ADD_INVENTORY", "FULFILL_ORDER" ) );

        mvc.perform( put( "/api/auth/roles/ROLE_STAFF/permissions" ).contentType( MediaType.APPLICATION_JSON )
                .content( json ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.name", Matchers.equalTo( "ROLE_STAFF" ) ) )
                .andExpect( jsonPath( "$.permissions", Matchers.hasSize( 2 ) ) ).andExpect(
                        jsonPath( "$.permissions", Matchers.containsInAnyOrder( "ADD_INVENTORY", "FULFILL_ORDER" ) ) );
    }

    /**
     * Invalid permission assignment (Customer cannot fulfill orders).
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testAssignPermissionsInvalid () throws Exception {
        Mockito.when( authService.assignPermissions( ArgumentMatchers.eq( "ROLE_CUSTOMER" ),
                ArgumentMatchers.anyCollection() ) )
                .thenThrow( new IllegalArgumentException( "Invalid Permission: Customers cannot fulfill orders." ) );

        final String json = MAPPER.writeValueAsString( List.of( "FULFILL_ORDER" ) );

        mvc.perform( put( "/api/auth/roles/ROLE_CUSTOMER/permissions" ).contentType( MediaType.APPLICATION_JSON )
                .content( json ) ).andExpect( status().isBadRequest() );
    }

    /**
     * Role not found.
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testAssignPermissionsRoleNotFound () throws Exception {
        Mockito.when( authService.assignPermissions( ArgumentMatchers.eq( "ROLE_UNKNOWN" ),
                ArgumentMatchers.anyCollection() ) )
                .thenThrow( new ResourceNotFoundException( "Role not found with name ROLE_UNKNOWN" ) );

        final String json = MAPPER.writeValueAsString( List.of( "ADD_INVENTORY" ) );

        mvc.perform( put( "/api/auth/roles/ROLE_UNKNOWN/permissions" ).contentType( MediaType.APPLICATION_JSON )
                .content( json ) ).andExpect( status().isNotFound() );
    }

    /**
     * Non-admin user attempting to assign permissions.
     */
    @Test
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testAssignPermissionsUnauthorized () throws Exception {
        final String json = MAPPER.writeValueAsString( List.of( "ADD_INVENTORY" ) );

        mvc.perform( put( "/api/auth/roles/ROLE_STAFF/permissions" ).contentType( MediaType.APPLICATION_JSON )
                .content( json ) ).andExpect( status().isForbidden() );
    }

    /**
     * Tests setting the tax rate
     *
     * @throws Exception if error
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testSetTaxRate () throws Exception {
        final MvcResult result = mvc.perform( get( "/api/auth/tax" ) ).andExpect( status().isOk() ).andReturn();
        assertEquals( Double.toString( authService.getTaxRate() ), result.getResponse().getContentAsString() );

        final TaxDto updatedTax = new TaxDto( 5 );
        mvc.perform( put( "/api/auth/tax" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( updatedTax ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );
    }

    /**
     * Tests retrieving all users, admin only.
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testGetAllUsers () throws Exception {
        final List<UserDto> mockUsers = List.of( new UserDto( 1L, "admin", "admin", "admin@ncsu.edu", List.of() ),
                new UserDto( 2L, "staff", "staff", "staff@ncsu.edu", List.of() ) );

        Mockito.when( authService.listUsers() ).thenReturn( mockUsers );

        mvc.perform( get( "/api/auth/users" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$[0].username" ).value( "admin" ) )
                .andExpect( jsonPath( "$[1].username" ).value( "staff" ) );
    }

    /**
     * Tests the updateUser() API endpoint
     */
    @Test
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testUpdateUser () throws Exception {
        // Arrange
        final Long userId = 1L;
        final UserDto userDto = new UserDto();
        userDto.setName( "Updated Name" );
        userDto.setUsername( "updateduser" );
        userDto.setEmail( "updated@email.com" );
        userDto.setPassword( "newpassword" );
        userDto.setRoles( new ArrayList<>( roleRepository.findAll() ) );

        Mockito.when( authService.updateUser( Mockito.eq( userId ), Mockito.any( UserDto.class ) ) )
                .thenReturn( userDto );

        // Act & Assert
        mvc.perform( put( "/api/auth/users/{id}", userId ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( userDto ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.name" ).value( "Updated Name" ) )
                .andExpect( jsonPath( "$.username" ).value( "updateduser" ) )
                .andExpect( jsonPath( "$.email" ).value( "updated@email.com" ) );
    }

    /**
     * Tests successfully deleting multiple users in UC10.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testBulkDeleteUsersSuccess () throws Exception {

        Mockito.doNothing().when( authService ).deleteUserById( 1L );
        Mockito.doNothing().when( authService ).deleteUserById( 2L );

        final List<Long> ids = List.of( 1L, 2L );

        mvc.perform( post( "/api/auth/users/delete" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ids ) ) ).andExpect( status().isOk() )
                .andExpect( content().string( "Selected users deleted successfully." ) );
    }

    /**
     * Tests that non-admin users cannot bulk delete users.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testBulkDeleteUsersForbidden () throws Exception {

        final List<Long> ids = List.of( 1L, 2L );

        mvc.perform( post( "/api/auth/users/delete" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ids ) ) ).andExpect( status().isForbidden() );
    }

    /**
     * Tests bulk delete where deleteUserById throws a self-delete error.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testBulkDeleteCannotDeleteSelf () throws Exception {

        final List<Long> ids = List.of( 99L ); // arbitrary id

        Mockito.doThrow( new edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException( HttpStatus.BAD_REQUEST,
                "You cannot delete your own account." ) ).when( authService ).deleteUserById( 99L );

        mvc.perform( post( "/api/auth/users/delete" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ids ) ) ).andExpect( status().isBadRequest() )
                .andExpect( content().string( Matchers.containsString( "cannot delete your own" ) ) );
    }

    /**
     * Tests bulk delete where staff user has active orders and cannot be
     * deleted.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testBulkDeleteStaffInUse () throws Exception {

        final List<Long> ids = List.of( 10L );

        Mockito.doThrow( new edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException( HttpStatus.BAD_REQUEST,
                "Cannot delete staff while they have an active (IN_PROGRESS) order assigned." ) ).when( authService )
                .deleteUserById( 10L );

        mvc.perform( post( "/api/auth/users/delete" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ids ) ) ).andExpect( status().isBadRequest() )
                .andExpect( content().string( Matchers.containsString( "active" ) ) );
    }

    /**
     * Tests bulk delete where one of the users no longer exists
     *
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = "ADMIN" )
    public void testBulkDeleteUserNotFound () throws Exception {

        final List<Long> ids = List.of( 123L );

        Mockito.doThrow( new ResourceNotFoundException( "User not found with id 123" ) ).when( authService )
                .deleteUserById( 123L );

        mvc.perform( post( "/api/auth/users/delete" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ids ) ) ).andExpect( status().isNotFound() )
                .andExpect( content().string( Matchers.containsString( "User not found" ) ) );
    }

}
