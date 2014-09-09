package ru.dz.ccu825;

import java.util.logging.Logger;

import ru.dz.ccu825.payload.CCU825ReturnCode;
import ru.dz.ccu825.payload.CCU825SysInfo;
import ru.dz.ccu825.push.PollOpenHAB;
import ru.dz.ccu825.push.PushOpenHAB;
import ru.dz.ccu825.transport.ModBusConnection;
import ru.dz.ccu825.transport.EmptyModbusConnector;
import ru.dz.ccu825.util.CCU825Exception;
import ru.dz.ccu825.util.CCU825ProtocolException;

public class CCU825Test {
	private final static Logger log = Logger.getLogger(CCU825Test.class.getName());


	/**
	 * @param args
	 */
	public static void main(String[] args) {				
		
		ModBusConnection mc = new EmptyModbusConnector();
		PushOpenHAB oh = new PushOpenHAB("localhost");

		oh.setDefaultItemNames();
		
		byte[] key = { 0x00, 0x00, 0x00, 0x00 };
		
		CCU825Connection c = new CCU825Connection(mc, key);
		
		try {
		
			CCU825ReturnCode protocolRC = c.connect();
			System.out.print("RC = " + protocolRC );
			
			System.out.println( c.getDeviceInfo() );
			
			//System.out.println( c.getSysInfo() );
			
		} catch (CCU825Exception e) {
			e.printStackTrace();
			return;
		}

		
		System.out.println(c.getDeviceInfo());
	
		for( int i = 20; i > 0; i-- )
		{
			try {
				CCU825SysInfo si = c.getSysInfo();
				oh.sendSysInfo(si);
				System.out.println(si);
			} catch (CCU825ProtocolException e) {
				e.printStackTrace();
			}			
		}
		
	}



	public static void dumpBytes(String string, byte[] b) 
	{
		if( b==null )
		{
			System.err.println(string + ", null array " );
			return;
		}
		
		System.err.println(string + ", len = " + b.length);
		
		int p = 0;
		
		while( p < b.length )
		{
			if( (p % 16) == 0 )
				System.err.println("");
			
			byte cb = b[p++];
			
			System.err.print( String.format("%02X ", cb) );
		}
		
		
		System.err.println("");
		System.err.println("--");
	}

}
