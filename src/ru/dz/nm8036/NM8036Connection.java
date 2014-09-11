package ru.dz.nm8036;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Connect to MasterKit 8038 board RS232 through TCP/IP protocol convertor.
 * <p>
 * <li>Read temperature sensor data.
 * <li>Read ADC values.
 * <li>Set DO state.
 * <p>
 * 
 * @author dz
 *
 */
public class NM8036Connection 
{
	private static final int MAX_SENSORS = 128;
	
	private static final int N_AI = 4;
	private static final int N_DO = 12;
	
	private String hostName;
	private int port;

	private InputStream is;
	private OutputStream os;
	private Socket clientSocket;
	
	public String getHostName() {		return hostName;	}
	public void setHostName(String hostName) {		this.hostName = hostName;	}
	public int getPort() {		return port;	}
	public void setPort(int port) {		this.port = port;	}
	
	public void connect() throws UnknownHostException, IOException
	{

		  clientSocket = new Socket(hostName, port);
		  
		  clientSocket.setSoTimeout(50); // Wait just 50 msec if no data
		  
		  os = clientSocket.getOutputStream();
		  is = clientSocket.getInputStream();
	}

	private void sendByte(byte c) throws IOException {
		os.write(c);
	}

	private int readByte() throws IOException {
		int b = is.read();
		
		if( b == -1 ) throw new IOException();
		
		return b;
	}

	private int readShort() throws IOException {
		
		int hi = readByte();
		int lo = readByte();
		
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
	
	public double[] readtemperatureSensors() throws IOException
	{
		drainInput();
		sendByte((byte)'t');
		
		int nSensors = readByte();
		
		if( nSensors > MAX_SENSORS )
		{
			throw new ArrayIndexOutOfBoundsException("too many sensors: "+nSensors);
		}
		
		double[] out = new double[nSensors];
		
		for( int i = 0; i < nSensors; i++ )
		{
			out[i] = readTemperatureValue();
		}
		
		
		return out;
	}
	
	private double readTemperatureValue() throws IOException 
	{
		// Convert to short to make 16-bit value signed
		short shortValue = (short)readShort();
		
		return shortValue/100.0;
	}
	
	
	/**
	 * Read 4 analog inputs
	 * @return binary data from ADC
	 * @throws IOException
	 */
	public int[] readAinputs() throws IOException
	{
		drainInput();
		sendByte((byte)'s');
		
		expectByte(((byte)'s'));
		
		int[] out = new int[N_AI];
		
		for( int i = 0; i < N_AI; i++ )
		{
			out[i] = readAinValue();
		}
		
		
		return out;
	}
	
	private void expectByte(byte expect) throws IOException 
	{
		int echo = readByte();
		if( (echo & 0xFF) != expect)
			throw new IOException("no 's' echo");
	}
		
	private int readAinValue() throws IOException
	{
		return readShort();
	}

	/**
	 * PWM values for output, 0-100
	 * @param percentages int[12]
	 * @throws IOException
	 * @throws ArrayIndexOutOfBoundsException if percentage not in 0-100 range 
	 */
	public void setPwmValues(int[] percentages) throws IOException
	{
		drainInput();
		sendByte((byte)'a');
		expectByte((byte)'a');

		for( int i = 0; i < N_DO; i++ )
		{
			int p = percentages[i];
			if( (p < 0) || (p > 100) )
				throw new ArrayIndexOutOfBoundsException(p);
		}
		
		for( int i = 0; i < N_DO; i++ )
			sendByte((byte)percentages[i]);
		
		for( int i = 0; i < N_DO; i++ )
			sendByte( (byte)(100-percentages[i]) );
		
	}
	
}
