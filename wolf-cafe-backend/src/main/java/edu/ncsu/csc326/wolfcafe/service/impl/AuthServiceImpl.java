package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
import edu.ncsu.csc326.wolfcafe.entity.Tax;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.mapper.TaxMapper;
import edu.ncsu.csc326.wolfcafe.mapper.UserMapper;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.repository.TaxRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.security.JwtTokenProvider;
import edu.ncsu.csc326.wolfcafe.service.AuthService;
import lombok.AllArgsConstructor;

/**
 * Implemented AuthService
 *
 * @author Dania Swelam
 * @author Diya Patel
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    /** Tax repository */
    private final TaxRepository         taxRepository;
    /** User repository */
    private final UserRepository        userRepository;
    /** Role repository */
    private final RoleRepository        roleRepository;
    /** Password encoder object */
    private final PasswordEncoder       passwordEncoder;
    /** Authentication manager */
    private final AuthenticationManager authenticationManager;
    /** JWT Token provider for working with user tokens */
    private final JwtTokenProvider      jwtTokenProvider;
    /** Order repository */
    private final OrderRepository       orderRepository;

    /**
     * Registers the given user
     *
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    @Override
    public String register ( final RegisterDto registerDto ) {
        // Check for duplicates - username
        if ( userRepository.existsByUsername( registerDto.getUsername() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }
        // Check for duplicates - email
        if ( userRepository.existsByEmail( registerDto.getEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        final User user = new User();
        user.setName( registerDto.getName() );
        user.setUsername( registerDto.getUsername() );
        user.setEmail( registerDto.getEmail() );
        user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );

        final Set<Role> roles = new HashSet<>();
        final Role userRole = roleRepository.findByName( "ROLE_CUSTOMER" );
        roles.add( userRole );

        user.setRoles( roles );

        userRepository.save( user );

        return "User registered successfully.";
    }

    /**
     * Creates the given user (can be of any role), can be used by admin only
     *
     * @param userDto
     *            new user information
     * @return response with created user
     */
    @Override
    public UserDto createUser ( final UserDto userDto ) {
        // Validate the user
        // The name of the user must be a non-empty string
        if ( userDto.getName().length() == 0 ) {
            throw new IllegalArgumentException( "User's name must be a non-empty string" );
        }
        // The email address of the user must contain the "@" symbol
        if ( !userDto.getEmail().contains( "@" ) ) {
            throw new IllegalArgumentException( "User's email address is not in a valid format" );
        }
        // The password of the user must be a non-empty string
        if ( userDto.getPassword().length() == 0 ) {
            throw new IllegalArgumentException( "User's password must be a non-empty string" );
        }
        // The email of the user cannot be a duplicate of an already existing
        // user in the system
        if ( userRepository.existsByEmail( userDto.getEmail() ) ) {
            throw new IllegalArgumentException( "User's email address is already used by an existing user" );
        }

        final User user = UserMapper.mapToUser( userDto );
        final User savedUser = userRepository.save( user );
        return UserMapper.mapToUserDto( savedUser );
    }

    /**
     * Logins in the given user
     *
     * @param loginDto
     *            username/email and password
     * @return response with authenticated user
     */
    @Override
    public JwtAuthResponse login ( final LoginDto loginDto ) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( loginDto.getUsernameOrEmail(), loginDto.getPassword() ) );

        SecurityContextHolder.getContext().setAuthentication( authentication );

        final String token = jwtTokenProvider.generateToken( authentication );

        final Optional<User> userOptional = userRepository.findByUsernameOrEmail( loginDto.getUsernameOrEmail(),
                loginDto.getUsernameOrEmail() );

        String role = null;
        if ( userOptional.isPresent() ) {
            final User loggedInUser = userOptional.get();
            final Optional<Role> optionalRole = loggedInUser.getRoles().stream().findFirst();

            if ( optionalRole.isPresent() ) {
                final Role userRole = optionalRole.get();
                role = userRole.getName();
            }
        }

        final JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setRole( role );
        jwtAuthResponse.setAccessToken( token );

        return jwtAuthResponse;
    }

    /**
     * Deletes the given user by id
     *
     * @param id
     *            id of user to delete
     */
    @Override
    public void deleteUserById ( final Long id ) {

        // Check if user exists (Concurrent Delete Case)
        final User user = userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "User not found with id " + id ) );

        // Prevent Admin from deleting themselves
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long loggedInUserId = null;

        if ( auth != null && auth.getName() != null ) {
            final String username = auth.getName();
            loggedInUserId = userRepository.findByUsername( username ).map( User::getId ).orElse( null );
        }

        // Prevent self-delete ONLY when an authenticated user is present
        if ( loggedInUserId != null && loggedInUserId.equals( id ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "You cannot delete your own account." );
        }

        // Determine user role
        final Role role = user.getRoles().stream().findFirst()
                .orElseThrow( () -> new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "User has no role assigned." ) );

        final String roleName = role.getName();

        // Staff cannot be deleted if they have active orders
        if ( "ROLE_STAFF".equals( roleName ) ) {

            final List<Order> inProgressOrders = orderRepository.findAllByStatus( OrderStatus.IN_PROGRESS ).stream()
                    .filter( o -> o.getPreparedBy() != null && o.getPreparedBy().getId().equals( id ) )
                    .collect( Collectors.toList() );

            if ( !inProgressOrders.isEmpty() ) {
                throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST,
                        "Cannot delete staff while they have an active (IN_PROGRESS) order assigned." );
            }
        }

        // delete their entire order history of customer
        if ( "ROLE_CUSTOMER".equals( roleName ) ) {
            final List<Order> customerOrders = orderRepository.findAllByCustomerId( id );
            orderRepository.deleteAll( customerOrders );
        }

        // Finally delete the user
        userRepository.delete( user );
    }

    /**
     * assigns permissions to the staff role
     *
     * @param roleName
     *            the name of the role
     * @param permissions
     *            the permissions to update
     * @return the role that was updated
     */
    @Override
    public Role assignPermissions ( final String roleName, final Collection<Permission> permissions ) {
        final Role role = roleRepository.findByName( roleName );
        if ( role == null ) {
            throw new ResourceNotFoundException( "Role not found with name " + roleName );
        }

        // validate the permission
        if ( "ROLE_CUSTOMER".equalsIgnoreCase( roleName ) ) {
            for ( final Permission p : permissions ) {
                if ( p == Permission.ADD_INVENTORY || p == Permission.FULFILL_ORDER ) {
                    throw new IllegalArgumentException(
                            "Invalid Permission: Customers cannot add tax or fulfill orders." );
                }
            }
        }
        // Only staff permissions can be updated
        if ( !"ROLE_STAFF".equalsIgnoreCase( roleName ) ) {
            throw new IllegalArgumentException( "Only staff role permissions can be modified by admin." );
        }

        // Validate permissions allowed for staff
        for ( final Permission p : permissions ) {
            if ( p == Permission.PURCHASE_ITEM || p == Permission.PURCHASE_RECIPE ) {
                throw new IllegalArgumentException( "Invalid Permission: Staff cannot purchase items or recipes." );
            }
        }

        role.setPermissions( new java.util.HashSet<>( permissions ) );
        return roleRepository.save( role );
    }

    /**
     * Creates the tax rate.
     *
     * @param taxDto
     *            tax rate to create
     * @return updated tax rate after creation
     */
    @Override
    @Transactional
    public TaxDto createTax ( final TaxDto taxDto ) {
        final Tax tax = TaxMapper.mapToTax( taxDto );
        final Tax savedTax = taxRepository.save( tax );
        return TaxMapper.mapToTaxDto( savedTax );
    }

    /**
     * Returns the current tax rate of the system
     *
     * @return current tax rate as an integer (2.00 = 2.00%)
     */
    @Override
    @Transactional
    public double getTaxRate () {
        final List<Tax> tax = taxRepository.findAll();
        if ( tax.size() == 0 ) {
            final TaxDto newTaxDto = new TaxDto();
            newTaxDto.setCurrentAmount( 2 );
            final TaxDto savedTaxDto = createTax( newTaxDto );
            return savedTaxDto.getCurrentAmount();
        }
        return TaxMapper.mapToTaxDto( tax.get( 0 ) ).getCurrentAmount();
    }

    /**
     * Sets the current tax rate of the system
     *
     * @param taxRate
     *            the tax rate to set
     */
    @Override
    @Transactional
    public void setTaxRate ( final TaxDto taxRate ) {
        // Validate that the tax rate is a positive integer
        if ( taxRate.getCurrentAmount() > 0 ) {
            final List<Tax> tax = taxRepository.findAll();
            if ( tax.size() != 0 ) {
                taxRepository.delete( tax.get( 0 ) );
            }

            final TaxDto newTaxDto = new TaxDto();
            newTaxDto.setCurrentAmount( taxRate.getCurrentAmount() );
            createTax( newTaxDto );
        }
    }

    @Override
    public List<UserDto> listUsers () {
        // Retrieve all users from the database
        final List<User> users = userRepository.findAll();

        // Convert each User entity into a UserDto, and directly maps roles to
        // collection
        return users.stream().map( user -> new UserDto( user.getId(), user.getName(), user.getUsername(),
                user.getEmail(), user.getRoles() ) ).collect( Collectors.toList() );
    }

    /**
     * Updates the given user by id
     *
     * @param id
     *            id of user to update
     * @param userDto
     *            updated user information
     * @return response with updated user
     */
    @Override
    public UserDto updateUser ( final Long id, final UserDto userDto ) {
        final User existingUser = userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "User not found with id " + id ) );

        // [Invalid Name] Check: non-empty and letters only
        if ( userDto.getName() == null || userDto.getName().trim().isEmpty()
                || !userDto.getName().matches( "[a-zA-Z ]+" ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST,
                    "Invalid name: must be a non-empty string with letters." );
        }

        // [Invalid Email] Check: non-empty and valid format
        if ( userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()
                || !userDto.getEmail().matches( "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$" ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST,
                    "Invalid email: must be a non-empty string with a valid format." );
        }

        // [Duplicate] Check: email changed and already exists
        if ( !existingUser.getEmail().equals( userDto.getEmail() )
                && userRepository.existsByEmail( userDto.getEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        // [Invalid Password] Check: if password is provided, must be non-empty
        if ( userDto.getPassword() != null && userDto.getPassword().trim().isEmpty() ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Invalid password: must be a non-empty string." );
        }

        // Update fields
        existingUser.setName( userDto.getName() );
        existingUser.setUsername( userDto.getUsername() );
        existingUser.setEmail( userDto.getEmail() );
        existingUser.setRoles( userDto.getRoles() );

        if ( userDto.getPassword() != null && !userDto.getPassword().isEmpty() ) {
            existingUser.setPassword( passwordEncoder.encode( userDto.getPassword() ) );
        }

        final User updatedUser = userRepository.save( existingUser );
        return UserMapper.mapToUserDto( updatedUser );
    }
}

/**
 * GENERATIVE AI WAS USED IN THE CREATION OF THIS FILE:
 *
 * Model: GitHub Copilot GPT-4.1
 * Prompts:
 * - "Generate an implementation of updating a user in Java using Spring Boot."
 * - "Enhance the user update method to include validation checks for name, email, and password."
 */
