package ru.dz.ccu825.transport;

import ru.dz.ccu825.util.CCU825Exception;

public interface ModBusConnection {
		public void setSpeed( int baud );
		
		public void connect() throws CCU825Exception;
		public void disconnect();
		
		
		/**
		 * fn23 r/w multiple
		 * 
		 * @param nRead number of 16 bit registers to read
		 * @param writeData data to write
		 * @return bytes read 
		 * @throws CCU825Exception 
		 */
		
		public byte [] rwMultiple( int nRead, byte [] writeData ) throws CCU825Exception;
		
}
