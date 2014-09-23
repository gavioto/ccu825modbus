package ru.dz.mercury;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import ru.dz.ccu825.CCU825Test;
import ru.dz.mercury.data.MercuryActivePower;
import ru.dz.mercury.data.MercuryEnergy;
import ru.dz.mercury.data.MercuryFixed;
import ru.dz.mercury.data.MercuryFreq;
import ru.dz.mercury.data.MercuryIV;
import ru.dz.mercury.pkt.ChannelOpenPacket;
import ru.dz.mercury.pkt.ChannelTestPacket;
import ru.dz.mercury.pkt.Packet;
import ru.dz.mercury.pkt.ParameterReadRequestPacket;


/**
 * Connect to Mercury 230 AC power meter RS485 port through TCP/IP protocol convertor.
 * <p>
 * <li>TODO Read power measure results as displayed on meter indicator.
 * <li>TODO Read instant/max I/V/Freq, etc.
 * <p>
 * 
 * @author dz
 *
 */
public class Mercury230Connection 
{
	private boolean dumpPacketData = false;

	private static final int MAX_PKT_LEN = 512; // Why? max payload 256?

	/** Power metering data contain special info in hight bits. */
	private static final byte P_MASK = 0x3F;  

	private String hostName;
	private int port;

	private InputStream is;
	private OutputStream os;
	private Socket clientSocket;
	private byte netAddress = 0; // broadcast

	public String getHostName() {		return hostName;	}
	public void setHostName(String hostName) {		this.hostName = hostName;	}
	public int getPort() {		return port;	}
	public void setPort(int port) {		this.port = port;	}

	public void connect() throws UnknownHostException, IOException
	{

		clientSocket = new Socket(hostName, port);

		//clientSocket.setSoTimeout(getNextPktTimeout()); // Wait this long if no data
		clientSocket.setSoTimeout(500); // Wait this long if no data

		os = clientSocket.getOutputStream();
		is = clientSocket.getInputStream();
	}

	private void sendByte(byte c) throws IOException {
		os.write(c);
	}

	private void sendShort(int s) throws IOException {
		os.write((byte) (s & 0xFF));
		os.write((byte) ((s>>8) & 0xFF));
	}

	private int readByte() throws IOException {
		int b = is.read();

		if( b == -1 ) throw new IOException();

		return b;
	}

	private int readShort() throws IOException {

		int lo = readByte();
		int hi = readByte();

		int shortValue = (hi & 0xFF) << 8;
		shortValue |= (lo & 0xFF);

		return shortValue;
	}


	private void drainInput() throws IOException {
		while( is.available() > 0 )
			readByte();		
	}


	public void disconnect() throws IOException
	{
		clientSocket.close();
	}

	// ---------------------------------------------------------------------------
	//
	// ---------------------------------------------------------------------------

	/**
	 * Timeout at the end of packet
	 * @return time, msec
	 */
	private int getSendPktEndTimeout() {
		// For 9600
		return 5; 
	}

	/**
	 * Timeout before sending next packet
	 * @return time, msec
	 */
	private int getNextPktTimeout() {
		// For 9600
		return 150; 
	}


	public int getNetAddress() {
		return ((int)netAddress) & 0xFF;
	}
	public void setNetAddress(byte netAddress) {
		this.netAddress = netAddress;
	}
	public Packet readPacket() throws Mercury230CRCException, IOException, Mercury230ProtocolTimeoutException
	{
		int pos = 0;
		byte[] ans = new byte[MAX_PKT_LEN];

		try{

			while( is.available() > 0 )
			{
				ans[pos++] = (byte) readByte();
			}

		} catch(java.net.SocketTimeoutException t)
		{
			// It's ok, timeout means we have a packet
		}

		if( pos == 0 )
			throw new Mercury230ProtocolTimeoutException();
		//throw new Mercury230UnexpectedPacketException(ans, "zero length packet")

		byte [] reply = new byte[pos];
		System.arraycopy(ans, 0, reply, 0, pos);

		if(dumpPacketData) CCU825Test.dumpBytes("got pklt", reply);

		Packet p = new Packet(reply); 

		return p;
	}


	public Packet readNonRcPacket() throws Mercury230ProtocolException, IOException
	{
		Packet pkt = readPacket();
		
		if(pkt.isReturnCodePacket())
			throw new Mercury230ProtocolException("Got rc="+pkt.getReturnCode());
		
		return pkt;
	}

