package ru.dz.ccu825.payload;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.dz.ccu825.CCU825Packet;
import ru.dz.ccu825.util.CCU825PacketFormatException;

/**
 * SysInfoEx payload decoder
 * @author dz
 *
 */


public class CCU825SysInfoEx extends AbstractSysInfo  {

	
	public static final int N_IN = 16;
	
	
	public CCU825SysInfoEx(byte [] in ) throws CCU825PacketFormatException {
		if( in[0] != CCU825Packet.PKT_TYPE_SYSINFO_EX )
			throw new CCU825PacketFormatException("Wrong SysInfoEx payload header byte");
		
		ByteBuffer bb = ByteBuffer.wrap(in);
		
		decodePayload(bb); 
		
	}


	/**
	 * Package private constructor, used to decode similar payload in other packets.
	 * @param bb data to decode, no type byte check!
	 */
	public CCU825SysInfoEx(ByteBuffer bb) 
	{		
		decodePayload(bb); 		
	}

	void decodePayload(ByteBuffer bb) {
		inValue = new double[N_IN];

		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		inBits = bb.getShort(1);		
		outBits = bb.get(35);
		
		for( int i = 0; i < N_IN; i++ )
		{
			inValue [i] = ((double)bb.getShort(i+3)) * 10.0 / 4095; 
		}
		
		// TODO extract as sub-object
		{
		byte S1 = bb.get(36);
		powerOk = (S1 & 0x08) != 0;		
		balanceValid = (S1 & 0x04) != 0;
		}
		
		{
		byte S2 = bb.get(37);
		caseOpen = (S2 & 0x01) != 0;
		}
		
		powerVoltage = ((double)bb.get(38))/10.0;
		
		batteryPercentage = bb.get(39);
		
		deviceTemperature = bb.get(40);
		
		GSMBalance = Float.intBitsToFloat( bb.getInt(41) );
	}

	
	
	@Override
	public int nInputs() {
		return N_IN;
	}
	
}
