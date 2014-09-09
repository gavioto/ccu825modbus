package ru.dz.ccu825.push;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import ru.dz.ccu825.CCU825Connection;
import ru.dz.ccu825.pkt.CCU825OutStateCmdPacket;
import ru.dz.ccu825.util.CCU825ProtocolException;

public class PollOpenHAB {

	private Map<String,PollItemHandler> items = new HashMap<String,PollItemHandler>();
	private String openHABHostName;
	private CCU825Connection conn;

	public PollOpenHAB(String hostName, CCU825Connection conn) {
		openHABHostName = hostName;
		this.conn = conn;
	}
	
	void addItem(String name, PollItemHandler h)
	{
		items.put(name,h);
	}

	
	/**
	 * Polls item and does nothing
	 * @param name item name
	 */
	public void addVoidItem(String name)
	{
		PollItemHandler h = new EmptyPollItemHandler();
		items.put(name,h);
	}
	
	/**
	 * Polls item and sets/resets CCU825 output bit accordingly
	 * @param name
	 * @param nBit
	 */
	public void addBitItem(String name, int nBit)
	{
		if( (nBit > CCU825OutStateCmdPacket.N_OUT_BITS) || (nBit < 0) )
		{
			// TODO err
			return;
		}
		
		PollItemHandler h = new OutBitPollItemHandler(nBit, conn);
		items.put(name,h);
	}
	
	
	
	public void doPoll()
	{
		for( String item : items.keySet() )
		{
			PollItemHandler h = items.get(item);
			String value = getValue(item);

			h.transfer(item,value);
		}
	}

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
			// TODO log
			e.printStackTrace();
			return null;
		}
	}

	private URL makeUrl(String name) throws MalformedURLException 
	{
		return new URL("http", openHABHostName, 8080, String.format("/rest/items/%s/state", name ) );
	}
	
	private String callUrl(URL url) throws IOException {

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


abstract class PollItemHandler
{

	public abstract void transfer(String item, String value);

}

class EmptyPollItemHandler extends PollItemHandler
{
	@Override
	public void transfer(String item, String value) {
		// Ignore		
	}
}


class OutBitPollItemHandler extends PollItemHandler
{
	private final int nOutBit;
	private CCU825Connection conn;

	/**
	 * 
	 * @param nOutBit CCU825 output number (bit pos)
	 */
	public OutBitPollItemHandler(int nOutBit, CCU825Connection conn) {
		this.nOutBit = nOutBit;
		this.conn = conn;
	}
	
	@Override
	public void transfer(String item, String value) {
		try {
			conn.setOutState(nOutBit, value.equalsIgnoreCase("ON"));
		} catch (CCU825ProtocolException e) {
			// TODO log
			e.printStackTrace();
		}		
	}
}



