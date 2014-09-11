package ru.dz.ccu825;

import java.util.logging.Logger;

import ru.dz.ccu825.payload.CCU825DeviceInfo;
import ru.dz.ccu825.payload.CCU825OutState;
import ru.dz.ccu825.payload.CCU825ReturnCode;
import ru.dz.ccu825.payload.CCU825SysInfo;
import ru.dz.ccu825.pkt.CCU825DeviceInfoAckPacket;
import ru.dz.ccu825.pkt.CCU825DeviceInfoReqPacket;
import ru.dz.ccu825.pkt.CCU825OutStateCmdPacket;
import ru.dz.ccu825.pkt.CCU825SysInfoReqPacket;
import ru.dz.ccu825.pkt.CCU825ZeroLenghPacket;
import ru.dz.ccu825.transport.ModBusConnection;
import ru.dz.ccu825.util.CCU825Exception;
import ru.dz.ccu825.util.CCU825PacketFormatException;
import ru.dz.ccu825.util.CCU825ProtocolException;
import ru.dz.ccu825.util.RC4;

/**
 *
 * Connection to an instance of CCU825 device. Implements device-specific protocol over the (implemented elsewhere) modbus.
 * <p>
 * Conforms to CCU825-SM protocol spec. from 5 September 2014, firmware ver. 01.02.
 * 
 * Needs an external modbus io driver to work.
 * 
 * @author dz
 *
 */

public class CCU825Connection {

	private static final int NTRIES = 5;

	private ModBusConnection mc;
	private byte[] key;

	private int currentSeq = 0;
	private int currentAck = 0;

	private int lastRecvSeq;

	private CCU825DeviceInfo deviceInfo;

	private boolean encryptionEnabled = false;

	private boolean dataDumpEnabled = false;

	/**
	 * Init a connection. Does nothing.
	 * 
	 * @param mc ModbBus connection implementation, used just for fn23 io
	 * @param key communications encryption key. Contact radsel to get one. Have your device IMEI handy.
	 * @throws CCU825Exception Mostly due to communication or protocol errors.
	 */

	public CCU825Connection( ModBusConnection mc, byte [] key  ) {
		this.mc = mc;
		this.key = key;
	}

	/**
	 * Connect and do handshake as required by protocol.
	 * @return handshake result.
	 * @throws CCU825Exception in case of communications error or device malfunction
	 */
	
	public CCU825ReturnCode connect() throws CCU825Exception
	{
		setupModBus();
		mc.connect();

		return initProtocol();
	}
	
	private void setupModBus() {
		mc.setSpeed(9600);		
	}


	/** 
	 * Do a protocol transaction sending and receiving one packet.
	 * 
	 * @param send Packet to send
	 * @return Packet received
	 * @throws CCU825ProtocolException 
	 */
	
	private CCU825Packet exchange( CCU825Packet send ) throws CCU825ProtocolException
	{
		send.setSeqNum( currentSeq++ );
		send.setAckNum( currentAck );
		//send.setAckNum( lastRecvSeq );

		send.setEnc(encryptionEnabled);

		byte [] packetBytes = send.getPacketBytes();

		int writeBytes = packetBytes.length;

		assert( (writeBytes & 1) == 0 );

		if(encryptionEnabled)
		{
			byte[] payload = new byte[writeBytes-8];
			System.arraycopy(packetBytes,8,payload,0,writeBytes-8);
			
			RC4 enc = new RC4(key);
			payload = enc.encrypt(payload);
			
			//spd = new byte[writeBytes];
			System.arraycopy(payload, 0, packetBytes, 8, writeBytes-8);
		}


		if( dataDumpEnabled ) CCU825Test.dumpBytes( "modbus send", packetBytes );
		byte[] rcv = mc.rwMultiple( CCU825Packet.MAXPACKET+1/2, packetBytes );
		if( dataDumpEnabled) CCU825Test.dumpBytes( "modbus recv", rcv );


		/*
		byte[] rpd;
		if(encryptionEnabled)
		{
			RC4 dec = new RC4(key);
			rpd = dec.decrypt(rcv);
			
			System.arraycopy(rcv, 0, rpd, 0, 8); // header is unencrypted
		}
		else
			rpd = rcv;
		*/
		
		CCU825Packet rp = new CCU825Packet(rcv,key);

		int recvAck = rp.getAckNum();
		lastRecvSeq = rp.getSeqNum();

		// It seems to be a "can't happen" thing in modbus-based proto, as modbus io is syncronous, but...

		if( recvAck != currentSeq )
		{
			String msg = "our seq = " + currentSeq + " recv ack = " + recvAck; 
			logErr(msg);
			throw new CCU825PacketFormatException(msg);
		}

		if( currentAck != lastRecvSeq )
			logErr("our ack = " + (currentAck) + " recv seq = " + lastRecvSeq);

		currentAck++;

		return rp;
	}

	/**
	 * Do an initial protocol transactions (handshake) as defined 
	 * in CCU825-SM protocol spec. from 5 September 2014, firmware ver. 01.02
	 * 
	 * @return device return code
	 * @throws CCU825Exception
	 */

