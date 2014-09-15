package ru.dz.ccu825.push;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import ru.dz.ccu825.CCU825Test;
import ru.dz.ccu825.payload.CCU825SysInfo;
import ru.dz.ccu825.payload.ICCU825SysInfo;

/**
 * Push data to OpenHAB instance with http requests.
 * 
 * TODO REST https://github.com/openhab/openhab/wiki/REST-API
 * 
 * @author dz
 *
 */

public class PushOpenHAB {
	private final static Logger log = Logger.getLogger(PushOpenHAB.class.getName());

	private final String openHABHostName;
	//private String openHABHostName = "localhost";

	private String chargeItemName;

	
	
	
	public PushOpenHAB( String openHABHostName ) {
		this.openHABHostName = openHABHostName;
	}


	private static final Map<Integer,String> items = new HashMap<Integer,String>();

	/**
	 * Map CCU825 input to named OpenHAB item. 
	 * @param input CCU825 input number, 0-15
	 * @param itemName OpehNAB item to translate data to
	 */
	public void setInputItemName( int input, String itemName )
	{
		items.put(input, itemName);
	}


	public void sendSysInfo( ICCU825SysInfo si )
	{
		for( int i = 0; i < CCU825SysInfo.N_IN; i++ )
		{
			String name = items.get(i);
			if( name == null ) continue;

			sendValue( name, Double.toString( si.getInValue()[i] ) );
		}
		
		if(chargeItemName != null) sendValue( chargeItemName, Byte.toString( si.getBatteryPercentage() ) );
		
		sendValue( "CCU825_Device_Temperature", Byte.toString( si.getDeviceTemperature() ) );
		sendValue( "CCU825_Power_Voltage", Double.toString( si.getPowerVoltage() ) );

		if( si.isBalanceValid() ) 
			sendValue( "CCU825_GSM_Balance", Double.toString( si.getGSMBalance() ) );
	}


	private void sendValue(String name, String string) {
		try {
			URL url = makeUrl(name,string);
			callUrl(url);
		} catch(IOException e)
		{
			log.severe(e.getMessage());
			//e.printStackTrace();
		}
	}


	private void callUrl(URL url) throws IOException 
	{
		URLConnection yc = url.openConnection();

		BufferedReader in = new BufferedReader(
				new InputStreamReader(yc.getInputStream()));

		String inputLine;

		while ((inputLine = in.readLine()) != null) 
		{
			log.finest("callUrl="+inputLine);
			//System.out.println(inputLine);
		}
		
		in.close();
	}


	private URL makeUrl(String name, String value) throws MalformedURLException {
		return new URL("http", openHABHostName, 8080, String.format("CMD?%s=%s ", openHABHostName, name, value ) );
		//return new URL( String.format("http://%s:8080/CMD?%s=%s ", openHABHostName, name, value ) );
	}


	public void setDefaultItemNames() 
	{
		for( int i = 0; i < CCU825SysInfo.N_IN; i++ )
		{
			setInputItemName(i, String.format( "CCU825_In%d", i) );
		}
		
		chargeItemName = "CCU825_Battery_Charge";
	}


	public String getOpenHABHostName() {
		return openHABHostName;
	}
	
	
	public String getChargeItemName() {
		return chargeItemName;
	}


	public void setChargeItemName(String chargeItemName) {
		this.chargeItemName = chargeItemName;
	}



}
