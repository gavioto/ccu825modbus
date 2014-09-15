package ru.dz.ccu825.data;

/**
 * 
 * Part of Event packet payload, one byte event type.
 * 
 * @author dz
 *
 */
public class GuardEvent {

	/**
	 * Return human-readable event name.
	 * 
	 * @param _event event type byte
	 * @return event name
	 */
	public static String eventName(byte _event) {
		int e = _event;
		
		e &= 0xFF; // positive
		
		if( (e >= 0) && (e < 8) )
			return String.format("in %d active", e-0 );
		if( (e >= 44) && (e < 52) )
			return String.format("in %d active", e-44+8 );
	
	
		if( (e >= 8) && (e < 16) )
			return String.format("in %d inactive", e-8 );
		if( (e >= 52) && (e < 60) )
			return String.format("in %d inactive", e-52+8 );
	
		if( (e >= 29) && (e < 37) )
			return String.format("inf. msg in %d", e-29 );
		if( (e >= 60) && (e < 68) )
			return String.format("inf. msg in %d", e-60+8 );
		
		if( (e >= 37) && (e < 44) )
			return String.format("inf. msg out %d", e-37 );
	
		if( (e >= 74) && (e < 82) )
			return String.format("profile %d", e-74 );
		
		switch(e)
		{
		case 16:	return "Arm";
		case 17:	return "Disarm";
		case 18:	return "Protect";
		case 19:	return "Pwr fail";
		case 20:	return "Pwr ok";
		case 21:	return "Battery discharge 1";
		case 22:	return "Battery discharge 2";
		case 23:	return "GSM balance low";
		case 24:	return "Temp low";
		case 25:	return "Temp ok";
		case 26:	return "Temp hi";
		case 27:	return "Case open";
		case 28:	return "Test event";

		case 68:	return "Arm 2";
		case 69:	return "Disarm 2";
		case 70:	return "Arm 3";
		case 71:	return "Disarm 3";
		case 72:	return "Arm 4";
		case 73:	return "Disarm 4";
		
		case 82:	return "FW update attempt";
		case 83:	return "GPRS connection requested";

		case 84:	return "Device turned on";
		case 85:	return "Device restarted";
		}
		
		return String.format("?%02x", e );
	}
	
	
}
