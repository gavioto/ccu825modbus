package ru.dz.ccu825.convert;

/**
 * Converter interface.
 * <p>
 * Converters are used to, naturally, convert ADC voltage output to 
 * the physical value such as temperature or pressure, and vice versa.
 * 
 * @author dz
 *
 */

public interface IConvertor {

	/**
	 * Do a forward conversion
	 * @param in ADC value (voltage) 
	 * @return Physical value
	 */
	double convert( double in );
	
	/**
	 * Do a backward conversion
	 * @param in Physical value
	 * @return ADC value (voltage)
	 */
	double convertBack( double in );
	
}
