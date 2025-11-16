package edu.ncsu.csc326.wolfcafe.dto;

import java.util.Collection;

import edu.ncsu.csc326.wolfcafe.entity.Role;

/**
 * Used to transfer User data between the client and server. This class will
 * serve as the response in the REST API.
 *
 * @author Diya Patel
 */
public class UserDto {

    /** the user id */
    private Long             id;
    /** the name of the user */
    private String name;
    /** the username of the user */
    private String           username;
    /** the users email */
    private String           email;
    /** the users password */
    private String           password;
    /** the collection of roles */
    private Collection<Role> roles;

    /**
     * The default constructor for UserDto
     */
    public UserDto () {
    }

    /**
     * The UserDto constructor for the user, including the password
     *
     * @param id
     *            the user id
     * @param name
     *            the name for the user
     * @param username
     *            the username for the user
     * @param email
     *            the email for the user
     * @param password
     *            the password for the user
     * @param roles
     *            the collection of roles
     */
    public UserDto ( final Long id, final String name, final String username, final String email, final String password,
            final Collection<Role> roles ) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    /**
     * constructs UserDto excluding the password
     *
     * @param id
     *            the id of the user
     * @param name
     *            the name for the user
     * @param username
     *            the username of the user
     * @param email
     *            the email for the user
     * @param roles
     *            the roles of the user
     */
    public UserDto ( final Long id, final String name, final String username, final String email, final Collection<Role> roles ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    /**
     * gets the Id of user
     *
     * @return id the users id
     */
    public Long getId () {
        return id;
    }

    /**
     * sets the id of the user
     *
     * @param id
     *            the id of the user
     */
    public void setId ( final Long id ) {
        this.id = id;
    }
    
	/**
     * gets the name of the user
     *
     * @return name of the user
     */
    public String getName() {
		return name;
	}

    /**
     * sets the name for the user
     *
     * @param name
     *            the name of the user
     */
	public void setName(String name) {
		this.name = name;
	}

	/**
     * gets the username of the user
     *
     * @return username of the user
     */
    public String getUsername () {
        return username;
    }

    /**
     * sets the username for the user
     *
     * @param username
     *            the username of the user
     */
    public void setUsername ( final String username ) {
        this.username = username;
    }

    /**
     * gets the email of the user
     *
     * @return email the users email
     */
    public String getEmail () {
        return email;
    }

    /**
     * sets the email of the user
     *
     * @param email
     *            the email of the user
     */
    public void setEmail ( final String email ) {
        this.email = email;
    }

    /**
     * gets the password of the user
     *
     * @return password the password of the user
     */
    public String getPassword () {
        return password;
    }

    /**
     * sets the password of the user
     *
     * @param password
     *            the password of the user
     */
    public void setPassword ( final String password ) {
        this.password = password;
    }

    /**
     * gets the roles of the users
     *
     * @return roles the collection of roles
     */
    public Collection<Role> getRoles () {
        return roles;
    }

    /**
     * sets the roles of the users
     *
     * @param roles
     *            the roles of the users
     */
    public void setRoles ( final Collection<Role> roles ) {
        this.roles = roles;
    }
}
