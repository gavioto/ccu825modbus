package test.ru.dz.ccu825;

import java.io.IOException;

import org.junit.Test;

import ru.dz.ccu825.push.OpenHABConnector;
import ru.dz.ccu825.push.PollOpenHAB;
import ru.dz.ccu825.push.PushOpenHAB;

/**
 * Test OpenHAB connector by connecting to public OpenHAB instance.
 * 
 * Items:
 * <li> demo.openhab.org:8080/rest/items/Temperature_FF_Office/state
 * <li> demo.openhab.org:8080/rest/items/Heating_FF_Office/state
 * 
 * @author dz
 *
 */
public class OpenHabTest {
	static final String hostName = "demo.openhab.org";

	@Test
	public void testOpenHABConnector() throws IOException 
	{
		OpenHABConnector c = new OpenHABConnector(hostName);
		c.getItemsList();
	}
	
	@Test
	public void testOpenHABPoll() 
	{
		
		PollOpenHAB poll = new PollOpenHAB(hostName, null);
		
		poll.addVoidItem("Heating_FF_Office");
		poll.addVoidItem("Temperature_FF_Office");
		poll.addVoidItem("Light_FF_Office_Ceiling");
		
		poll.doPoll();
		
	}

	@Test
	public void testOpenHABPush() throws IOException 
	{
		
		PushOpenHAB push = new PushOpenHAB(hostName);
		
		push.sendValue("Heating_FF_Office", "ON");
		
		// TODO can we check for success?
	}
	
}
