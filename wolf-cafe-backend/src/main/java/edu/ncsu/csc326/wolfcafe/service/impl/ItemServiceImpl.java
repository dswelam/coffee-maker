package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.service.ItemService;
import lombok.AllArgsConstructor;

/**
 * Implemented item service
 *
 * @author Dania Swelam
 */
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    /** Item repository */
    private final ItemRepository itemRepository;

    /** Mapper class */
    private final ModelMapper    modelMapper;

    /**
     * Adds given item
     * @param itemDto item to add
     * @return added item
     */
    @Override
    public ItemDto addItem ( final ItemDto itemDto ) {
        final Item item = modelMapper.map( itemDto, Item.class );
        final Item savedItem = itemRepository.save( item );
        return modelMapper.map( savedItem, ItemDto.class );
    }

    /**
     * Returns all items
     * @return all items
     */
    @Override
    public List<ItemDto> getAllItems () {
        final List<Item> items = itemRepository.findAll();
        return items.stream().map( ( item ) -> modelMapper.map( item, ItemDto.class ) ).collect( Collectors.toList() );
    }

    /**
     * Updates the item with the given id
     * @param id id of item to update
     * @param itemDto information of item to update
     * @return updated item
     */
    @Override
    public ItemDto updateItem ( final Long id, final ItemDto itemDto ) {
        final Item item = itemRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Item not found with id " + id ) );
        item.setName( itemDto.getName() );
        item.setDescription( itemDto.getDescription() );
        item.setPrice( itemDto.getPrice() );
        final Item updatedItem = itemRepository.save( ( item ) );
        return modelMapper.map( updatedItem, ItemDto.class );
    }

    /**
     * Deletes the item with the given id
     * @param id id of item to delete
     */
    @Override
    public void deleteItem ( final Long id ) {
        itemRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Item not found with id " + id ) );
        itemRepository.deleteById( id );
    }

    /**
     * Returns the item with the given id.
     *
     * @param itemId
     *            item's id
     * @return the item with the given id
     * @throws ResourceNotFoundException
     *             if the item doesn't exist
     */
    @Override
    public ItemDto getItemById ( final Long itemId ) {
        final Item item = itemRepository.findById( itemId )
                .orElseThrow( () -> new ResourceNotFoundException( "Item does not exist with id " + itemId ) );
        return modelMapper.map( item, ItemDto.class );
    }

    /**
     * Returns the item with the given name
     *
     * @param itemName
     *            item's name
     * @return the item with the given name.
     * @throws ResourceNotFoundException
     *             if the item doesn't exist
     */
    @Override
    public ItemDto getItemByName ( final String itemName ) {
        final Item item = itemRepository.findByName( itemName )
                .orElseThrow( () -> new ResourceNotFoundException( "Item does not exist with name " + itemName ) );
        return modelMapper.map( item, ItemDto.class );
    }

    /**
     * Checks if item name is duplicate
     *
     * @param itemName name to check
     * @return true if duplicate, false otherwise
     */
    @Override
    public boolean isDuplicateName ( final String itemName ) {
        try {
            getItemByName( itemName );
            return true;
        }
        catch ( final ResourceNotFoundException e ) {
            return false;
        }
    }
}
