package test.ru.dz.ccu825;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.dz.ccu825.payload.CCU825DeviceInfo;
import ru.dz.ccu825.util.CCU825PacketFormatException;

public class CCU825DeviceInfoTest {

	private static final String languageTestValue = "RUS";
	private static final String imeiTestValue = "49-015420-323751";
	private static final String deviceModificationTestValue = "-SM";
	private static final String deviceTypeTestValue = "CCU825";
	private static final String dateTestValue = "08 Sep 2014";
	private static final byte[] snTestValue = { 0, 1, 2, 3, 4, 5, 6, 7, 7, 6, 5, 4, 3, 2, 1, 0 };

	@Test
	public void testCCU825DeviceInfo() throws CCU825PacketFormatException {
		
		byte[] pl = new byte[76];
		
		
		pl[0] = 0x02;
		
		fillZeroTerm( pl, 1, 8, deviceTypeTestValue );
		fillZeroTerm( pl, 10, 8, deviceModificationTestValue );
		
		pl[19] = 0x01;
		pl[21] = 0x02;
		pl[23] = 0x03;
		
		fillZeroTerm( pl, 25, 12, dateTestValue ); // TODO date format
		fillZeroTerm( pl, 37, 4, languageTestValue );

		fillZeroTerm( pl, 57, 16, imeiTestValue ); // IMEI

		System.arraycopy(snTestValue, 0, pl, 41, snTestValue.length);
		
		CCU825DeviceInfo di = new CCU825DeviceInfo(pl);
		
		//System.err.println(di);
		
		assertEquals( di.toString(), "Controller CCU825 modification -SM HW 1 FW 2 (08 Sep 2014) BL 3 language is RUS IMEI=49-015420-323751");
		
		assertEquals(0x01, di.getVerHardWare() );
		assertEquals(0x02, di.getVerFirmWare() );
		assertEquals(0x03, di.getVerBootLoader() );
		
		
		assertEquals(languageTestValue, di.getLang());
		assertEquals(imeiTestValue, di.getIMEI());
		assertEquals(dateTestValue, di.getFirmWareBuildDate());
		assertEquals(deviceTypeTestValue, di.getDevType());
		assertEquals(deviceModificationTestValue, di.getDevMod());
		
		assertArrayEquals( snTestValue, di.getSerialNumber() );
		
		
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
