package ru.dz.mercury.pkt;

public class LogReadRequestPacket extends Packet {

	public LogReadRequestPacket(int nParam, int nRecord) {
		super(PKT_TYPE_READ_RECORD,makePayload(nParam,nRecord));
	}

	private static byte[] makePayload(int nParam, int nRecord) {
		if(nParam == 0)
		{
			byte[] payload = new byte[1];
			payload[0] = 0;
			return payload;
		}
		else
		{
			byte[] payload = new byte[2];

			payload[0] = (byte)nParam;
			payload[1] = (byte)nRecord;

			return payload;
		}
	}

}
