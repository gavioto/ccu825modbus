package ru.dz.ccu825.transport;


/**
 * This keyring is made for test and returns just one key for any request.
 * @author dz
 *
 */
public class ConstantKeyRing implements ICCU825KeyRing 
{

	private byte[] key;
	
	public ConstantKeyRing(byte[] key) {
		this.key = key;
	}
	
	
	@Override
	public byte[] getKeyForIMEI(String IMEI) {
		return key;
	}

	public void addKey( String IMEI, String key )
	{
		System.err.println("Warning: ConstantKeyRing put() called");
	}

}
