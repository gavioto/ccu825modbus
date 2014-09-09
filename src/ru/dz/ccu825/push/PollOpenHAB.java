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

import ru.dz.ccu825.CCU825Connection;
import ru.dz.ccu825.pkt.CCU825OutStateCmdPacket;

/*
 * poll openHub - possibly use https://github.com/Atmosphere/atmosphere
 */

/**
 * 
 * Poll OpenHAB for item data, translate to CCU825 outputs (or some other place)
 * 
 * @author dz
 *
 */
public class PollOpenHAB {
	private final static Logger log = Logger.getLogger(PollOpenHAB.class.getName());
	
	private Map<String,PollItemHandler> items = new HashMap<String,PollItemHandler>();
	private String openHABHostName;
	private CCU825Connection conn;

	public PollOpenHAB(String hostName, CCU825Connection conn) {
		openHABHostName = hostName;
		this.conn = conn;
	}
	
	/**
	 * Add Item to be polled and processed by given PollItemHandler
	 * @param name item name
	 * @param h handler to pass item value to
	 */
	void addItem(String name, PollItemHandler h)
	{
		items.put(name,h);
	}

	
	/**
	 * Add Item to be polled and written nowhere
	 * @param name item name
	 */
	public void addVoidItem(String name)
	{
		PollItemHandler h = new EmptyPollItemHandler();
		items.put(name,h);
	}
	
	/**
	 * Add (boolean) Item to be polled and written to CCU825 output bit
	 * 
	 * @param name
	 * @param nBit
	 */
	public void addBitItem(String name, int nBit)
	{
		if( (nBit > CCU825OutStateCmdPacket.N_OUT_BITS) || (nBit < 0) )
		{
			log.severe("addBitItem bit n="+nBit);
			return;
		}
		
		PollItemHandler h = new OutBitPollItemHandler(nBit, conn);
		items.put(name,h);
	}
	
	/**
	 * Forget item, don't poll for it any more
	 * @param itemName
	 * @return null or handler that was registered for named item
	 */
	public PollItemHandler removeItem(String itemName)
	{
		return items.remove(itemName);
	}
	
	/**
	 * Poll OpehHAB for all registered items.
	 */
	
	public void doPoll()
	{
		for( String item : items.keySet() )
		{
			PollItemHandler h = items.get(item);
			String value = getValue(item);

			h.transfer(item,value);
		}
	}

	/**
	 * Ask OpenHAB for named item's value
	 * @param item item name
	 * @return item value or null if error occurred
	 */
	private String getValue(String item) {
		try
		{
			URL url = makeUrl(item);
			String val = callUrl(url);
			System.out.println("item "+item+"="+val);
			return val;
		}
		catch(IOException e)
		{
			// e.printStackTrace();
			log.severe(e.getMessage());
			return null;
		}
	}

	/**
	 * Create URL for reading item value
	 * @param name item name
	 * @return URL to connect to and read value
	 * @throws MalformedURLException
	 */
	private URL makeUrl(String name) throws MalformedURLException 
	{
		return new URL("http", openHABHostName, 8080, String.format("/rest/items/%s/state", name ) );
	}
	
	/**
	 * Make (an http) call to URL, return answer collected
	 * @param url URL to visit
	 * @return Web page text
	 * @throws IOException
	 */
	private String callUrl(URL url) throws IOException 
	{
		URLConnection yc = url.openConnection();

		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						yc.getInputStream()));

		StringBuffer bb = new StringBuffer(); 
		{
			String inputLine;

			while ((inputLine = in.readLine()) != null) 
				bb.append(inputLine);
		}

		in.close();

		return bb.toString();
	}



}



