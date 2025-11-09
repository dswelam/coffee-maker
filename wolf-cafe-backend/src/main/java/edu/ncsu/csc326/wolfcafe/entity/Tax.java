package edu.ncsu.csc326.wolfcafe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Tax rate for the WolfCafe. Tax is a Data Access Object (DAO) is tied
 * to the database using Hibernate libraries. TaxRepository provides the
 * methods for database CRUD operations.
 *
 * @author Brooke Wu
 */
@Entity
@Table ( name = "tax" )
public class Tax {

    /** id for tax entry */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long                 id;
    
    /** Current tax rate as a percentage, default is 2.00% */
	private int currentAmount;

	/**
     * Default constructor for Hibernate
     */
	public Tax() {
		super();
		this.id = 1L;
		this.currentAmount = 2;
	}

    /**
     * Constructor for an ingredient using an ID and a given amount
     *
     * @param id
     *            id of the tax
     * @param currentAmount
     *            current amount of the tax
     */
	public Tax(Long id, int currentAmount) {
		super();
		this.id = id;
		this.currentAmount = currentAmount;
	}

    /**
     * Constructor for an ingredient using a given amount
     *
     * @param currentAmount
     *            current amount of the tax
     */
	public Tax(int currentAmount) {
		super();
		this.currentAmount = currentAmount;
	}

    /**
     * Get the ID of the tax
     *
     * @return the id
     */
	public Long getId() {
		return id;
	}

    /**
     * Used by Hibernate to set the ID of the tax
     *
     * @param id
     *            the id to set
     */
	public void setId(Long id) {
		this.id = id;
	}

    /**
     * Get the current amount of the tax
     *
     * @return the current amount
     */
	public int getCurrentAmount() {
		return currentAmount;
	}

    /**
     * Sets the current amount of the tax
     *
     * @param currentAmount
     *            the current Amount to set
     */
	public void setCurrentAmount(int currentAmount) {
		this.currentAmount = currentAmount;
	}
	
}
