package ru.dz.ccu825.push;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import ru.dz.ccu825.payload.CCU825SysInfo;

public class PushOpenHAB {

	private String openHABHostName = "localhost";

	public PushOpenHAB(String openHABHostName ) {
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


	public void sendSysInfo( CCU825SysInfo si )
	{
		for( int i = 0; i < CCU825SysInfo.N_IN; i++ )
		{
			String name = items.get(i);
			if( name == null ) continue;

			sendValue( name, Double.toString( si.getInValue()[i] ) );
		}
	}


	private void sendValue(String name, String string) {
		try {
			URL url = makeUrl(name,string);
			callUrl(url);
		} catch(IOException e)
		{
			// TODO log
			e.printStackTrace();
		}
	}


	private void callUrl(URL url) throws IOException {

		URLConnection yc = url.openConnection();


		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						yc.getInputStream()));
		String inputLine;

		// TODO just for test
		while ((inputLine = in.readLine()) != null) 
			System.out.println(inputLine);

		in.close();
	}


	private URL makeUrl(String name, String value) throws MalformedURLException {
		return new URL("http", openHABHostName, 8080, String.format("CMD?%s=%s ", openHABHostName, name, value ) );
		//return new URL( String.format("http://%s:8080/CMD?%s=%s ", openHABHostName, name, value ) );
	}

}
