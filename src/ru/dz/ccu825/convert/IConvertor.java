package ru.dz.ccu825.convert;

public interface IConvertor {

	/**
	 * Do a forward conversion
	 * @param in 
	 * @return
	 */
	double convert( double in );
	
	/**
	 * Do a backward conversion
	 * @param in 
	 * @return
	 */
	double convertBack( double in );
	
}