	int readRetCodePacket() throws Mercury230UnexpectedPacketException, Mercury230CRCException, IOException, Mercury230ProtocolTimeoutException
	{
		Packet p = readPacket();
		if(!p.isReturnCodePacket())
			throw new Mercury230UnexpectedPacketException(p,"Expected result code packet");

		return p.getReturnCode();
	}


	
	public void sendPacked(Packet p) throws IOException
	{

		byte[] toSend = p.getPacketBytes();

		if(dumpPacketData) CCU825Test.dumpBytes("send pkt", toSend);

		for( byte b : toSend )
			sendByte(b);

		sleep(getSendPktEndTimeout());
		sleep(getNextPktTimeout());
	}








	/*
	void sendLogReadRequestPacket(int nParam, int nRecord) throws IOException
	{
		if(nParam == 0)
		{
			byte[] payload = new byte[1];
			payload[0] = 0;
			sendPacked(PKT_TYPE_READ_RECORD, payload);
			return;
		}

		byte[] payload = new byte[2];

		payload[0] = (byte)nParam;
		payload[1] = (byte)nRecord;

		sendPacked(PKT_TYPE_CHANNEL_OPEN, payload);

	}
	 */

	void sendParameterReadRequestPacket(int nParam) throws IOException
	{
		sendPacked(new ParameterReadRequestPacket(netAddress, nParam));		
	}

	public void sendParameterReadRequestPacket(int nParam, int subParam) throws IOException
	{
		sendPacked(new ParameterReadRequestPacket(netAddress, nParam, subParam));		
	}

	// ---------------------------------------------------------------------------
	// Read meter general parameters
	// ---------------------------------------------------------------------------

	int readDeviceAddress() throws IOException, Mercury230ProtocolException
	{
		sendParameterReadRequestPacket(5);
		byte[] payload = readNonRcPacket().getPayload();
		return payload[1];
	}


	// ---------------------------------------------------------------------------
	//
	// ---------------------------------------------------------------------------

