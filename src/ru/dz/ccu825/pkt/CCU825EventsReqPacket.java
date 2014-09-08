package ru.dz.ccu825.pkt;

import ru.dz.ccu825.CCU825CheckSumException;
import ru.dz.ccu825.CCU825Packet;
import ru.dz.ccu825.CCU825PacketFormatException;

public class CCU825EventsReqPacket extends CCU825Packet {
	
	// Not so intuitive, but...
	private static final byte[] data = { 0x00, 0x00 } ;

	public CCU825EventsReqPacket()
			throws CCU825CheckSumException, CCU825PacketFormatException {
		
		super( (byte)0x00, data);

	}


}
