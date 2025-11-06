package edu.ncsu.csc326.wolfcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Used to get and set the current tax rate of the system
 *
 * @author Brooke Wu (bwu25)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxDto {

	/** Current tax rate as a percentage, default is 2.00% */
	private int currentAmount;
}
