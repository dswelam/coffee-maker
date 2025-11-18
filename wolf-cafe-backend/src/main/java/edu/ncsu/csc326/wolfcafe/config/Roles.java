package edu.ncsu.csc326.wolfcafe.config;

/**
 * Defines user roles for WolfCafe
 */
public class Roles {
	
	/** Admin role name */
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	
	/**
	 * Defines all roles in the system, EXCEPT for the Admin role.
	 */
	public enum UserRoles {
		
		/** Staff for WolfCafe - manages the Inventory */
		ROLE_STAFF,
		/** Barista for WolfCafe - manages the Orders */
		ROLE_BARISTA,
		/** Anonymous Customer for WolfCafe */
		ROLE_ANONYMOUS,
		/** Customer for WolfCafe */
		ROLE_CUSTOMER

	}
	
}
