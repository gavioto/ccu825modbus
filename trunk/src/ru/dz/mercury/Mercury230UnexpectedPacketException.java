package ru.dz.mercury;

public class Mercury230UnexpectedPacketException extends Mercury230ProtocolException {

	private static final long serialVersionUID = 84317507414523850L;
	private final byte[] packet;

	public Mercury230UnexpectedPacketException(byte[] packet, String string) {
		super(string);
		this.packet = packet;
	}

	public byte[] getPacket() {
		return packet;
	}


}
