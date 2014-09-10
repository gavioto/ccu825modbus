package ru.dz.ccu825;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Logger;

import ru.dz.ccu825.util.CCU825CheckSumException;
import ru.dz.ccu825.util.CCU825PacketFormatException;
import ru.dz.ccu825.util.CRC16;
import ru.dz.ccu825.util.RC4;

/**
 * Represents CCU825-SM protocol packet. Does assembly/disassembly.
 * 
 * @author dz
 *
 */

public class CCU825Packet {
	private final static Logger log = Logger.getLogger(CCU825Packet.class.getName());


	private static final int PKT_MAX_PAYLOAD = 1545;
	private static final int PKT_HEADER_LEN = 8;

	public static final int MAXPACKET = PKT_MAX_PAYLOAD + PKT_HEADER_LEN + 1; // + 1 to make it even
	
	public static final byte PKT_FLAG_ENC = 0x01;
	public static final byte PKT_FLAG_SYN = 0x02;
	public static final byte PKT_FLAG_ACK = 0x04;


	// Answers
	public static final byte PKT_TYPE_RETCODE = 0x01;
	public static final byte PKT_TYPE_DEVICEINFO = 0x02;
	public static final byte PKT_TYPE_SYSINFO = 0x03;
	public static final byte PKT_TYPE_EVENTS = 0x04;
	public static final byte PKT_TYPE_PARTITIONSTATE = 0x05;
	public static final byte PKT_TYPE_OUTSTATE = 0x06;

	// REQUESTS
	public static final byte PKT_TYPE_EMPTY = 0x00;

	public static final byte PKT_TYPE_INFOREQ = 0x01;
	
	// 2nd byte for PKT_TYPE_INFOREQ req
	public static final byte PKT_TYPE_DEVICEINFO_SUBREQ = 0x01;
	public static final byte PKT_TYPE_SYSINFO_SUBREQ = 0x00;
	public static final byte PKT_TYPE_OUTSTATE_SUBREQ = 0x03;
	public static final byte PKT_TYPE_PARTITIONSTATE_SUBREQ = 0x02;
	

	

	private byte[] data;
	private byte[] payload;
	
	/**
	 * Construct packet object from raw protocol data received from ModBus IO transaction.
	 * 
	 * @param data What we've got from ModBus fn23 
	 * @param key 
	 * 
	 * @throws CCU825CheckSumException
	 * @throws CCU825PacketFormatException
	 */
	
	public CCU825Packet( byte [] data, byte[] key ) throws CCU825CheckSumException, CCU825PacketFormatException {
		if( data.length < PKT_HEADER_LEN )
			throw new CCU825PacketFormatException("packet len < " + PKT_HEADER_LEN);

		if( data[0] != 0x01 )
			throw new CCU825PacketFormatException("Wrong header byte");
		
		ByteBuffer bb = ByteBuffer.wrap(data);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		short pktLen = bb.getShort(6);
		short pktCs = bb.getShort(4);
		
		int plen = checkLen(data, pktLen );		
		checkCheckSum( data, pktLen+8, pktCs );

		// Packet is ok
		
		this.data = data;
		
		payload = new byte[plen];
		System.arraycopy(data, PKT_HEADER_LEN, payload, 0, plen);
		
		if( isEnc() )
		{
			RC4 dec = new RC4(key);
			payload = dec.decrypt(payload);
		}
		
	}



	/**
	 * Get raw packet to send to ModBus fn23.
	 * @return packet bytes.
	 */

	public byte[] getPacketBytes() {
		return data;
	}


	/**
	 * Get packet payload data (offset 8).
	 * @return Payload bytes.
	 */
	
	public byte[] getPacketPayload() {
		return payload;
	}
	
	
	/**
	 * Set packet's SYN header flag.
	 * @param b set or reset
	 */
	
	public void setSyn(boolean b) {
		if( b )
			data[1] |= PKT_FLAG_SYN;
		else
			data[1] &= ~PKT_FLAG_SYN;
	}
	
	

	public void setEnc(boolean encryptionEnabled) {
		if( encryptionEnabled )
			data[1] |= PKT_FLAG_ENC;
		else
			data[1] &= ~PKT_FLAG_ENC;
	}

		
	/**
	 * @return True if packet header has SYN flag.
	 */
	
