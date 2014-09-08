package ru.dz.ccu825;

import ru.dz.ccu825.payload.CCU825DeviceInfo;
import ru.dz.ccu825.payload.CCU825ReturnCode;
import ru.dz.ccu825.payload.CCU825SysInfo;
import ru.dz.ccu825.pkt.CCU825DeviceInfoAckPacket;
import ru.dz.ccu825.pkt.CCU825DeviceInfoReqPacket;
import ru.dz.ccu825.pkt.CCU825EmptyPacket;
import ru.dz.ccu825.pkt.CCU825SysInfoReqPacket;
import ru.dz.ccu825.transport.ModBusConnection;
import ru.dz.ccu825.util.CCU825CheckSumException;
import ru.dz.ccu825.util.CCU825Exception;
import ru.dz.ccu825.util.CCU825PacketFormatException;
import ru.dz.ccu825.util.CCU825ProtocolException;
import ru.dz.ccu825.util.RC4;

/**
 * 
 * @author dz
 *
 * Connection to an instance of CCU825 device. Implements device-specific protocol over the (implemented elsewhere) modbus.
 * 
 * Conforms CCU825-SM protocol spec. from 5 September 2014, firmware ver. 01.02.
 * 
 * Needs an external modbus io driver to work.
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

	/**
	 * 
	 * @param mc ModbBus connection implementation, used just for fn23 io
	 * @param key communications encryption key. Contact radsel to get one. Have your device IMEI handy.
	 * @throws CCU825Exception Mostly due to communication or protocol errors.
	 */

	public CCU825Connection( ModBusConnection mc, byte [] key  ) {
		this.mc = mc;
		this.key = key;
	}

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
	 * 
	 * @throws CCU825CheckSumException Received packet had checksum error
	 * @throws CCU825PacketFormatException Received packet was broken somehow else
	 */
	
	private CCU825Packet exchange( CCU825Packet send ) throws CCU825CheckSumException, CCU825PacketFormatException
	{
		send.setSeqNum( currentSeq++ );
		//send.setAckNum( currentAck );
		send.setAckNum( lastRecvSeq );


		byte [] packetBytes = send.getPacketBytes();

		int writeBytes = packetBytes.length;

		assert( (writeBytes & 1) == 0 );

		RC4 enc = new RC4(key);
		byte[] spd = enc.encrypt(packetBytes);

		CCU825Test.dumpBytes( "modbus send", spd );
		byte[] rcv = mc.rwMultiple(0, CCU825Packet.MAXPACKET, 0, writeBytes/2, writeBytes, spd);
		CCU825Test.dumpBytes( "modbus recv", rcv );


		RC4 dec = new RC4(key);
		byte[] rpd = dec.decrypt(rcv);

		CCU825Packet rp = new CCU825Packet(rpd);

		int recvAck = rp.getAckNum();
		lastRecvSeq = rp.getSeqNum();

		// It seems to be a "can't happen" thing in modbus-based proto, as modbus io is syncronous, but...

		if( recvAck != currentSeq-1 )
		{
			//logErr("our seq = " + (currentSeq-1) + " recv ack = " + recvAck);
			throw new CCU825PacketFormatException("our seq = " + (currentSeq-1) + " recv ack = " + recvAck);
		}

		currentAck++;

		if( currentAck != lastRecvSeq )
			logErr("our ack = " + (currentAck) + " recv seq = " + lastRecvSeq);

		return rp;
	}

	/**
	 * Do an initial protocol transactions as defined in CCU825-SM protocol spec. from 5 September 2014, firmware ver. 01.02
	 * @return device return code
	 * @throws CCU825Exception
	 */

	private CCU825ReturnCode initProtocol() throws CCU825Exception {
		int tries;

		// 1. send/get syn

		for( tries = NTRIES; tries > 0; tries-- )
		{

			try {
				CCU825EmptyPacket sp = new CCU825EmptyPacket();

				sp.setSyn( true );

				CCU825Packet rp = exchange( sp ); // TODO catch timeout

				byte[] rdata = rp.getPacketBytes();

				if( rdata[0] != CCU825Packet.PKT_TYPE_EMPTY )
					logErr("wrong packet type" + rdata[0] );

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

				/*
				byte[] rdata = rp.getPacketBytes();

				if( rdata[0] != CCU825Packet.PKT_TYPE_DEVICEINFO )
					logErr("wrong packet type" + rdata[0] );

				*/
				
				deviceInfo = new CCU825DeviceInfo(rp.getPacketPayload());

				// TODO store devinfo 
				// TODO process/print devinfo here

			} catch( CCU825ProtocolException ex )
			{
				logProtoErr( ex );
			}

		}

		if( tries == 0 ) throw new CCU825Exception("Can't get device info");

		// 3. Ack devinfo and get return code

		int protocolRC = 0;

		for( tries = NTRIES; tries > 0; tries-- )
		{

			try {
				CCU825Packet rp = exchange(new CCU825DeviceInfoAckPacket() );

				byte[] rdata = rp.getPacketBytes();

				if( rdata[0] != CCU825Packet.PKT_TYPE_RETCODE )
					logErr("wrong packet type" + rdata[0] );

				protocolRC = rdata[1];


			} catch( CCU825ProtocolException ex )
			{
				logProtoErr( ex );
			}

		}

		if( tries == 0 ) throw new CCU825Exception("Can't ack device info");

		return new CCU825ReturnCode( protocolRC );		
	}



	private void logErr(String string) {
		System.err.println("Error: "+ string );		
	}

	private void logProtoErr(CCU825ProtocolException ex) {
		System.err.println("Protocol err: "+ex.getMessage());		
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
	 * 
	 * @return device info as we got in protocol init transaction
	 */
	public CCU825DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}



}
