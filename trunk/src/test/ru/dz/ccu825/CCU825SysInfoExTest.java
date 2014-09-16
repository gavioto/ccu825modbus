package test.ru.dz.ccu825;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.dz.ccu825.payload.CCU825SysInfo;
import ru.dz.ccu825.payload.CCU825SysInfoEx;
import ru.dz.ccu825.payload.ICCU825SysInfo;
import ru.dz.ccu825.util.CCU825PacketFormatException;

public class CCU825SysInfoExTest {

	@Test
	public void testCCU825SysInfoEx() throws CCU825PacketFormatException {
		byte[] pl = new byte[45];
		
		pl[0] = 0x0C;

		
		pl[39] = 50;
		
		pl[1] = 0x02;
		pl[35] = 0x03; // out
		

		// TODO test more state 
		
		ICCU825SysInfo si = new CCU825SysInfoEx(pl);
		
		System.err.println(si);
		
		//assertEquals( si.toString(), "Controller CCU825 modification -SM HW 1 FW 2 (08 Sep 2014) BL 3 language is RUS IMEI=49-015420-323751");
		
		assertEquals(50, si.getBatteryPercentage() );
		
		assertEquals(0x02, si.getInBits() );
		assertEquals(0x03, si.getOutBits() );
	}

}
