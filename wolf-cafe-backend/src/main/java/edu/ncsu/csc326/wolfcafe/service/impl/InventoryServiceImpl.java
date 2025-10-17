
package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import edu.ncsu.csc326.wolfcafe.exception.InvalidIngredientAmountException;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.InventoryMapper;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;

/**
 * Implementation of the InventoryService interface.
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Creates the inventory.
     *
     * @param inventoryDto
     *            inventory to create
     * @return updated inventory after creation
     */
    @Override
    public InventoryDto createInventory ( final InventoryDto inventoryDto ) {
        final Inventory inventory = InventoryMapper.mapToInventory( inventoryDto );
        final Inventory savedInventory = inventoryRepository.save( inventory );
        return InventoryMapper.mapToInventoryDto( savedInventory );
    }

    /**
     * Returns the single inventory.
     *
     * @return the single inventory
     */
    @Override
    public InventoryDto getInventory () {
        final List<Inventory> inventory = inventoryRepository.findAll();
        if ( inventory.size() == 0 ) {
            final InventoryDto newInventoryDto = new InventoryDto();
            final InventoryDto savedInventoryDto = createInventory( newInventoryDto );
            return savedInventoryDto;
        }
        return InventoryMapper.mapToInventoryDto( inventory.get( 0 ) );
    }

    /**
     * Updates the contents of the inventory.
     *
     * @param inventoryDto
     *            values to update
     * @return updated inventory
     */
    @Override
    public InventoryDto updateInventory ( final InventoryDto inventoryDto ) {
        final Inventory inventory = inventoryRepository.findById( 1L ).orElseThrow(
                () -> new ResourceNotFoundException( "Inventory does not exist with id of " + inventoryDto.getId() ) );

        final Map<String, Integer> currentIngredients = inventory.getIngredients();
        final Map<String, Integer> additionalIngredients = inventoryDto.getIngredients();

        for ( final Map.Entry<String, Integer> entry : additionalIngredients.entrySet() ) {
            final String ingredient = entry.getKey();
            final Integer amountToAdd = entry.getValue();
            if ( amountToAdd == null || amountToAdd < 0 ) {
                throw new InvalidIngredientAmountException(
                        "Invalid amount for " + ingredient + ". Must be a positive integer" );
            }
            final int newAmount = currentIngredients.getOrDefault( ingredient, 0 ) + amountToAdd;
            currentIngredients.put( ingredient, newAmount );
        }

        inventory.setIngredients( currentIngredients );
        final Inventory savedInventory = inventoryRepository.save( inventory );
        return InventoryMapper.mapToInventoryDto( savedInventory );
    }

}
