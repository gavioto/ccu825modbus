package ru.dz.ccu825.payload;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.dz.ccu825.CCU825Packet;
import ru.dz.ccu825.util.CCU825PacketFormatException;

/**
 * SysInfo payload decoder
 * @author dz
 *
 */


public class CCU825SysInfo extends AbstractSysInfo  {

	
	public static final int N_IN = 8;
	
	
	public CCU825SysInfo(byte [] in ) throws CCU825PacketFormatException {
		
		if( in[0] != CCU825Packet.PKT_TYPE_SYSINFO )
			throw new CCU825PacketFormatException("Wrong SysInfo payload header byte");
		
		ByteBuffer bb = ByteBuffer.wrap(in);
		
		decodePayload(bb); 		
	}

	/**
	 * Package private constructor, used to decode similar payload in other packets.
	 * @param bb data to decode, no type byte check!
	 */
	public CCU825SysInfo(ByteBuffer bb) 
	{		
		decodePayload(bb); 		
	}


	void decodePayload(ByteBuffer bb)
	{
		inValue = new double[N_IN];
		
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		//inBits = ((int)in[1]) & 0xFF;
		//outBits = ((int)in[18]) & 0xFF;
		inBits = ((int)bb.get(1)) & 0xFF;
		outBits = ((int)bb.get(18)) & 0xFF;
		
		for( int i = 0; i < N_IN; i++ )
		{
			inValue [i] = ((double)bb.getShort(i+2)) * 10.0 / 4095; 
		}

		// TODO extract as sub-object
		{
		byte S1 = bb.get(19);
		powerOk = (S1 & 0x08) != 0;		
		balanceValid = (S1 & 0x04) != 0;
		}
		
		{
		byte S2 = bb.get(20);
		caseOpen = (S2 & 0x01) != 0;
		}
		
		powerVoltage = ((double)bb.get(21))/10.0;
		
		batteryPercentage = bb.get(22);
		
		deviceTemperature = bb.get(23);
		
		GSMBalance = Float.intBitsToFloat( bb.getInt(24) );
	}

	
	
	@Override
	public int nInputs() {		return N_IN;	}
	
}
