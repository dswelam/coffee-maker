package edu.ncsu.csc326.wolfcafe.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.TaxDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.entity.Permission;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.service.AuthService;
import lombok.AllArgsConstructor;

/**
 * Controller for authentication functionality.
 *
 * @author Dania Swelam
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/auth" )
@AllArgsConstructor
public class AuthController {

    /** Link to AuthService */
    @Autowired
    private final AuthService authService;

    /**
     * Registers a new customer user with the system.
     *
     * @param registerDto
     *            object with registration info
     * @return response indicating success or failure
     */
    @PostMapping ( "/register" )
    public ResponseEntity<String> register ( @RequestBody final RegisterDto registerDto ) {
        final String response = authService.register( registerDto );
        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    /**
     * Registers a new user of any role with the system.
     *
     * @param userDto          object with user info
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @PostMapping ( "/users" )
    public ResponseEntity<UserDto> createUser ( @RequestBody final UserDto userDto ) {
        final UserDto savedUserDto = authService.createUser( userDto );
        return new ResponseEntity<>( savedUserDto, HttpStatus.CREATED );
    }

    /**
     * Updates an existing user of any role with the system.
     *
     * @param id       id of user to update
     * @param userDto object with updated user info
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @PutMapping ( "/users/{id}" )
    public ResponseEntity<UserDto> updateUser ( @PathVariable ( "id" ) final Long id,
            @RequestBody final UserDto userDto ) {
        final UserDto updatedUserDto = authService.updateUser( id, userDto );
        return new ResponseEntity<>( updatedUserDto, HttpStatus.OK );
    }

    /**
     * Logs in the given user
     *
     * @param loginDto
     *            user information for login
     * @return object representing the logged in user
     */
    @PostMapping ( "/login" )
    public ResponseEntity<JwtAuthResponse> login ( @RequestBody final LoginDto loginDto ) {
        final JwtAuthResponse jwtAuthResponse = authService.login( loginDto );
        return new ResponseEntity<>( jwtAuthResponse, HttpStatus.OK );
    }

    /**
     * Deletes the given user. Requires the ADMIN role.
     *
     * @param id
     *            id of user to delete
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @DeleteMapping ( "/user/{id}" )
    public ResponseEntity<String> deleteUser ( @PathVariable ( "id" ) final Long id ) {
        authService.deleteUserById( id );
        return ResponseEntity.ok( "User deleted successfully." );
    }

    /**
     * Updates the permissions of the given role
     *
     * @param roleName
     *            the name of the role
     * @param permissions
     *            the permissions to update
     * @return the reponse indicating success or failure
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @PutMapping ( "/roles/{roleName}/permissions" )
    public ResponseEntity<Role> assignPermissions ( @PathVariable ( "roleName" ) final String roleName,
            @RequestBody final Collection<Permission> permissions ) {
        try {
            final Role updated = authService.assignPermissions( roleName, permissions );
            return ResponseEntity.ok( updated );
        }
        catch ( final IllegalArgumentException e ) {
            return ResponseEntity.badRequest().build();
        }
        catch ( final ResourceNotFoundException e ) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).build();
        }
    }

    /**
     * Returns the current tax rate set in the system
     *
     * @return current tax rate as an integer
     */
    @GetMapping ( "/tax" )
    public ResponseEntity<Double> getTaxRate () {
        return ResponseEntity.ok( authService.getTaxRate() );
    }

    /**
     * Sets the current tax rate in the system, requires the ADMIN role
     *
     * @param taxDto
     *            the tax rate containing the current amount to set
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @PutMapping ( "/tax" )
    public void setTaxRate ( @RequestBody final TaxDto taxDto ) {
        authService.setTaxRate( taxDto );
    }

    /**
     * Gets the list of users
     *
     * @return the list of users
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @GetMapping ( "/users" )
    public ResponseEntity<List<UserDto>> getAllUsers () {
        final List<UserDto> users = authService.listUsers();
        return ResponseEntity.ok( users );
    }

    /**
     * Deletes multiple users at once. Calls deleteUserById for each user.
     *
     * @param ids
     *            list of user IDs to delete
     * @return success message or error from underlying deleteUserById logic
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @PostMapping ( "/users/delete" )
    public ResponseEntity<String> deleteMultipleUsers ( @RequestBody final List<Long> ids ) {

        try {
            for ( final Long id : ids ) {
                authService.deleteUserById( id );
            }
            return ResponseEntity.ok( "Selected users deleted successfully." );
        }
        catch ( final ResourceNotFoundException e ) {
            // UC10: user already deleted by another admin
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( e.getMessage() );
        }
        catch ( final WolfCafeAPIException e ) {
            // UC10: cannot delete self or cannot delete staff w/ active orders
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( e.getMessage() );
        }
    }

}
