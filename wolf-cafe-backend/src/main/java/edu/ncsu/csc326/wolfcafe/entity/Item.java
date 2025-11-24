package edu.ncsu.csc326.wolfcafe.entity;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an item for sale in the WolfCafe.
 *
 * @author Dania Swelam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "items" )
@JsonIgnoreProperties ( { "hibernateLazyInitializer", "handler" } )
public class Item {

    /** Item id */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long                 id;

    /** Item name */
    @Column ( nullable = false, unique = true )
    private String               name;

    /** Item description */
    private String               description;

    /** Item price */
    @Column ( nullable = false )
    private double               price;

    /** List of ingredients and their initial amounts */
    @ElementCollection ( fetch = FetchType.EAGER )
    @CollectionTable ( name = "item_ingredients", joinColumns = @JoinColumn ( name = "item_id" ) )
    @MapKeyColumn ( name = "ingredient_name" )
    @Column ( name = "ingredient_amount" )
    private Map<String, Integer> ingredients = new HashMap<>();
}
