package ru.dz.ccu825.payload;

import java.util.Iterator;

import ru.dz.ccu825.data.ArmModeChange;


public abstract class AbstractEvents implements Iterable<Byte>, ICCU825Events {

	protected byte nEvents;
	protected byte[] events;
	
	protected ArmModeChange armDetail;
	protected ArmModeChange disarmDetail;
	protected ArmModeChange protectDetail;
	
	protected ICCU825SysInfo si;

	public static String eventName(byte _event) {
		int e = _event;
		
		e &= 0xFF; // positive
		
		if( (e >= 0) && (e < 8) )
			return String.format("in %d active", e-0 );
		if( (e >= 44) && (e < 52) )
			return String.format("in %d active", e-44+8 );
	
	
		if( (e >= 8) && (e < 16) )
			return String.format("in %d inactive", e-8 );
		if( (e >= 52) && (e < 60) )
			return String.format("in %d inactive", e-52+8 );
	
		if( (e >= 29) && (e < 37) )
			return String.format("inf. msg in %d", e-29 );
		if( (e >= 60) && (e < 68) )
			return String.format("inf. msg in %d", e-60+8 );
		
		if( (e >= 37) && (e < 44) )
			return String.format("inf. msg out %d", e-37 );
	
		if( (e >= 74) && (e < 82) )
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

		case 68:	return "Arm 2";
		case 69:	return "Disarm 2";
		case 70:	return "Arm 3";
		case 71:	return "Disarm 3";
		case 72:	return "Arm 4";
		case 73:	return "Disarm 4";
		
		case 82:	return "FW update attempt";
		case 83:	return "GPRS connection requested";

		case 84:	return "Device turned on";
		case 85:	return "Device restarted";
		}
		
		return String.format("?%02x", e );
	}

	/* (non-Javadoc)
	 * @see ru.dz.ccu825.payload.ICCU825Events#iterator()
	 */
	@Override
	public Iterator<Byte> iterator() {
		return new CCU825EventIterator(this);
	}

	/* (non-Javadoc)
	 * @see ru.dz.ccu825.payload.ICCU825Events#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(nEvents*20); // estimate size
	
		Iterator<Byte> i = iterator();
		while(i.hasNext())
		{
			sb.append( eventName(i.next()) );
			
			if(i.hasNext()) sb.append( ", " );
		}
	
		// TODO arm/disarm/protect details
		
		return sb.toString();
	}


	/* (non-Javadoc)
	 * @see ru.dz.ccu825.payload.ICCU825Events#getSysInfo()
	 */
	@Override
	public ICCU825SysInfo getSysInfo() {
		return si;
	}

	
	
	
	class CCU825EventIterator implements Iterator<Byte>
	{
		private int pos = 0;
		private AbstractEvents ev;

		public CCU825EventIterator(AbstractEvents ev) {
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




	public ArmModeChange getArmDetail() {
		return armDetail;
	}

	public ArmModeChange getDisarmDetail() {
		return disarmDetail;
	}

	public ArmModeChange getProtectDetail() {
		return protectDetail;
	}
	
	
	
	
}