	private void ping()
	{
		try {
			System.out.print("Ping... ");

			sendPacked(new ChannelTestPacket(netAddress));
			int retCode = readRetCodePacket();

			System.out.println("rc = "+retCode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Mercury230UnexpectedPacketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Mercury230CRCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Mercury230ProtocolTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	int openChannel(int level, String passwd) throws Mercury230ProtocolException
	{
		try {
			sendPacked(new ChannelOpenPacket(netAddress, level, passwd));
			return readRetCodePacket();
		} catch (IOException e) {
			throw new Mercury230ProtocolException(e);		
		}
	}

	void sleep(int mSec)
	{
		synchronized (this) {


			try {
				this.wait(mSec);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws UnknownHostException, IOException, Mercury230ProtocolException {
		Mercury230Connection c = new Mercury230Connection();

		if(false)
		{
			c.setHostName("etherwan.");
			c.setPort(604);
		} else {
			c.setHostName("moxa.");
			c.setPort(4002);
		}
		c.connect();

		c.ping();
		c.openChannel(1, "\1\1\1\1\1\1");

		int addr = c.readDeviceAddress();
		System.out.println("Device address = "+addr);

		c.getInstantPowerValues();

		//while(true)			c.ping();

	}

	private void getInstantPowerValues() throws Mercury230CRCException, Mercury230ProtocolTimeoutException 
	{
		byte[] packet;

		try {
			//double [] v;
			//double [] i;
			double [] angle;

			/*
			sendParameterReadRequestPacket(0x16, 0x11);
			v = read3dPacket();
			System.out.println("V = "+v[0]+" "+v[1]+" "+v[2]+" ");

			sendParameterReadRequestPacket(0x16, 0x21);
			i = read3dPacket();
			System.out.println("I = "+i[0]+" "+i[1]+" "+i[2]+" ");
			*/
			
			MercuryIV iv = new MercuryIV(this);
			iv.dump();
			
			(new MercuryFreq(this)).dump();

			/* ��������������� �������. ��, ��� ���.

			sendparameterReadRequestPacket(0x14, 0xF0);
			packet = readPacket().getPayload();
			CCU825Test.dumpBytes("Energy", packet);

			sendparameterReadRequestPacket(0x14, 0xF1);
			packet = readPacket().getPayload();
			CCU825Test.dumpBytes("Energy T1", packet);

			sendparameterReadRequestPacket(0x14, 0xF2);
			packet = readPacket().getPayload();
			CCU825Test.dumpBytes("Energy T2", packet);

			sendparameterReadRequestPacket(0x14, 0xF3);
			packet = readPacket().getPayload();
			CCU825Test.dumpBytes("Energy T3", packet);

			sendparameterReadRequestPacket(0x14, 0xF4);
			packet = readPacket().getPayload();
			CCU825Test.dumpBytes("Energy T4", packet);
			 */			

			/*
			// Active power
			sendParameterReadRequestPacket(0x16, 0x00);
			double[] p = read4dPacket();
			System.out.println("P = "+p[0]+" "+p[1]+" "+p[2]+" "+p[3]+" (active)");

			// Reactive power
			sendParameterReadRequestPacket(0x16, 0x04);
			double[] q = read4dPacket();
			System.out.println("Q = "+q[0]+" "+q[1]+" "+q[2]+" "+q[3]+" (reactive)");

			// Full (P+Q) power
			sendParameterReadRequestPacket(0x16, 0x08);
			double[] s = read4dPacket();
			System.out.println("S = "+s[0]+" "+s[1]+" "+s[2]+" "+s[3]+" (full)");
			*/
			
			//MercuryPower power = new MercuryPower(this);
			MercuryActivePower power = new MercuryActivePower(this);
			power.dump();
			
			/*
			sendPacked(new EnergyReadRequestPacket(netAddress,0,6));
			packet = readPacket().getPayload();
			double[] e = new double[4];

			MercuryFixed.decode4x4(packet, 16*0, e);
			System.out.println("E = "+e[0]+" \t"+e[1]+" \t"+e[2]+" \t"+e[3]+" \t(T1)");

			MercuryFixed.decode4x4(packet, 16*1, e);
			System.out.println("E = "+e[0]+" \t"+e[1]+" \t"+e[2]+" \t"+e[3]+" \t(T2)");

			MercuryFixed.decode4x4(packet, 16*2, e);
			System.out.println("E = "+e[0]+" \t"+e[1]+" \t"+e[2]+" \t"+e[3]+" \t(T3)");

			MercuryFixed.decode4x4(packet, 16*3, e);
			System.out.println("E = "+e[0]+" \t"+e[1]+" \t"+e[2]+" \t"+e[3]+" \t(T4)");

			MercuryFixed.decode4x4(packet, 16*4, e);
			System.out.println("E = "+e[0]+" \t"+e[1]+" \t"+e[2]+" \t"+e[3]+" \t(Total)");

			MercuryFixed.decode4x4(packet, 16*5, e);
			System.out.println("E = "+e[0]+" \t"+e[1]+" \t"+e[2]+" \t"+e[3]+" \t(Loss)");
			 */

			MercuryEnergy energy = new MercuryEnergy(this);
			energy.dumpForwardActive();



		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Current
		catch (Mercury230ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	/**
	 * Read and decode typical I/V/etc reply with 3 fixed numbers 3 bytes each.
	 * <p>
	 * 
	 * 
	 * @throws IOException
	 * @throws Mercury230ProtocolException 
	 */
	public double[] read3dPacket() throws IOException, Mercury230ProtocolException {
		double[] v = new double[3];
		byte[] packet = readNonRcPacket().getPayload();
		MercuryFixed.decode3x3(packet, v);
		return v;
	}

	/**
	 * Read and decode typical power reply with 4 fixed numbers 3 bytes each.
	 * <p>
	 * NB! We do clear power specific high bits in 1st bytes of 4 numbers 
	 * 
	 * 
	 * @throws IOException
	 * @throws Mercury230ProtocolException 
	 */
	public double[] read4dPacket() throws IOException, Mercury230ProtocolException {
		double[] v = new double[4];
		byte[] packet = readNonRcPacket().getPayload();


		packet[0] &= P_MASK;
		packet[3] &= P_MASK;
		packet[6] &= P_MASK;
		packet[9] &= P_MASK;

		v[0] = MercuryFixed.decode3b(packet,0);
		v[1] = MercuryFixed.decode3b(packet,3);
		v[2] = MercuryFixed.decode3b(packet,6);
		v[3] = MercuryFixed.decode3b(packet,9);

		return v;
	}



}
