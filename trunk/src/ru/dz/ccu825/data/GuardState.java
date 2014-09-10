package ru.dz.ccu825.data;

import java.util.logging.Logger;


/**
 * Represents state of a guard zone (partition) = what CCU does for that zone: 
 * <ul>
 * 	<li>Guarding (Arm)
 * 	<li>Protecting
 * 	<li>Just monitoring (Disarm)
 * </ul>
 * 
 * See CCU825 manual for details
 * 
 * <br>
 * @author dz
 *
 */

public enum GuardState {
	/** 
	 * Unknown state is used as a 'No state change' in PartitionStateCmd
	 */
	Unknown, // No change in modify style calls
	Disarm,
	Protect,
	Arm;
	
	private static final Logger log = Logger.getLogger(GuardState.class.getName()); 

	
	/**
	 * Convert to bits for a PartitionStateCmd packet payload
	 * @return 2 bits of encoded state
	 */
	
	public byte toCmdBits()
	{
		switch(this)
		{
		case Unknown: 	return 0;	
		case Disarm: 	return 2;
		case Protect: 	return 1;
		case Arm: 		return 3;
		}
		
		return 0; // Can't be
	}
	
	/**
	 * Make from PartitonState.State bits 
	 * @param i PartitonState packet State field
	 */
	public static GuardState fromStateBits(int i) 
	{
		// take 2 low bits
		i &= 3;
		switch(i)
		{
			case 0: return Disarm;
			case 1: return Arm;
			case 2: return Protect;
			
			default:
				log.severe("Unknown GuardState value in fromStateBits()!");
				return Arm;  
		}
	}
}
