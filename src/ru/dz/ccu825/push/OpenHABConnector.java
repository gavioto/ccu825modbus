package ru.dz.ccu825.push;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class OpenHABConnector {

	private final static Logger log = Logger.getLogger(OpenHABConnector.class.getName());

	private final String openHABHostName;
	
	
	public OpenHABConnector( String openHABHostName ) {
		this.openHABHostName = openHABHostName;
	}

	public void getItemsList() throws IOException
	{
		URL il = new URL("http", openHABHostName, 8080, "/rest/items");  
		
		//String xr = callUrl(il);
		//System.out.println(xr);
		
		OpenHABItemsInfo ii = new OpenHABItemsInfo();
		try {
			ii.parseItemsXML(il.toString());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Make (an http) call to URL, return answer collected
	 * @param url URL to visit
	 * @return Web page text
	 * @throws IOException
	 */
	public static String callUrl(URL url) throws IOException 
	{
		URLConnection yc = url.openConnection();
		yc.setRequestProperty("Accept", "application/xml");
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
