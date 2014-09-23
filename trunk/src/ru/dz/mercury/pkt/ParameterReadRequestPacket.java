package ru.dz.mercury.pkt;

public class ParameterReadRequestPacket extends Packet {

	public ParameterReadRequestPacket(int address,int nParam) {
		super(address,PKT_TYPE_READ_PARAMETER,makeOneBytePayload(nParam));
	}

	public ParameterReadRequestPacket(int address,int nParam1, int nParam2) {
		super(address,PKT_TYPE_READ_PARAMETER,makeTwoBytesPayload(nParam1,nParam2));
	}

	
}
