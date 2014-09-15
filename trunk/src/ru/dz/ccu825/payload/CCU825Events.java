package ru.dz.ccu825.payload;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

import ru.dz.ccu825.CCU825Packet;
import ru.dz.ccu825.data.ArmModeChange;
import ru.dz.ccu825.util.CCU825PacketFormatException;

/**
 * Events packet payload decoder
 * @author dz
 *
 */

public class CCU825Events extends AbstractEvents  {

	public CCU825Events(byte [] in ) throws CCU825PacketFormatException {
		ByteBuffer bb = ByteBuffer.wrap(in);

		bb.order(ByteOrder.LITTLE_ENDIAN);

		if( in[0] != CCU825Packet.PKT_TYPE_EVENTS )
			throw new CCU825PacketFormatException("Wrong Events payload header byte");

		si = new CCU825SysInfo(bb);

		armDetail = new ArmModeChange(in, 28);
		disarmDetail = new ArmModeChange(in, 45);
		protectDetail = new ArmModeChange(in, 62);

		nEvents = in[79];
		events = new byte[nEvents];
		System.arraycopy(in, 80, events, 0, nEvents);

	}

	

}
