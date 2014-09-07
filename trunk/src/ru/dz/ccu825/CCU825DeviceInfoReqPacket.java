package ru.dz.ccu825;

public class CCU825DeviceInfoReqPacket extends CCU825Packet {

	private static final byte[] data = { CCU825Packet.PKT_TYPE_DEVICEINFOREQ, 0x01 } ;

	public CCU825DeviceInfoReqPacket()
			throws CCU825CheckSumException, CCU825PacketFormatException {
		
		super( (byte)0x00, data);

	}

}
