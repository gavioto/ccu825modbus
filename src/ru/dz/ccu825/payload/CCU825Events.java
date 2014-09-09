package ru.dz.ccu825.payload;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

import ru.dz.ccu825.CCU825Packet;
import ru.dz.ccu825.util.CCU825PacketFormatException;

/**
 * Events packet payload decoder
 * @author dz
 *
 */

public class CCU825Events implements Iterable<Byte> {

	private final byte nEvents;
	private final byte[] events;

	public CCU825Events(byte [] in ) throws CCU825PacketFormatException {
		ByteBuffer bb = ByteBuffer.wrap(in);

		bb.order(ByteOrder.LITTLE_ENDIAN);

		if( in[0] != CCU825Packet.PKT_TYPE_EVENTS )
			throw new CCU825PacketFormatException("Wrong Events payload header byte");


		nEvents = in[79];
		events = new byte[nEvents];
		System.arraycopy(in, 80, events, 0, nEvents);

	}

	@Override
	public Iterator<Byte> iterator() {
		return new CCU825EventIterator(this);
	}

	private class CCU825EventIterator implements Iterator<Byte>
	{
		private int pos = 0;
		private CCU825Events ev;

		public CCU825EventIterator(CCU825Events ev) {
			this.ev = ev;
		}

		@Override
		public boolean hasNext() {
			return pos < ev.nEvents;
		}

		@Override
		public synchronized Byte next() {
			return ev.events[pos++];
		}

		@Override
		public void remove() {
			throw new RuntimeException("CCU825EventIterator delete() not possible");
		}

	}



	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder(nEvents*20); // estimate size

		Iterator<Byte> i = iterator();
		while(i.hasNext())
		{
			sb.append( eventName(i.next()) );
			
			if(i.hasNext()) sb.append( ", " );
		}

		return sb.toString();
	}

	
	public static String eventName(byte _event)
	{
		int e = _event;
		
		e &= 0xFF; // positive
		
		if( (e >= 0) && (e < 8) )
			return String.format("in %d active", e-0 );

		if( (e >= 8) && (e < 16) )
			return String.format("in %d inactive", e-8 );

		if( (e >= 29) && (e < 37) )
			return String.format("inf. msg in %d", e-29 );
		
		if( (e >= 37) && (e < 44) )
			return String.format("inf. msg out %d", e-37 );

		if( (e >= 74) && (e < 78) )
			return String.format("profile %d", e-74 );
		
		switch(e)
		{
		case 16:	return "Arm";
		case 17:	return "Disarm";
		case 18:	return "Protect";
		case 19:	return "Pwr fail";
		case 20:	return "Pwr ok";
		case 21:	return "Battery discharge 1";
		case 22:	return "Battery discharge 2";
		case 23:	return "GSM balance low";
		case 24:	return "Temp low";
		case 25:	return "Temp ok";
		case 26:	return "Temp hi";
		case 27:	return "Case open";
		case 28:	return "Test event";
		
		}
		
		return String.format("?%02x", e );
	}
	

}
