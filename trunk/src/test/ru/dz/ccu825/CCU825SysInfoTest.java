package test.ru.dz.ccu825;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.dz.ccu825.payload.CCU825DeviceInfo;
import ru.dz.ccu825.payload.CCU825SysInfo;
import ru.dz.ccu825.util.CCU825PacketFormatException;

public class CCU825SysInfoTest {

	@Test
	public void testCCU825SysInfo() throws CCU825PacketFormatException {
		byte[] pl = new byte[28];
		
		pl[0] = 0x03;

		
		pl[22] = 50;
		
		pl[1] = 0x02;
		pl[18] = 0x03; // out
		

		
		
		CCU825SysInfo si = new CCU825SysInfo(pl);
		
		System.err.println(si);
		
		//assertEquals( si.toString(), "Controller CCU825 modification -SM HW 1 FW 2 (08 Sep 2014) BL 3 language is RUS IMEI=49-015420-323751");
		
		assertEquals(50, si.getBatteryPercentage() );
		
		assertEquals(0x02, si.getInBits() );
		assertEquals(0x03, si.getOutBits() );
	}

}
