package ru.dz.mercury;

import ru.dz.mercury.pkt.Packet;

public class Mercury230UnexpectedPacketException extends Mercury230ProtocolException {

	private static final long serialVersionUID = 84317507414523850L;
	private final Packet packet;

	public Mercury230UnexpectedPacketException(Packet packet, String string) {
		super(string);
		this.packet = packet;
	}

	public Packet getPacket() {
		return packet;
	}


}
