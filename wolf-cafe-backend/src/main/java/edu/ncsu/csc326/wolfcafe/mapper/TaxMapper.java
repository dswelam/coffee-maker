package edu.ncsu.csc326.wolfcafe.mapper;

import edu.ncsu.csc326.wolfcafe.dto.TaxDto;
import edu.ncsu.csc326.wolfcafe.entity.Tax;

/**
 * Converts between TaxDto and Tax entity.
 *
 * @author Brooke Wu
 */
public class TaxMapper {

    /**
     * Converts an Tax entity to TaxDto
     *
     * @param tax
     *            Tax to convert
     * @return TaxDto object
     */
    public static TaxDto mapToTaxDto ( final Tax tax ) {
        return new TaxDto( tax.getCurrentAmount() );
    }

    /**
     * Converts an TaxDto to an Tax entity
     *
     * @param taxDto
     *            TaxDto to convert
     * @return Tax entity
     */
    public static Tax mapToTax ( final TaxDto taxDto ) {
        return new Tax( 1L, taxDto.getCurrentAmount() );

    }
}
