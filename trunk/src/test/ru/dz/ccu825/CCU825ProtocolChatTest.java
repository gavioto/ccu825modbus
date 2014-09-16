package test.ru.dz.ccu825;

import static org.junit.Assert.fail;

import org.junit.Test;

import ru.dz.ccu825.CCU825Connection;
import ru.dz.ccu825.data.CCU825ReturnCode;
import ru.dz.ccu825.transport.ConstantKeyRing;
import ru.dz.ccu825.transport.ICCU825KeyRing;
import ru.dz.ccu825.transport.TestChatModbusConnector;
import ru.dz.ccu825.util.CCU825Exception;

/**
 * 
 * CCU825 driver main class for debugging and test purposes.
 * 
 * @author dz
 *
 */
public class CCU825ProtocolChatTest 
{
	//private final static Logger log = Logger.getLogger(CCU825ProtocolChatTest.class.getName());

	@Test
	public void testCCU825ProtocolChat() {				
		
		TestChatModbusConnector mc = new TestChatModbusConnector();
		
		ICCU825KeyRing kr = new ConstantKeyRing(TestChatModbusConnector.key);
		
		CCU825Connection c = new CCU825Connection(mc, kr);
		
		try {
		
			CCU825ReturnCode protocolRC = c.connect();
			
			//System.out.println("RC = " + protocolRC );			
			//System.out.println( c.getDeviceInfo() );
			
			//System.out.println( c.getSysInfo() );
			
		} catch (CCU825Exception e) {
			//e.printStackTrace();
			fail(e.getMessage());
			//System.exit(33);
		}

		
		//System.out.println(c.getDeviceInfo());
	
		/*
		for( int i = 20; i > 0; i-- )
		{
			try {
				CCU825SysInfo si = c.getSysInfo();
				oh.sendSysInfo(si);
				System.out.println(si);
			} catch (CCU825ProtocolException e) {
				//e.printStackTrace();
				log.severe(e.getMessage());
			}			
		}
		*/
	}



	

}
