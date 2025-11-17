package edu.ncsu.csc326.wolfcafe.service;

import java.util.Collection;
import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.TaxDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.entity.Permission;
import edu.ncsu.csc326.wolfcafe.entity.Role;

/**
 * Authorization service
 *
 * @author Dania Swelam
 */
public interface AuthService {
    /**
     * Registers the given customer user
     *
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    String register ( RegisterDto registerDto );

    /**
     * Creates the given user (can be of any role), can be used by admin only
     * @param userDto new user information
     * @return response with created user
     */
    UserDto createUser ( UserDto userDto );

    /**
     * Updates the given user information
     * @param userDto updated user information
     * @return response with updated user
     */
    UserDto updateUser ( Long id, UserDto userDto );

    /**
     * Logins in the given user
     *
     * @param loginDto
     *            username/email and password
     * @return response with authenticated user
     */
    JwtAuthResponse login ( LoginDto loginDto );

    /**
     * Deletes the given user by id
     *
     * @param id
     *            id of user to delete
     */
    void deleteUserById ( Long id );

    /**
     * Returns the current tax rate of the system
     * @return current tax rate as an double (2.00 = 2.00%)
     */
    double getTaxRate ();

    /**
     * Sets the current tax rate of the system
     * @param taxRate the tax rate to set
     */
    void setTaxRate ( TaxDto taxRate );

    /**
     * assigns permissions to the role
     *
     * @param roleName
     *            the name of the role
     * @param permissions
     *            the permissions to update
     * @return the role that was updated
     */
    Role assignPermissions ( String roleName, Collection<Permission> permissions );

    /**
     * Creates the tax rate.
     *
     * @param taxDto
     *            tax rate to create
     * @return updated tax rate after creation
     */
    TaxDto createTax ( TaxDto taxDto );

    /**
     *
     * returns the list of current users
     *
     * @return the list of current users
     */
    List<UserDto> listUsers ();
}
