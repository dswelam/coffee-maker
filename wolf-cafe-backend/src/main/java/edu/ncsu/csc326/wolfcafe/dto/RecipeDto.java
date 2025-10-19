package edu.ncsu.csc326.wolfcafe.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to transfer Recipe data between the client and server. This class will
 * serve as the response in the REST API.
 */
public class RecipeDto {

    /** Recipe Id */
    private Long                 id;

    /** Recipe name */
    private String               name;

    /** Recipe price */
    private Integer              price;

    /** List of ingredients and their amounts */
    private Map<String, Integer> ingredients = new HashMap<>();

    /**
     * Default constructor for Recipe.
     */
    public RecipeDto () {

    }

    /**
     * Creates recipe from field values.
     *
     * @param id
     *            recipe's id
     * @param name
     *            recipe's name
     * @param price
     *            recipe's price
     * @param ingredients
     *            map of ingredients and their amounts
     */
    public RecipeDto ( final Long id, final String name, final Integer price, final Map<String, Integer> ingredients ) {
        super();
        this.id = id;
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
    }

    /**
     * Creates recipe from field values.
     *
     * @param name
     *            recipe's name
     * @param price
     *            recipe's price
     * @param ingredients
     *            map of ingredients and their amounts
     */
    public RecipeDto ( final String name, final Integer price, final Map<String, Integer> ingredients ) {
        super();
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
    }

    /**
     * Gets the recipe id.
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * Recipe id to set.
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets recipe's name
     *
     * @return the name
     */
    public String getName () {
        return name;
    }

    /**
     * Recipe name to set.
     *
     * @param name
     *            the name to set
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Gets the recipe's price
     *
     * @return the price
     */
    public Integer getPrice () {
        return price;
    }

    /**
     * Prices value to set.
     *
     * @param price
     *            the price to set
     */
    public void setPrice ( final Integer price ) {
        this.price = price;
    }

    /**
     * Gets the map of ingredients and their amounts
     *
     * @return the ingredients
     */
    public Map<String, Integer> getIngredients () {
        return ingredients;
    }

    /**
     * Sets the map of ingredients and their amounts
     *
     * @param ingredients
     *            the ingredients to set
     */
    public void setIngredients ( final Map<String, Integer> ingredients ) {
        this.ingredients = ingredients;
    }
}
