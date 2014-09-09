package ru.dz.ccu825.push;

public class OpenHabTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		//String tempUrl = "demo.openhab.org:8080/rest/items/Temperature_FF_Office/state";		
		//String switchUrl = "http://demo.openhab.org:8080/rest/items/Heating_FF_Office/state";
		
		String hostName = "demo.openhab.org";
		PollOpenHAB poll = new PollOpenHAB(hostName, null);
		
		poll.addVoidItem("Heating_FF_Office");
		poll.addVoidItem("Temperature_FF_Office");
		poll.addVoidItem("Light_FF_Office_Ceiling");
		
		poll.doPoll();
		
	}

}
