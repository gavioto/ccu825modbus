package ru.dz.ccu825;

public class CCU825Connection {

	private static final int NTRIES = 5;
	
	private ModBusConnection mc;
	private byte[] key;

	private int currentSeq = 0;
	private int currentAck = 0;

	private int lastRecvSeq;
	
	public CCU825Connection( ModBusConnection mc, byte [] key  ) throws CCU825Exception {
		
		this.mc = mc;
		this.key = key;
		
		setupModBus();
		mc.connect();
		
		int protocolRC = initProtocol();
		System.out.print("RC = " + protocolRC ); // TODO move out
		
	}

		private void setupModBus() {
		mc.setSpeed(9600);		
	}
	

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

	
	private int initProtocol() throws CCU825Exception {
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
				
				byte[] rdata = rp.getPacketBytes();
				
				if( rdata[0] != CCU825Packet.PKT_TYPE_DEVICEINFO )
					logErr("wrong packet type" + rdata[0] );
				
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
	
		return protocolRC;		
	}

	
	
	private void logErr(String string) {
		System.err.println("Error: "+ string );		
	}

	private void logProtoErr(CCU825ProtocolException ex) {
		System.err.println("Protocol err: "+ex.getMessage());		
	}

	
	
	
	public void getSysInfo() {
		// TODO Auto-generated method stub
		
	}


	
}
