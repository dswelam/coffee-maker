package edu.ncsu.csc326.wolfcafe.mapper;

import edu.ncsu.csc326.wolfcafe.dto.UserDto;
import edu.ncsu.csc326.wolfcafe.entity.User;

/**
 * Converts between User entity and UserDto
 *
 * @author Brooke Wu (bwu25)
 */
public class UserMapper {

    /**
     * Converts a User entity to UserDto
     *
     * @param user
     *            User to convert
     * @return UserDto object
     */
    public static UserDto mapToUserDto ( final User user ) {
        return new UserDto( user.getId(), user.getName(), user.getUsername(), user.getEmail(), user.getPassword(), user.getRoles() );

    }

    /**
     * Converts a UserDto object to a User entity.
     *
     * @param userDto
     *            UserDto to convert
     * @return User entity
     */
    public static User mapToUser ( final UserDto userDto ) {
        return new User( userDto.getId(), userDto.getName(), userDto.getUsername(), userDto.getEmail(), userDto.getPassword(), userDto.getRoles() );
    }

}
