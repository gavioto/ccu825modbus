package ru.dz.mercury.pkt;

public class ChannelTestPacket extends Packet {
	private static final byte [] empty = new byte[0];

	public ChannelTestPacket(int address) {
		super(address,PKT_TYPE_CHANNEL_TEST,empty);
	}
	
}
