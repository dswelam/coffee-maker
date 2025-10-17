package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.RecipeDto;
import edu.ncsu.csc326.wolfcafe.entity.Recipe;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.RecipeMapper;
import edu.ncsu.csc326.wolfcafe.repository.RecipeRepository;
import edu.ncsu.csc326.wolfcafe.service.RecipeService;

/**
 * Implementation of the RecipeService interface.
 */
@Service
public class RecipeServiceImpl implements RecipeService {

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private RecipeRepository recipeRepository;

    /**
     * Creates a recipe with the given information.
     *
     * @param recipeDto
     *            recipe to create
     * @return created recipe
     */
    @Override
    public RecipeDto createRecipe ( final RecipeDto recipeDto ) {
        final Recipe recipe = RecipeMapper.mapToRecipe( recipeDto );
        final Recipe savedRecipe = recipeRepository.save( recipe );
        return RecipeMapper.mapToRecipeDto( savedRecipe );
    }

    /**
     * Returns the recipe with the given id.
     *
     * @param recipeId
     *            recipe's id
     * @return the recipe with the given id
     * @throws ResourceNotFoundException
     *             if the recipe doesn't exist
     */
    @Override
    public RecipeDto getRecipeById ( final Long recipeId ) {
        final Recipe recipe = recipeRepository.findById( recipeId )
                .orElseThrow( () -> new ResourceNotFoundException( "Recipe does not exist with id " + recipeId ) );
        return RecipeMapper.mapToRecipeDto( recipe );
    }

    /**
     * Returns the recipe with the given name
     *
     * @param recipeName
     *            recipe's name
     * @return the recipe with the given name.
     * @throws ResourceNotFoundException
     *             if the recipe doesn't exist
     */
    @Override
    public RecipeDto getRecipeByName ( final String recipeName ) {
        final Recipe recipe = recipeRepository.findByName( recipeName )
                .orElseThrow( () -> new ResourceNotFoundException( "Recipe does not exist with name " + recipeName ) );
        return RecipeMapper.mapToRecipeDto( recipe );
    }

    /**
     * Returns true if the recipe already exists in the database.
     *
     * @param recipeName
     *            recipe's name to check
     * @return true if already in the database
     */
    @Override
    public boolean isDuplicateName ( final String recipeName ) {
        try {
            getRecipeByName( recipeName );
            return true;
        }
        catch ( final ResourceNotFoundException e ) {
            return false;
        }
    }

    /**
     * Returns a list of all the recipes
     *
     * @return all the recipes
     */
    @Override
    public List<RecipeDto> getAllRecipes () {
        final List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream().map( ( recipe ) -> RecipeMapper.mapToRecipeDto( recipe ) )
                .collect( Collectors.toList() );
    }

    /**
     * Updates the recipe with the given id with the recipe information
     *
     * @param recipeId
     *            id of recipe to update
     * @param recipeDto
     *            values to update
     * @return updated recipe
     * @throws ResourceNotFoundException
     *             if the recipe doesn't exist
     */
    @Override
    public RecipeDto updateRecipe ( final Long recipeId, final RecipeDto recipeDto ) {
        final Recipe recipe = recipeRepository.findById( recipeId )
                .orElseThrow( () -> new ResourceNotFoundException( "Recipe does not exist with id " + recipeId ) );

        // Check for duplicate name (if name is being changed)
        if ( !recipe.getName().equals( recipeDto.getName() ) && isDuplicateName( recipeDto.getName() ) ) {
            throw new IllegalArgumentException( "Recipe name already exists: " + recipeDto.getName() );
        }

        recipe.setName( recipeDto.getName() );
        recipe.setPrice( recipeDto.getPrice() );
        recipe.setIngredients( recipeDto.getIngredients() );

        final Recipe savedRecipe = recipeRepository.save( recipe );
        return RecipeMapper.mapToRecipeDto( savedRecipe );
    }

    /**
     * Deletes the recipe with the given id
     *
     * @param recipeId
     *            recipe's id
     * @throws ResourceNotFoundException
     *             if the recipe doesn't exist
     */
    @Override
    public void deleteRecipe ( final Long recipeId ) {
        final Recipe recipe = recipeRepository.findById( recipeId )
                .orElseThrow( () -> new ResourceNotFoundException( "Recipe does not exist with id " + recipeId ) );

        recipeRepository.delete( recipe );
    }
}
