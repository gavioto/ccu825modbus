package test.ru.dz.ccu825;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.dz.ccu825.payload.CCU825DeviceInfo;
import ru.dz.ccu825.util.CCU825PacketFormatException;

public class CCU825DeviceInfoTest {

	@Test
	public void testCCU825DeviceInfo() throws CCU825PacketFormatException {
		
		byte[] pl = new byte[76];
		
		pl[0] = 0x02;
		
		fillZeroTerm( pl, 1, 8, "CCU825" );
		fillZeroTerm( pl, 10, 8, "-SM" );
		
		pl[19] = 0x01;
		pl[21] = 0x02;
		pl[23] = 0x03;
		
		fillZeroTerm( pl, 25, 12, "08 Sep 2014" ); // TODO date format
		fillZeroTerm( pl, 37, 4, "RUS" );

		fillZeroTerm( pl, 57, 16, "49-015420-323751" ); // IMEI

		// TODO S/N
		
		CCU825DeviceInfo di = new CCU825DeviceInfo(pl);
		
		//System.err.println(di);
		
		assertEquals( di.toString(), "Controller CCU825 modification -SM HW 1 FW 2 (08 Sep 2014) BL 3 language is RUS IMEI=49-015420-323751");
		
		assertEquals(0x01, di.getVerHardWare() );
		assertEquals(0x02, di.getVerFirmWare() );
		assertEquals(0x03, di.getVerBootLoader() );
		
		
		assertEquals("RUS", di.getLang());
		assertEquals("49-015420-323751", di.getIMEI());
		assertEquals("08 Sep 2014", di.getFirmWareBuildDate());
		assertEquals("CCU825", di.getDevType());
		assertEquals("-SM", di.getDevMod());
		
		
	}

	private void fillZeroTerm(byte[] pl, int start, int len, String string) {

		for( int i = 0; i < len; i++ )
		{
			if( i >= string.length() )
			{
				pl[i+start] = 0;
			}
			else
			{
				byte b = (byte)string.charAt(i);
				pl[i+start] = b;
			}
		}
		
	}

}
