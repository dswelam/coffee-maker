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

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Permission;
import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.exception.WolfCafeAPIException;
import edu.ncsu.csc326.wolfcafe.mapper.IngredientMapper;
import edu.ncsu.csc326.wolfcafe.mapper.UserMapper;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.security.JwtTokenProvider;
import edu.ncsu.csc326.wolfcafe.service.AuthService;
import lombok.AllArgsConstructor;

/**
 * Implemented AuthService
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

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
    
    private UserDto getUserByEmail ( String userEmail ) {
        User user = userRepository.findByUsernameOrEmail( null, userEmail ).orElseThrow(
                () -> new ResourceNotFoundException( "User does not exist with email " + userEmail ) );
        return UserMapper.mapToUserDto( user );
    }
    
    private boolean isDuplicateEmail ( String userEmail ) {
        try {
            getUserByEmail( userEmail );
            return true;
        }
        catch ( ResourceNotFoundException e ) {
            return false;
        }
    }
    
    /**
     * Creates the given user (can be of any role), can be used by admin only
     * @param userDto new user information
     * @return response with created user
     */
	@Override
	public UserDto createUser(UserDto userDto) {
        // Validate the user
		// The name of the user must be a non-empty string
		if ( userDto.getName().length() == 0 ) {
            throw new IllegalArgumentException("User's name must be a non-empty string");
        }
		// The email address of the user must contain the "@" symbol
		if (!userDto.getEmail().contains("@")) {
			throw new IllegalArgumentException("User's email address is not in a valid format");
		}
		// The password of the user must be a non-empty string
		if (userDto.getPassword().length() == 0) {
			throw new IllegalArgumentException("User's password must be a non-empty string");
		}
		// The email of the user cannot be a duplicate of an already existing user in the system
		if (isDuplicateEmail(userDto.getEmail())) {
			throw new IllegalArgumentException("User's email address is already used by an existing user");
		}
		
		User user = UserMapper.mapToUser( userDto );
        User savedUser = userRepository.save( user );
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
        userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "User not found with id " + id ) );
        userRepository.deleteById( id );
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

    @Override
    public List<UserDto> listUsers () {
        // Retrieve all users from the database
        final List<User> users = userRepository.findAll();

        // Convert each User entity into a UserDto, and directly maps roles to
        // collection
        return users.stream()
                .map( user -> new UserDto( user.getId(), user.getName(), user.getUsername(), user.getEmail(), user.getRoles() ) )
                .collect( Collectors.toList() );
    }

}
