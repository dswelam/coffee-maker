package edu.ncsu.csc326.wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import edu.ncsu.csc326.wolfcafe.entity.Permission;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.service.AuthService;

/**
 * Tests the authorization controller.
 *
 * @author Diya Patel
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
                .content( json ) ).andExpect( status().isBadRequest() )
                .andExpect( content().string( Matchers.containsString( "Invalid Permission" ) ) );
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
                .content( json ) ).andExpect( status().isNotFound() )
                .andExpect( content().string( Matchers.containsString( "Role not found" ) ) );
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
     * @throws Exception 
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testSetTaxRate() throws Exception {
    		MvcResult result = mvc.perform( get( "/api/auth/tax" ) ).andExpect( status().isOk() ).andReturn();
    		assertEquals(Double.toString(authService.getTaxRate()), result.getResponse().getContentAsString());
    		
        final TaxDto updatedTax = new TaxDto( 5 );
        mvc.perform( put( "/api/auth/tax" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( updatedTax ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );
    }

}
