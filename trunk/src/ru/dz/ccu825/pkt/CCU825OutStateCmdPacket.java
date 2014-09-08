package ru.dz.ccu825.pkt;

import ru.dz.ccu825.CCU825CheckSumException;
import ru.dz.ccu825.CCU825Packet;
import ru.dz.ccu825.CCU825PacketFormatException;

public class CCU825OutStateCmdPacket extends CCU825Packet {
	
	//private static final byte[] data = new byte[9];

	public static final int N_OUT_BITS = 7;

	public CCU825OutStateCmdPacket(int bits, int mask)
			throws CCU825CheckSumException, CCU825PacketFormatException {
		
		super( (byte)0x00, makeReq(bits,mask));

	}

	private static byte[] makeReq(int bits, int mask) 
	{
		byte[] data = new byte[9];
		
		data[0] = CCU825Packet.PKT_TYPE_INFOREQ;
		data[1] = CCU825Packet.PKT_TYPE_OUTSTATE_SUBREQ;
	
		for( int i = 0; i < N_OUT_BITS; i++)
		{
			if( (mask & 1) == 0 )
			{
				data[2+i] = 0;
			}
			else
			{
				data[2+i] = (byte)(((bits & 1) != 0) ? 1 : 2);
			}
			
			bits >>= 1;
			mask >>= 1;
		}

		return data;
	}


}
