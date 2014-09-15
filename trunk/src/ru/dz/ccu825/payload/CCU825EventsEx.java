package ru.dz.ccu825.payload;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

import ru.dz.ccu825.CCU825Packet;
import ru.dz.ccu825.util.CCU825PacketFormatException;

/**
 * EventsEx packet payload decoder
 * @author dz
 *
 */

public class CCU825EventsEx extends AbstractEvents  {

	public CCU825EventsEx(byte [] in ) throws CCU825PacketFormatException {
		ByteBuffer bb = ByteBuffer.wrap(in);

		bb.order(ByteOrder.LITTLE_ENDIAN);

		if( in[0] != CCU825Packet.PKT_TYPE_EVENTS_EX )
			throw new CCU825PacketFormatException("Wrong Events payload header byte");

		si = new CCU825SysInfoEx(bb);

		nEvents = in[189];
		events = new byte[nEvents];
		System.arraycopy(in, 190, events, 0, nEvents);

	}

	

}
