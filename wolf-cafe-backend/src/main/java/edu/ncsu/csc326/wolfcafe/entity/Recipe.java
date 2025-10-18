package edu.ncsu.csc326.wolfcafe.entity;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

/**
 * Recipe for the coffee maker. Recipe is a Data Access Object (DAO) is tied to
 * the database using Hibernate libraries. RecipeRepository provides the methods
 * for database CRUD operations.
 */
@Entity
@Table ( name = "recipes" )
public class Recipe {

    /** Recipe id */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long                 id;

    /** Recipe name */
    private String               name;

    /** Recipe price */
    private Integer              price;

    /** List of ingredients and their initial amounts */
    @ElementCollection
    @CollectionTable ( name = "recipe_ingredients", joinColumns = @JoinColumn ( name = "recipe_id" ) )
    @MapKeyColumn ( name = "ingredient_name" )
    @Column ( name = "ingredient_amount" )
    private Map<String, Integer> ingredients = new HashMap<>();

    /**
     * Creates a default recipe for the coffee maker.
     */
    public Recipe () {
        this.name = "";
    }

    /**
     * Creates a recipe from all the fields
     *
     * @param id
     *            recipe id
     * @param name
     *            recipe name
     * @param price
     *            recipe price
     * @param ingredients
     *            map of ingredients and their amounts
     */
    public Recipe ( final Long id, final String name, final Integer price, final Map<String, Integer> ingredients ) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
    }

    /**
     * Creates a recipe from all the fields
     *
     * @param name
     *            recipe name
     * @param price
     *            recipe price
     * @param ingredients
     *            map of ingredients and their amounts
     */
    public Recipe ( final String name, final Integer price, final Map<String, Integer> ingredients ) {
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
    }

    /**
     * Get the ID of the Recipe
     *
     * @return the ID
     */
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Recipe (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns name of the recipe.
     *
     * @return Returns the name.
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the recipe name.
     *
     * @param name
     *            The name to set.
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Returns the price of the recipe.
     *
     * @return Returns the price.
     */
    public Integer getPrice () {
        return price;
    }

    /**
     * Sets the recipe price.
     *
     * @param price
     *            The price to set.
     */
    public void setPrice ( final Integer price ) {
        this.price = price;
    }

    /**
     * Returns the ingredients of the recipe.
     *
     * @return Returns the ingredients.
     */
    public Map<String, Integer> getIngredients () {
        return ingredients;
    }

    /**
     * Sets the recipe ingredients.
     *
     * @param ingredients
     *            The ingredients to set.
     */
    public void setIngredients ( final Map<String, Integer> ingredients ) {
        this.ingredients = ingredients;
    }
}
