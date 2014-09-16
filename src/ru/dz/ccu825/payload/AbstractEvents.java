package ru.dz.ccu825.payload;

import java.util.Iterator;

import ru.dz.ccu825.data.ArmModeChange;
import ru.dz.ccu825.data.GuardEvent;


public abstract class AbstractEvents implements Iterable<Byte>, ICCU825Events {

	protected byte nEvents;
	protected byte[] events;
	
	protected ArmModeChange armDetail;
	protected ArmModeChange disarmDetail;
	protected ArmModeChange protectDetail;
	
	protected ICCU825SysInfo si;


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

		sb.append("Last arm    : "+armDetail);
		sb.append("Last disarm : "+disarmDetail);
		sb.append("Last protect: "+protectDetail);
		
		Iterator<Byte> i = iterator();
		while(i.hasNext())
		{
			sb.append( GuardEvent.eventName(i.next()) );
			
			if(i.hasNext()) sb.append( ", " );
		}
		
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