package edu.ncsu.csc326.wolfcafe.entity;

/**
 * System permissions that can be assigned to roles.
 *
 * @author Diya Patel
 */
public enum Permission {

    /** Permission to add inventory */
    ADD_INVENTORY,

    /** Permission to  fulfill orders */
    FULFILL_ORDER,

    /** Permission to purchase items */
    PURCHASE_ITEM,

    /** Permission to manage users */
    MANAGE_USERS
}
