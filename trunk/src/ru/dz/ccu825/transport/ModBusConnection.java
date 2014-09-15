package ru.dz.ccu825.transport;

import ru.dz.ccu825.CCU825Packet;
import ru.dz.ccu825.util.CCU825Exception;
import ru.dz.ccu825.util.CCU825ProtocolException;

/**
 * ModBus connection interface, as needed by CCU825 protocol.
 * <p> 
 * Just one function, interface is simplified to minimum.
 * <p>
 * TODO rename to IModBusConnection
 *   
 * @author dz
 *
 */

public interface ModBusConnection {
	// RADSEL reads 250 bytes for some reason, we try to read 
	// CCU825Packet.MAXPACKET which is protocol-defined max
	// packet size 
	// public final static int BIGGEST_PACKET_SIZE = 250;
	
	/**
	 * Called before connect() to set serial port parameters.
	 * @param baud always 9600
	 */
	public void setSpeed( int baud );

	/**
	 * Called before any attempt to use rwMultiple.
	 * @throws CCU825Exception
	 */
	public void connect() throws CCU825Exception;
	
	/**
	 * Well, disconnect from transport.
	 */
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
