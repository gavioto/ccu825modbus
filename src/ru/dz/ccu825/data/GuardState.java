package ru.dz.ccu825.data;

public enum GuardState {
	Unknown, // No change in modify style calls
	Disarm,
	Protect,
	Arm;
	
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
			
			default: return Arm; // TODO make noise 
		}
	}
}
