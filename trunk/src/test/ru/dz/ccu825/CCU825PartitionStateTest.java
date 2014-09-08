package test.ru.dz.ccu825;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.dz.ccu825.data.GuardState;
import ru.dz.ccu825.payload.CCU825OutState;
import ru.dz.ccu825.payload.CCU825PartitionState;
import ru.dz.ccu825.util.CCU825PacketFormatException;

public class CCU825PartitionStateTest {

	@Test
	public void testCCU825PartitionState() throws CCU825PacketFormatException {
		byte[] pl = new byte[3];
		
		pl[0] = 0x05;
		pl[1] = 0x6;

		CCU825PartitionState ps = new CCU825PartitionState( pl );
		
		//System.err.println(ps);
		
		assertEquals( ps.toString(), "State p1=Protect p2=Arm p3=Disarm p4=Disarm");
		
		assertEquals( GuardState.Protect, ps.getPartitionState(0) );
		assertEquals( GuardState.Arm, ps.getPartitionState(1) );
		assertEquals( GuardState.Disarm, ps.getPartitionState(2) );
		assertEquals( GuardState.Disarm, ps.getPartitionState(3) );
		
	}

}
