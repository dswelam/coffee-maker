package edu.ncsu.csc326.wolfcafe.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Item for data transfer.
 *
 * @author Dania Swelam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    /** Item id */
    private Long                 id;

    /** Item name */
    private String               name;

    /** Item description */
    private String               description;

    /** Item price */
    private double               price;

    /** List of ingredients and their amounts */
    private Map<String, Integer> ingredients = new HashMap<>();
}