	private CCU825ReturnCode initProtocol() throws CCU825Exception {
		int tries;

		// Start with no encryption
		setEncryptionEnabled(false);

		// 1. send/get syn
		//dataDumpEnabled = true;
		
		for( tries = NTRIES; tries > 0; tries-- )
		{

			try {
				CCU825ZeroLenghPacket sp = new CCU825ZeroLenghPacket();

				sp.setSyn( true );

				CCU825Packet rp = exchange( sp );

				byte[] rdata = rp.getPacketBytes();

				/*
				if( rdata[0] != CCU825Packet.PKT_TYPE_EMPTY )
					logErr("wrong packet type" + rdata[0] );
				*/
				if( !rp.isSyn() )
					throw new CCU825PacketFormatException("no syn in reply" + rdata[1]);
				//logErr("no syn in reply" + rdata[1] );

				if( !rp.isAck() )
					throw new CCU825PacketFormatException("no ack in reply" + rdata[1]);

				break;

			} catch( CCU825ProtocolException ex )
			{
				logProtoErr( ex );
			}

		}

		if( tries == 0 ) throw new CCU825Exception("Can't syn");

		// 2. req/get deviceInfo

		for( tries = NTRIES; tries > 0; tries-- )
		{

			try {
				CCU825Packet rp = exchange(new CCU825DeviceInfoReqPacket() );		
				deviceInfo = new CCU825DeviceInfo(rp.getPacketPayload());
				break;
			} catch( CCU825ProtocolException ex )
			{
				logProtoErr( ex );
			}

		}

		if( tries == 0 ) throw new CCU825Exception("Can't get device info");

		// Now switch to encrypted mode
		setEncryptionEnabled(true);
		
		// 3. Ack devinfo and get return code

		int protocolRC = 0;

		for( tries = NTRIES; tries > 0; tries-- )
		{

			try {
				CCU825Packet rp = exchange(new CCU825DeviceInfoAckPacket() );

				byte[] rdata = rp.getPacketPayload();

				if( rdata[0] != CCU825Packet.PKT_TYPE_RETCODE )
					logErr("wrong packet type" + rdata[0] );

				protocolRC = rdata[1];
				break;
				
			} catch( CCU825ProtocolException ex )
			{
				logProtoErr( ex );
			}

		}

		if( tries == 0 ) throw new CCU825Exception("Can't ack device info");

		return new CCU825ReturnCode( protocolRC );		
	}


	private static final Logger log = Logger.getLogger(CCU825Connection.class.getName()); 

	private void logErr(String string) {
		//System.err.println("Error: "+ string );
		log.severe(string);
	}

	private void logProtoErr(CCU825ProtocolException ex) {
		//System.err.println("Protocol err: "+ex.getMessage());
		log.severe("Protocol err: "+ex.getMessage());
	}




	/**
	 * Does a request
	 * @return sysinfo (i/o state etc) at the current moment
	 * @throws CCU825ProtocolException
	 */
	public CCU825SysInfo getSysInfo() throws CCU825ProtocolException 
	{
		CCU825Packet rp = exchange(new CCU825SysInfoReqPacket() );
		return new CCU825SysInfo(rp.getPacketPayload());
	}

	/**
	 * Access informaion about the device such as IMEI, HW version, etc.
	 * 
	 * @return device info as we got in protocol init transaction
	 */
	public CCU825DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}


	/**
	 * Does a request to change outputs. Can change a group of outputs at once. 
	 * 
	 * @param state new state of ouputs, bit per output
	 * @param mask only outputs with 1's in corresponding mask bits are changed
	 * 
	 * @return actual outputs state from device
	 * @throws CCU825ProtocolException
	 */
	public int setOutState( int state, int mask ) throws CCU825ProtocolException
	{
		CCU825Packet rp = exchange(new CCU825OutStateCmdPacket( state, mask ) );
		return new CCU825OutState(rp.getPacketPayload()).getOutBits();
	}
	
	/**
	 * Request actual outputs state. 
	 * @return actual outputs state from device
	 * @throws CCU825ProtocolException
	 */
	public int getOutState(  ) throws CCU825ProtocolException
	{
		return setOutState( 0, 0 ); // Mask of zeros = modify none
	}

	/**
	 * Set or reset one output.
	 * 
	 * @param nOutBit Output to change, 0-6
	 * @param state new state
	 * @throws CCU825ProtocolException
	 */
	public void setOutState(int nOutBit, boolean state) throws CCU825ProtocolException 
	{
		int mask = (1 << nOutBit);
		int bits = 0;
		
		if( state )
			bits = mask;
	
		setOutState(bits, mask);
	}

	/**
	 * If encryption is currently enabled.
	 * @return True if we do encryption.
	 */
	public boolean isEncryptionEnabled() {
		return encryptionEnabled;
	}

	/**
	 * Enable or disable encryption. You better don't. Protocol driver does it by itself.
	 * Can be possibly needed to restart handshake. 
	 * 
	 * @param encryptionEnabled True to enable.
	 */
	private void setEncryptionEnabled(boolean encryptionEnabled) {
		this.encryptionEnabled = encryptionEnabled;
	}

	/* who needs
	public boolean isDataDumpEnabled() {
		return dataDumpEnabled;
	}*/

	/**
	 * Dump or not all I/O.
	 * 
	 * @param dataDumpEnabled True to enable packet data logging.
	 */
	public void setDataDumpEnabled(boolean dataDumpEnabled) {
		this.dataDumpEnabled = dataDumpEnabled;
	}

}
