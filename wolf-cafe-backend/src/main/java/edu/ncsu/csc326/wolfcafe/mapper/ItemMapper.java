package edu.ncsu.csc326.wolfcafe.mapper;

import java.util.HashMap;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;

/**
 * Converts between Item entity and ItemDto
 *
 * @author Brooke Wu
 */
public class ItemMapper {

    /**
     * Converts a Item entity to ItemDto
     *
     * @param item
     *            Item to convert
     * @return ItemDto object
     */
    public static ItemDto mapToItemDto ( final Item item ) {
        final ItemDto dto = new ItemDto();
        dto.setId( item.getId() );
        dto.setName( item.getName() );
        dto.setPrice( item.getPrice() );
        dto.setIngredients( new HashMap<>( item.getIngredients() ) );
        return dto;
    }

    /**
     * Converts a ItemDto object to a Item entity.
     *
     * @param itemDto
     *            ItemDto to convert
     * @return Item entity
     */
    public static Item mapToItem ( final ItemDto itemDto ) {
        return new Item( itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getPrice(),
                itemDto.getIngredients() );
    }

}
