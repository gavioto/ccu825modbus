package ru.dz.ccu825.transport;

import ru.dz.ccu825.util.CCU825Exception;
import ru.dz.ccu825.util.CCU825ProtocolException;

/**
 * Modbus connection interface, as needed by CCU825 protocol. 
 * Just one function, interface is simplified to minimum. 
 * @author dz
 *
 */

public interface ModBusConnection {
		public void setSpeed( int baud );
		
		public void connect() throws CCU825Exception;
		public void disconnect();
		
		
		/**
		 * fn23 r/w multiple interface, reduced to what CU825 protocol needs. 
		 * Write count is taken from writeData size. 
		 * 
		 * @param nRead number of 16 bit registers to read
		 * @param writeData data to write
		 * @return bytes read 
		 * @throws CCU825Exception 
		 */
		
		public byte [] rwMultiple( int nRead, byte [] writeData ) throws CCU825ProtocolException;
		
}
