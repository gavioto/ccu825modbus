package ru.dz.ccu825.transport;

public interface ModBusConnection {
		public void setSpeed( int baud );
		
		public void connect();
		public void disconnect();
		
		
		/**
		 * fn23 r/w multiple
		 * 
		 * @param readStartAddress start register to read from
		 * @param nRead number fo 16 bit registers to read
		 * @param writeStartAddress start register to write to
		 * @param nWrite number fo 16 bit registers to write
		 * @param writeBytes multiple of 2
		 * @param writeData data to write
		 * @return bytes read 
		 */
		
		public byte [] rwMultiple( int readStartAddress, int nRead, int writeStartAddress, int nWrite, int writeBytes, byte [] writeData );
		
}
