package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.IngredientMapper;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.service.IngredientService;

/**
 * Implementation of the IngredientService interface.
 *
 * @author Nora Cam (nncam)
 */
@Service
public class IngredientServiceImpl implements IngredientService {

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private IngredientRepository ingredientRepository;

    @Override
    public IngredientDto createIngredient ( IngredientDto ingredientDto ) {
        Ingredient ingredient = IngredientMapper.mapToIngredient( ingredientDto );
        Ingredient savedIngredient = ingredientRepository.save( ingredient );
        return IngredientMapper.mapToIngredientDto( savedIngredient );
    }

    @Override
    public IngredientDto getIngredientById ( Long ingredientId ) {
        Ingredient ingredient = ingredientRepository.findById( ingredientId ).orElseThrow(
                () -> new ResourceNotFoundException( "Ingredient does not exist with id " + ingredientId ) );
        return IngredientMapper.mapToIngredientDto( ingredient );
    }

    @Override
    public IngredientDto getIngredientByName ( String ingredientName ) {
        Ingredient ingredient = ingredientRepository.findByName( ingredientName ).orElseThrow(
                () -> new ResourceNotFoundException( "Ingredient does not exist with name " + ingredientName ) );
        return IngredientMapper.mapToIngredientDto( ingredient );
    }

    @Override
    public boolean isDuplicateName ( String ingredientName ) {
        try {
            getIngredientByName( ingredientName );
            return true;
        }
        catch ( ResourceNotFoundException e ) {
            return false;
        }
    }

    @Override
    public List<IngredientDto> getAllIngredients () {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        return ingredients.stream().map( ( ingredient ) -> IngredientMapper.mapToIngredientDto( ingredient ) )
                .collect( Collectors.toList() );
    }

    @Override
    public IngredientDto updateIngredient ( Long ingredientId, IngredientDto ingredientDto ) {
        Ingredient ingredient = ingredientRepository.findById( ingredientId ).orElseThrow(
                () -> new ResourceNotFoundException( "Ingredient does not exist with id " + ingredientId ) );

        ingredient.setName( ingredientDto.getName() );

        Ingredient savedIngredient = ingredientRepository.save( ingredient );

        return IngredientMapper.mapToIngredientDto( savedIngredient );
    }

    @Override
    public void deleteIngredient ( Long ingredientId ) {
        Ingredient ingredient = ingredientRepository.findById( ingredientId ).orElseThrow(
                () -> new ResourceNotFoundException( "Ingredient does not exist with id " + ingredientId ) );

        ingredientRepository.delete( ingredient );
    }

}
