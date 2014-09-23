package ru.dz.nm8036;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
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
		  
		  //clientSocket.setSoTimeout(50); // Wait just 50 msec if no data
		  //clientSocket.setSoTimeout(500); // Wait just 500 msec if no data
		  clientSocket.setSoTimeout(2000); // Wait just 500 msec if no data
		  
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
	
	/**
	 * Request data from temperature sensors. 
	 * @return Array of sensor values, in Celsius degrees
	 * @throws IOException
	 */
	public double[] readTemperatureSensors() throws IOException
	{
		drainInput();
		sendByte((byte)'t');
		
		int nSensors = ((int)readByte()) & 0xFF;
		
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
	public int[] readAnalogInputs() throws IOException
	{
		drainInput();
		sendByte((byte)'s');
		
		expectByte(((byte)'s'));
		
		int[] out = new int[N_AI];
		
		for( int i = 0; i < N_AI; i++ )
		{
			out[i] = readShort();
		}
		
		
		return out;
	}
	
	private void expectByte(byte expect) throws IOException 
	{
		int echo = readByte();
		if( (echo & 0xFF) != expect)
			throw new IOException("no 's' echo");
	}
		
	

	/**
	 * PWM values for output, 0-100.
	 * <[p>
	 * Doesn't work in FW 1.8
	 * 
	 * @param percentages int[12]
	 * @throws IOException
	 * @throws ArrayIndexOutOfBoundsException if percentage not in 0-100 range 
	 */
	public void setPwmValues(int[] percentages) throws IOException
	{
		//int len = Math.min(N_DO, percentages.length);
		int len = N_DO;
		
		if( percentages.length != N_DO )
			throw new ArrayIndexOutOfBoundsException(percentages.length);
		
		for( int i = 0; i < len; i++ )
		{
			int p = percentages[i];
			if( (p <= 0) || (p > 100) )
				throw new ArrayIndexOutOfBoundsException(p);
		}
		
		drainInput();
		sendByte((byte)'A');
		expectByte((byte)'A');

		for( int i = 0; i < N_DO; i++ )
			//sendByte((byte)percentages[i]);
			sendShort(percentages[i]);
		
		for( int i = 0; i < N_DO; i++ )
			//sendByte((byte)(100-percentages[i]));
			sendShort(101-percentages[i]);

		try {
		readByte(); // Poor man's delay
		} catch(SocketTimeoutException e)
		{
			
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		NM8036Connection c = new NM8036Connection();
		
		c.setHostName("moxa.");
		c.setPort(4002);
		
		c.connect();

		
		//int[] percentages = new int[4] = { 100, 75, 25, 50 };
		int[] percentages = 
			{ 
				100, 75, 25, 50, 
				100, 75, 25, 50, 
				100, 75, 25, 50 
			};
		c.setPwmValues(percentages );
		
		
		double[] sensors = c.readTemperatureSensors();
		
		int i = 0;
		for(double s : sensors)
		{
			System.out.print("temp "+ i++ +" = "+s+"; \t");
			if( (i%4) == 0 )
				System.out.println();

		}
		//System.out.println();
		
		int[] analogInputs = c.readAnalogInputs();

		i = 0;
		for(int in : analogInputs)
		{
			double din = in;
					
			din *= 5;
			din /= 1024;
			//din /= 512;
					
			System.out.print("in "+ i++ +" = "+din+"; \t");
			if( (i%4) == 0 )
				System.out.println();

		}		
		System.out.println();

		
		
	}
}
