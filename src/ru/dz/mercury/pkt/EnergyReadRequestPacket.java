package ru.dz.mercury.pkt;

public class EnergyReadRequestPacket extends Packet {

	public EnergyReadRequestPacket(int type, int tariff) {
		super(PKT_TYPE_READ_ENERGY,makeTwoBytesPayload(type,tariff));
	}

	
}
