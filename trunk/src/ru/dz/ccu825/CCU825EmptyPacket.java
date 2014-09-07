package ru.dz.ccu825;

public class CCU825EmptyPacket extends CCU825Packet {

	private static final byte[] data = { CCU825Packet.PKT_TYPE_EMPTY } ;

	public CCU825EmptyPacket()
			throws CCU825CheckSumException, CCU825PacketFormatException {
		
		super( (byte)0x00, data );

	}

}
