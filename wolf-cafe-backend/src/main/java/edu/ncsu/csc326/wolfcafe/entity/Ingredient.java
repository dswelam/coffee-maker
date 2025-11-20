package edu.ncsu.csc326.wolfcafe.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Ingredient for the coffee maker. Ingredient is a Data Access Object (DAO) is
 * tied to the database using Hibernate libraries. IngredientRepository provides
 * the methods for database CRUD operations.
 *
 * @author Nora Cam (nncam)
 */
@Entity
@Table ( name = "ingredients" )
@JsonIgnoreProperties ( { "hibernateLazyInitializer", "handler" } )
public class Ingredient {

    /** Ingredient id */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long   id;

    /** Ingredient name */
    private String name;

    /**
     * Default constructor for Hibernate
     */
    public Ingredient () {
        super();
        this.name = "";
    }

    /**
     * Constructor for an ingredient using an ID and a given name
     *
     * @param id
     *            id of the ingredient
     * @param name
     *            name of the ingredient
     */
    public Ingredient ( final Long id, final String name ) {
        super();
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor for an ingredient using a given name
     *
     * @param name
     *            name of the ingredient
     */
    public Ingredient ( final String name ) {
        super();
        this.name = name;
    }

    /**
     * Get the ID of the ingredient
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * Used by Hibernate to set the ID of the ingredient
     *
     * @param id
     *            the id to set
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the name of the ingredient
     *
     * @return the name
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the name of the ingredient
     *
     * @param name
     *            the name to set
     */
    public void setName ( final String name ) {
        this.name = name;
    }

}
