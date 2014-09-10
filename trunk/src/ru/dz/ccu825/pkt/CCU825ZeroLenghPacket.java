package ru.dz.ccu825.pkt;

import ru.dz.ccu825.CCU825Packet;
import ru.dz.ccu825.util.CCU825CheckSumException;
import ru.dz.ccu825.util.CCU825PacketFormatException;

public class CCU825ZeroLenghPacket extends CCU825Packet {

	private static final byte[] data = {  } ;

	public CCU825ZeroLenghPacket()
			throws CCU825CheckSumException, CCU825PacketFormatException {
		
		super( (byte)0x00, data );

	}

}
