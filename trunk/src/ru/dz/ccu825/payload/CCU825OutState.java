package ru.dz.ccu825.payload;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.dz.ccu825.CCU825Packet;
import ru.dz.ccu825.pkt.CCU825OutStateCmdPacket;
import ru.dz.ccu825.util.CCU825PacketFormatException;

public class CCU825OutState {

	
	private byte outBits;
	
	

	public CCU825OutState(byte [] in ) throws CCU825PacketFormatException {
		ByteBuffer bb = ByteBuffer.wrap(in);
		
		bb.order(ByteOrder.LITTLE_ENDIAN);
		
		if( in[0] != CCU825Packet.PKT_TYPE_OUTSTATE )
			throw new CCU825PacketFormatException("Wrong OutState payload header byte");
		
		outBits = 0;
		
		for( int i = 0; i < CCU825OutStateCmdPacket.N_OUT_BITS; i++ )
		{
			if( in[1+i]!= 0 ) outBits |= 1 << i; 
		}
		
	}

	
	
	@Override
	public String toString() {	
		return "Out bits "+outBits;
	}
		

	public byte getOutBits() {
		return outBits;
	}


	
}
