package ru.dz.ccu825.transport;

import java.util.HashMap;
import java.util.Map;

public class ArrayKeyRing implements ICCU825KeyRing 
{

	private Map<String,byte[]> keys = new HashMap<String,byte[]>();
	
	{
		//keys.put("869158007853514", new byte[] = { 0, 0, 0} );
		addKey( "869158007853514", "2A70672E2233240901555A32695D4300");
		
	}
	
	@Override
	public byte[] getKeyForIMEI(String IMEI) {
		return keys.get(IMEI);
	}

	public void addKey( String IMEI, String key )
	{
		byte[] bkey = javax.xml.bind.DatatypeConverter.parseHexBinary(key);
		keys.put( IMEI, bkey );
	}

}
