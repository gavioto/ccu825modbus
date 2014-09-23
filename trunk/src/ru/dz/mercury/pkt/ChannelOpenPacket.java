package ru.dz.mercury.pkt;

import ru.dz.mercury.Mercury230ProtocolException;

public class ChannelOpenPacket extends Packet {

	public ChannelOpenPacket(int address, int level, String passwd) throws Mercury230ProtocolException {
		super(address,PKT_TYPE_CHANNEL_OPEN,makePayload(level,passwd));
	}

	private static byte[] makePayload(int level, String passwd) throws Mercury230ProtocolException 
	{
		if(passwd.length() > MAX_PASSWD_LEN)
			throw new Mercury230ProtocolException("password too long");

		byte[] payload = new byte[7];

		payload[0] = (byte)level;

		// TODO convert to one byte string! Russian letters won't work
		for(int i = 0; i < MAX_PASSWD_LEN; i++)
		{
			payload[i+1] = (byte) passwd.charAt(i); 
		}

		return payload;
	}
	
}
