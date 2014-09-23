package ru.dz.mercury.pkt;

public class ChannelClosePacket extends Packet {
	public ChannelClosePacket() {
		super(PKT_TYPE_CHANNEL_CLOSE,empty);
	}
	
}
