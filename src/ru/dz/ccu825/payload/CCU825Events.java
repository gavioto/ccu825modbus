package ru.dz.ccu825.payload;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.dz.ccu825.CCU825Packet;
import ru.dz.ccu825.util.CCU825PacketFormatException;

/**
 * TODO Events packet payload decoder
 * @author dz
 *
 */

public class CCU825Events {

	


	public CCU825Events(byte [] in ) throws CCU825PacketFormatException {
		ByteBuffer bb = ByteBuffer.wrap(in);
		
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		if( in[0] != CCU825Packet.PKT_TYPE_EVENTS )
			throw new CCU825PacketFormatException("Wrong Events payload header byte");
		
		// TODO decode
		
		//inBits = in[1];
		
	}

	
/*	
	@Override
	public String toString() {
		
		return 
				"In bits "+inBits+" out bits "+outBits+" GSM balance "+GSMBalance+" battery "+batteryPercentage+"% temp "+deviceTemperature+" voltage "+
				powerVoltage
				;
	}
*/	
	
}
