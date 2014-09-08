package ru.dz.ccu825.pkt;

import ru.dz.ccu825.CCU825Packet;
import ru.dz.ccu825.data.GuardState;
import ru.dz.ccu825.util.CCU825CheckSumException;
import ru.dz.ccu825.util.CCU825PacketFormatException;

public class CCU825PartitionStateCmdPacket extends CCU825Packet {


	public CCU825PartitionStateCmdPacket( GuardState p1mode, GuardState p2mode, GuardState p3mode, GuardState p4mode )
			throws CCU825CheckSumException, CCU825PacketFormatException {
		
		super( (byte)0x00, makeReq(p1mode, p2mode, p3mode, p4mode));

	}

	private static byte[] makeReq(GuardState p1mode, GuardState p2mode, GuardState p3mode, GuardState p4mode) 
	{
		byte[] data = new byte[3];
		
		data[0] = CCU825Packet.PKT_TYPE_INFOREQ;
		data[1] = CCU825Packet.PKT_TYPE_PARTITIONSTATE_SUBREQ;
	
		data[2] = 0;
		
		data[2] |= p1mode.toCmdBits() << 0;
		data[2] |= p2mode.toCmdBits() << 2;
		data[2] |= p3mode.toCmdBits() << 4;
		data[2] |= p4mode.toCmdBits() << 6;

		return data;
	}


}
