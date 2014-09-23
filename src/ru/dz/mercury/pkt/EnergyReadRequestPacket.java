package ru.dz.mercury.pkt;

public class EnergyReadRequestPacket extends Packet {

	public EnergyReadRequestPacket(int address,int type, int tariff) {
		super(address,PKT_TYPE_READ_ENERGY,makeTwoBytesPayload(type,tariff));
	}

	
}
