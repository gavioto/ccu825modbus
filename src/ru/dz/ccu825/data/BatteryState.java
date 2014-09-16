package ru.dz.ccu825.data;

import java.util.logging.Logger;

public enum BatteryState {
	BatteryNotUsed,
	BatteryNotConnected,
	BatteryDischargeLevel2,
	BatteryDischargeLevel1,
	BatteryCharged;

	private static final Logger log = Logger.getLogger(BatteryState.class.getName()); 
	
	/**
	 * Make from PartitonState.State bits 
	 * @param i PartitonState packet State field
	 */
	public static BatteryState fromStateBits(int i) 
	{
		// take 3 low bits
		i &= 7;
		switch(i)
		{
			case 0: return BatteryNotUsed;
			case 1: return BatteryNotConnected;
			case 2: return BatteryDischargeLevel2;
			case 3: return BatteryDischargeLevel1;
			case 4: return BatteryCharged;

			
			default:
				log.severe("Unknown BatteryState value in fromStateBits()!");
				return BatteryNotUsed;  
		}
	}

	
}
