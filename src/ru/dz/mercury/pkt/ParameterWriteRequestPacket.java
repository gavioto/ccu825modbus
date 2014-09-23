package ru.dz.mercury.pkt;

import ru.dz.mercury.Mercury230ProtocolException;

public class ParameterWriteRequestPacket extends Packet {

	private static final int MAX_PARAM_LEN = 16;

	public ParameterWriteRequestPacket(int nParam, byte[] data) throws Mercury230ProtocolException {
		super(PKT_TYPE_WRITE_PARAMETER,makePayload(nParam,data));
	}

	private static byte[] makePayload(int nParam, byte[] data) throws Mercury230ProtocolException 
	{
		if(data.length > MAX_PARAM_LEN)
			throw new Mercury230ProtocolException("parameter too long");

		byte[] payload = new byte[data.length+1];

		payload[0] = (byte)nParam;
		System.arraycopy(data, 0, payload, 1, data.length);

		return payload;
	}

	@Override
	public byte[] getPacketBytes() {
		if(getAddress() == 0)
			log.severe("Attempt to write parameter with address == 0, will be ignored by Mercury device");
		return super.getPacketBytes();
	}
}
