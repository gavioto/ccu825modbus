package test.ru.dz.ccu825;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.dz.ccu825.payload.CCU825OutState;
import ru.dz.ccu825.util.CCU825PacketFormatException;

public class CCU825OutStateTest {

	@Test
	public void testCCU825OutState() throws CCU825PacketFormatException {
		byte[] pl = new byte[8];
		
		pl[0] = 0x06;

		
		pl[1] = 0x1;
		pl[2] = 0x1;
		pl[3] = 0x1;
		pl[4] = 0x0;
		pl[5] = 0x0;
		pl[6] = 0x1;
		pl[7] = 0x0;

		CCU825OutState os = new CCU825OutState( pl );
		
		//System.err.println(os);
		
		assertEquals( os.toString(), "Out bits 39");
		
		assertEquals( 0x27, os.getOutBits() );
		
		
	}

}
