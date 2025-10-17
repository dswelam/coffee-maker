package edu.ncsu.csc326.wolfcafe.dto;

/**
 * Used to transfer Ingredient data between the client and server. This class
 * will serve as the response in the REST API.
 *
 * @author Nora Cam (nncam)
 */
public class IngredientDto {

    /** Ingredient Id */
    private Long   id;

    /** Ingredient name */
    private String name;

    /**
     * Default constructor for the IngredientDto
     */
    public IngredientDto () {

    }

    /**
     * IngredientDto with both an ID and name
     *
     * @param id
     *            id of the ingredient
     * @param name
     *            name of the ingredient
     */
    public IngredientDto ( final Long id, final String name ) {
        super();
        this.id = id;
        this.name = name;
    }

    /**
     * IngredientDto with just the name, no ID
     *
     * @param name
     *            name of the ingredient
     */
    public IngredientDto ( final String name ) {
        super();
        this.name = name;
    }

    /**
     * Gets the ID of the ingredient
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID of the ingredient
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
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
