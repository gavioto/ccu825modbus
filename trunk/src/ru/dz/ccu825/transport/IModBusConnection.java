package ru.dz.ccu825.transport;

import ru.dz.ccu825.util.CCU825Exception;
import ru.dz.ccu825.util.CCU825ProtocolException;

/**
 * 
 * ModBus connection interface, as needed by CCU825 protocol.
 * <p>
 * Provide your implementation or use sample one, see subclasses. 
 * <p>
 * Just one function, interface is simplified to minimum.
 * <p>
 *   
 * @author dz
 *
 */

public interface IModBusConnection {
	
	/**
	 * Set connector-specific target address, such as serial
	 * RS485 device port, TCP/IP address or so. 
	 * <p>
	 * For j2mod:
	 * <li>"device:com2" - serial port on Windows
	 * <li>"tcp:localhost:502" - modbus/tcp server on localhost port 502 
	 * 
	 * @param dest Connector-specific address to connect to.
	 */
	public void setDestination(String dest);
	
	/**
	 * Called before connect() to set serial port parameters.
	 * @param baud always 9600
	 */
	public void setSpeed( int baud );

	/**
	 * ModBus bus unit address. First field of a modbus packet.
	 * <p>
	 * You can find/set one in CCU825-SM configuration software on
	 * the 'common connection parameters' page, in 'modbus device
	 * address' field. Usually = 1.
	 * 
	 * @param unit Unit id - see CCU825 configurator's parameters.
	 */
	public void setModbusUnitId( int unit );
	
	/**
	 * Must be called before any attempt to use rwMultiple.
	 * 
	 * @throws CCU825Exception if connection fails.
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
