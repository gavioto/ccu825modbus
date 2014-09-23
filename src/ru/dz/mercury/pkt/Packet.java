package ru.dz.mercury.pkt;

import ru.dz.ccu825.util.CRC16;
import ru.dz.mercury.Mercury230CRCException;

public class Packet {

	public static final int MAX_PASSWD_LEN = 6;

	protected static final int PKT_TYPE_CHANNEL_TEST = 0;
	protected static final int PKT_TYPE_CHANNEL_OPEN = 1;
	protected static final int PKT_TYPE_READ_RECORD = 4;
	protected static final int PKT_TYPE_READ_PARAMETER = 8;

	
	
	private int address;
	private byte[] payload;
	private int requestCode;

	/**
	 * New packet for transmission.
	 * @param address Address to send packet to, 0 is broadcast.
	 * @param payload Packet payload part.
	 */
	protected Packet(int address, int requestCode, byte [] payload) {
		this.address = address;
		this.requestCode = requestCode;
		setPayload( payload );
	}

	/**
	 * Packet we just received.
	 * @param recvData Data from comm interface. 
	 * @throws Mercury230CRCException CRC is wrong
	 */
	public Packet(byte [] recvData) throws Mercury230CRCException 
	{
		checkCRC(recvData);
		
		setAddress( recvData[0] );
		this.requestCode = -1; // No request code in reply

		int pLen = recvData.length-3;
		payload = new byte[pLen];
		System.arraycopy(recvData, 1, payload, 0, pLen);
	}

	public int getAddress() {		return address;	}

	public void setAddress(int address) 
	{		
		address &= 0xFF;
		this.address = address;	
	}

	public byte[] getPayload() {		return payload;	}

	public void setPayload(byte[] payload) 
	{		
		int len = payload.length;
		this.payload = new byte[len];
		System.arraycopy(payload, 0, this.payload, 0, len);
	}
	
	
	
	// ---------------------------------------------------------------------------
	// CRC
	// ---------------------------------------------------------------------------

	public int getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	private int calcCRC(byte [] payload, int len)
	{
		return CRC16.crc(payload, len);
	}

	private void checkCRC(byte [] pkt) throws Mercury230CRCException
	{
		int pLen = pkt.length;

		int crc = calcCRC(pkt,pLen-2);

		if( 
				(pkt[pLen-2] != (byte) (crc & 0xFF)) 
				||
				(pkt[pLen-1] != (byte) ((crc>>8) & 0xFF))
				)
			throw new  Mercury230CRCException(pkt);
	}
	
	// ---------------------------------------------------------------------------
	// Send
	// ---------------------------------------------------------------------------
	
	public byte[] getPacketBytes()
	{
		
		int pLen = payload.length;
		byte[] toSend = new byte[pLen+4];

		toSend[0] = (byte) address;
		toSend[1] = (byte) requestCode;

		System.arraycopy(payload, 0, toSend, 2, pLen);

		int crc = calcCRC(toSend,pLen+2);

		toSend[pLen+2] = (byte) (crc & 0xFF);
		toSend[pLen+3] = (byte) ((crc>>8) & 0xFF);
		
		return toSend;
	}
	
	protected static byte[] makeOneBytePayload(int nParam) {
		byte[] payload = new byte[1];
		payload[0] = (byte) nParam;
		return payload;
	}
	
	protected static byte[] makeTwoBytesPayload(int nParam1, int nParam2) {
		byte[] payload = new byte[2];
		payload[0] = (byte) nParam1;
		payload[1] = (byte) nParam2;
		return payload;
	}
	
}