	public boolean isSyn() {
		return (data[1] & PKT_FLAG_SYN) != 0;
	}

	/**
	 * @return True if packet header has ACK flag.
	 */
	
	public boolean isAck() {
		return (data[1] & PKT_FLAG_ACK) != 0;
	}
	
	
	/**
	 * @return True if packet header has ENC flag. (Packet payload is encrypted)	
	 */
	
	public boolean isEnc() {
		return (data[1] & PKT_FLAG_ENC) != 0;
	}
	
	
	
	/**
	 * Check if packet length field is correct. 
	 * 
	 * @param data Packet data bytes
	 * @param recvLen 
	 * @return Actual payload length
	 * @throws CCU825PacketFormatException Length value is insane
	 */
	
	private int checkLen(byte[] data, short recvLen) throws CCU825PacketFormatException  {
		
		/*
		int rll = data[6];
		int rlh = data[7];
		
		rll &= 0xFF;
		rlh &= 0xFF;
		
		int recvLen = (rlh << 8) | rll; 
		*/
		if( recvLen+8 > data.length )
			throw new CCU825PacketFormatException("got len=" + recvLen+8 + " in pkt, actual "+ data.length);
		
		return recvLen;
	}
	
	/**
	 * Check if packet checksum is correct.
	 * NB! Clears checksum bytes in packet!
	 * @param data Packet data.
	 * @param recvCheckSum 
	 * @throws CCU825CheckSumException Checksum was wrong.
	 */
	
	private void checkCheckSum(byte[] data, int len, int recvCheckSum) throws CCU825CheckSumException {
		/*
		int rcl = data[4];
		int rch = data[5];
		
		rcl &= 0xFF;
		rch &= 0xFF;
		
		int recvCheckSum = (rch << 8) | rcl; 
		*/
		
		
		data[4] = 0;
		data[5] = 0;
		
		int calcCheckSum = makeCheckSum(data,len);
		
		if( calcCheckSum != (recvCheckSum & 0xFFFF) )
		{
			String msg = String.format( "got checksum=%04X in pkt, calculated=%04X", recvCheckSum, calcCheckSum );
			log.severe( msg );
			//throw new CCU825CheckSumException("got checksum=" + Integer.toHexString(recvCheckSum) + " in pkt, calculated="+ Integer.toHexString(calcCheckSum) );
			//throw new CCU825CheckSumException( msg );
		}
	}

	
	/** Calc a checksum for a packet.
	 * 
	 * @param data packet
	 * @param len 
	 * @return 16 bits of a checksum
	 */
	
	private int makeCheckSum(byte[] data, int len) {
		return CRC16.crc(data,len) & 0xFFFF; // Make sure int has just 16 bits
	}
	
	
	
	/**
	 * Construct packet for transmission from flags byte and payload. 
	 * @param flags
	 * @param payload
	 */
	
	
	protected CCU825Packet( byte flags, byte [] payload ) {

		assert( payload.length <= PKT_MAX_PAYLOAD );
		
		this.payload = payload;
		
		int outSize = payload.length + PKT_HEADER_LEN;
		
		if( (outSize & 0x01 ) != 0 )
			outSize++;
		
		byte[] out = new byte[outSize];
		
		System.arraycopy(payload, 0, out, PKT_HEADER_LEN, payload.length);
		
		ByteBuffer bb = ByteBuffer.wrap(out);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		
		out[0] = 0x01;
		out[1] = flags;
		
		out[2] = 0; // seq - io code will do  
		out[3] = 0; // ack - io code will do  

		out[4] = 0; // csum  
		out[5] = 0; //   

		//out[6] = (byte) ( payload.length & 0xFF );  
		//out[7] = (byte) ((payload.length >> 8) & 0xFF );    		
		bb.putShort(6, (short)payload.length);
		
		int calcCheckSum = makeCheckSum(out,outSize);
		
		//out[4] = (byte) ( calcCheckSum & 0xFF );  
		//out[5] = (byte) ((calcCheckSum >> 8) & 0xFF );    		
		bb.putShort(4, (short)calcCheckSum);
		
		
		data = out;
	}




	public void setSeqNum(int seq) { data[2] = (byte)seq; }
	public void setAckNum(int seq) { data[3] = (byte)seq; }


	public int getSeqNum() { return data[2]; }
	public int getAckNum() { return data[3]; }






	
}
