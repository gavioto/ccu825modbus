package ru.dz.mercury;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import ru.dz.ccu825.CCU825Test;
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


	private Packet readPacket() throws Mercury230CRCException, IOException, Mercury230ProtocolTimeoutException
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


	//private void sendPacked(int requestCode, byte [] payload) throws IOException
	private void sendPacked(Packet p) throws IOException
	{
		/*
		int pLen = payload.length;
		byte[] toSend = new byte[pLen+4];

		toSend[0] = netAddress;
		toSend[1] = (byte) requestCode;

		System.arraycopy(payload, 0, toSend, 2, pLen);

		int crc = calcCRC(toSend,pLen+2);

		toSend[pLen+2] = (byte) (crc & 0xFF);
		toSend[pLen+3] = (byte) ((crc>>8) & 0xFF);
		*/
		
		byte[] toSend = p.getPacketBytes();
		
		if(dumpPacketData) CCU825Test.dumpBytes("send pkt", toSend);

		for( byte b : toSend )
			sendByte(b);

		sleep(getSendPktEndTimeout());
		sleep(getNextPktTimeout());
	}



	int readRetCodePacket() throws Mercury230UnexpectedPacketException, Mercury230CRCException, IOException, Mercury230ProtocolTimeoutException
	{
		byte[] packet = readPacket().getPayload();
		if(packet.length != 1)
			throw new Mercury230UnexpectedPacketException(packet,"Expected result code packet");

		return packet[0] & 0xF;
	}





	void sendChannelOpenPacket(int level, String passwd) throws Mercury230ProtocolException, IOException
	{
		/*
		if(passwd.length() > MAX_PASSWD_LEN)
			throw new Mercury230ProtocolException("password too long");

		byte[] payload = new byte[7];

		payload[0] = (byte)level;

		// TODO convert to one byte string! Russian letters won't work
		for(int i = 0; i < MAX_PASSWD_LEN; i++)
		{
			payload[i+1] = (byte) passwd.charAt(i); 
		}
		*/
		sendPacked(new ChannelOpenPacket(netAddress, level, passwd));

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

	void sendparameterReadRequestPacket(int nParam) throws IOException
	{
		/*
		byte[] payload = new byte[1];
		payload[0] = (byte) nParam;
		*/
		sendPacked(new ParameterReadRequestPacket(netAddress, nParam));		
	}

	void sendparameterReadRequestPacket(int nParam, int subParam) throws IOException
	{
		/*
		byte[] payload = new byte[2];
		payload[0] = (byte) nParam;
		payload[1] = (byte) subParam;
		sendPacked(PKT_TYPE_READ_PARAMETER, payload);
		*/		
		sendPacked(new ParameterReadRequestPacket(netAddress, nParam, subParam));		
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
			sendChannelOpenPacket(level, passwd);
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

		c.openChannel(1, "\1\1\1\1\1\1");
		
		c.ping();

		c.getInstantPowerValues();

		//while(true)			c.ping();

	}

	private void getInstantPowerValues() throws Mercury230CRCException, Mercury230ProtocolTimeoutException 
	{
		byte[] packet;

		try {
			double [] v;
			double [] i;
			double [] angle;
			
			sendparameterReadRequestPacket(0x16, 0x11);
			v = read3dPacket();
			System.out.println("V = "+v[0]+" "+v[1]+" "+v[2]+" ");
			
			sendparameterReadRequestPacket(0x16, 0x21);
			i = read3dPacket();
			System.out.println("I = "+i[0]+" "+i[1]+" "+i[2]+" ");

			sendparameterReadRequestPacket(0x16, 0x51);
			angle = read3dPacket();
			System.out.println("Angle = "+angle[0]+" "+angle[1]+" "+angle[2]+" ");

			sendparameterReadRequestPacket(0x16, 0x40);
			packet = readPacket().getPayload();
			double freq = decode3b(packet,0);
			System.out.println("Freq = "+freq+" hz");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Current


	}
	
	/**
	 * Read and decode typical I/V/etc reply with 3 fixed numbers 3 bytes each.
	 * <p>
	 * 
	 * @param i where to put result to
	 * 
	 * @throws Mercury230CRCException
	 * @throws IOException
	 * @throws Mercury230ProtocolTimeoutException
	 */
	private double[] read3dPacket() throws Mercury230CRCException,
			IOException, Mercury230ProtocolTimeoutException {
		double[] v = new double[3];
		byte[] packet = readPacket().getPayload();
		decode3x3(packet, v);
		return v;
	}
	
	/**
	 * Typical V/I/etc reply is 3 values 3 byte each
	 * <p>
	 * 
	 * @param packet Packet to decode
	 * @param v array of 3 values to put result to
	 */
	private void decode3x3(byte[] packet, double[] v) {
		v[0] = decode3b(packet,0);
		v[1] = decode3b(packet,3);
		v[2] = decode3b(packet,6);
	}
	
	/**
	 * Decode Mercury's 3-byte fixed number as 3-byte int/100
	 * <p>
	 * 
	 * 
	 * @param packet where to get data from
	 * @param pos start byte position
	 * @return decoded double value
	 */
	private double decode3b(byte[] packet, int pos) {
		int i;
		
		i = ((int)packet[pos+0]) & 0xFF;
		i <<= 8;
		i |= ((int)packet[pos+2]) & 0xFF;
		i <<= 8;
		i |= ((int)packet[pos+1]) & 0xFF;
		
		return i/100.0;
		//return i/256.0;
	}
}
