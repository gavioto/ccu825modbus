/**
 * 
 * Connectors to ModBus implementations. 
 * <p>
 * Contains:
 * <li> j2mod connector
 * <li> Empty connector: discards send, receives zero packets
 * <li> Test chat connector: discards send, receives constant handshake packets.
 * <p>
 * CCU825 uses ModBus in a very specific way. ModBus regsiters are, really, 
 * do not exist on a controller side. Instead, writing to a group of modbus
 * registers transfers an upper level protocol packet to CCU825, and reading
 * gets you a reply packet. Both write and read are done with a single ModBus
 * function called <tt>Read / Write Multiple Registers</tt>. Write part of 
 * request is sending actual packet we have to send, read one trying to read 
 * some number of bytes bigger than any possible packet.
 *  
 * @author dz
 *
 */
package ru.dz.ccu825.transport;