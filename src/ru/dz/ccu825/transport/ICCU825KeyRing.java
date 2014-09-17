package ru.dz.ccu825.transport;

/**
 * 
 * <b>CCU825 protocol key ring interface.</b>
 * <p>
 * During a handshake CCU825 protocol finds out an IMEI of 
 * a controller we talk to. For communications to continue 
 * from that point protocol needs an encryption key for an
 * IMEI. Such a key is requested by protocol engine from a 
 * key ring.
 * <p>
 * Sample memory-backled implementations are provided, but 
 * if you work with a lot of devices, you can connect a
 * file or database keyring container by providing your 
 * implementation.
 * 
 * @author dz
 *
 */

public interface ICCU825KeyRing {

	/**
	 * 
	 * Find a key for a given IMEI.
	 * 
	 * @param IMEI Controller IMEI (unique identifier).
	 * @return Encryption key or <b>null</b> if no key found.
	 */
	
	public byte[] getKeyForIMEI( String IMEI );
	
}
