package ru.dz.ccu825;

import java.io.IOException;
import java.util.logging.Logger;

import ru.dz.ccu825.data.CCU825ReturnCode;
import ru.dz.ccu825.payload.CCU825SysInfo;
import ru.dz.ccu825.payload.ICCU825Events;
import ru.dz.ccu825.payload.ICCU825SysInfo;
import ru.dz.ccu825.push.PushOpenHAB;
import ru.dz.ccu825.transport.ArrayKeyRing;
import ru.dz.ccu825.transport.CCU825_j2mod_connector;
import ru.dz.ccu825.transport.EmptyModbusConnector;
import ru.dz.ccu825.transport.ICCU825KeyRing;
import ru.dz.ccu825.transport.IModBusConnection;
import ru.dz.ccu825.transport.TestChatModbusConnector;
import ru.dz.ccu825.util.CCU825Exception;
import ru.dz.ccu825.util.CCU825ProtocolException;

/**
 * 
 * CCU825 driver main class for debugging and test purposes.
 * 
 * @author dz
 *
 */
public class CCU825Test 
{
	private final static Logger log = Logger.getLogger(CCU825Test.class.getName());

	static IModBusConnection mc = new CCU825_j2mod_connector();
	static boolean doPoll = true;

	/**
	 * @param args
	 */
	public static void main(String[] args) {				

		//Thread.currentThread().setDaemon(false);
		mc.setDestination("device:com2");
		//mc.setDestination("tcp:192.168.88.145:4002");  // Doesnt work yet

		PushOpenHAB oh = new PushOpenHAB("localhost");
		oh.setDefaultItemNames();

		ICCU825KeyRing kr = new ArrayKeyRing();
		//byte[] key = TestChatModbusConnector.key;
		//byte[] key = kr.getKeyForIMEI("869158007853514"); 
		//dumpBytes("key",key);

		CCU825Connection c = new CCU825Connection(mc, kr);

		AbstractRequestLoop loop = new AbstractRequestLoop(c) {
			
			@Override
			protected void pollDevice(CCU825Connection c) throws CCU825Exception {
				CCU825Test.pollDevice(c);				
			}
			
			@Override
			protected void Say(String string) {
				System.out.println(string);				
			}
		};
		
		loop.startBlocking();
		
		/*
		while(doPoll)
		{
			try {
				connectAndPoll(c);
			} catch (CCU825Exception e) {
				//e.printStackTrace();
				log.severe(e.getMessage());
				//System.exit(33);
			}

			// Reconnect on exception

			c.disconnect();
		}
		*/
		System.exit(0);

	}



	private static void connectAndPoll(CCU825Connection c) throws CCU825Exception 
	{
		System.out.println( "Connecting via "+mc.getDestination() );

		CCU825ReturnCode protocolRC = c.connect();

		System.out.println("RC = " + protocolRC );

		if(!protocolRC.isOk())
		{
			log.severe("Bad connect return code");
			System.exit(33);
		}

		System.out.println( c.getDeviceInfo() );

		//System.out.println( c.getSysInfo() );

		while(doPoll)
			pollDevice(c);
	}



	static int iOut = 0;
	private static void pollDevice(CCU825Connection c) throws CCU825Exception {

		//for( int i = 100; i > 0; i-- )
		{
			
			c.setOutState(iOut++, 0x7F);

			ICCU825SysInfo si;

			ICCU825Events events = c.getEvents();
			if(events != null)
			{
				System.out.println(events);
				si = events.getSysInfo();
			}
			else
			{
				si = c.getSysInfo();
			}
			//oh.sendSysInfo(si);
			System.out.println(si);
			
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
